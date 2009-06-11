package org.objectweb.proactive.extra.dataspaces.adapter.vfs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.Selectors;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.dataspaces.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.api.DataSpacesFileObject;
import org.objectweb.proactive.extra.dataspaces.api.FileContent;
import org.objectweb.proactive.extra.dataspaces.api.FileSelector;
import org.objectweb.proactive.extra.dataspaces.api.FileSystem;
import org.objectweb.proactive.extra.dataspaces.api.FileType;
import org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException;


public class VFSFileObjectAdapter implements DataSpacesFileObject {

    private final FileObject adaptee;

    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES);

    private final DataSpacesURI spaceUri;

    private final String vfsSpaceRootPath;

    /**
     * Creates an instance of DataSpacesFileObjectImpl. Before any usage of this class, id of an
     * active object has to be set accordingly.
     *
     * @param fileObject
     *            file object that is going to be represented as DataSpacesFileObject; cannot be
     *            <code>null</code>
     * @param spaceUri
     *            Data Spaces URI of this file object's space; cannot be <code>null</code>
     * @param vfsSpaceRootPath
     *            VFS path of the space root FileObject; cannot be <code>null</code>
     */
    public VFSFileObjectAdapter(FileObject adaptee, DataSpacesURI spaceUri, String vfsSpaceRootPath) {
        this.spaceUri = spaceUri;
        this.vfsSpaceRootPath = vfsSpaceRootPath;
        this.adaptee = adaptee;
    }

    private VFSFileObjectAdapter(FileObject adaptee, VFSFileObjectAdapter fileObjectAdapter) {
        this.spaceUri = fileObjectAdapter.spaceUri;
        this.vfsSpaceRootPath = fileObjectAdapter.vfsSpaceRootPath;
        this.adaptee = adaptee;
    }

    //FIXME:
    /*
     * * Returned URI is always suitable for having path: {@link
     * DataSpacesURI#isSuitableForHavingPath()} returns true. <p> FIXME: after getParent()
     * limitation it should be like that?
     */
    public String getURI() {
        // FIXME when DataSpacesFileObject will have just VFS adapter, store full URI as a field instead?
        final String path = adaptee.getName().getPath();
        if (!path.startsWith(vfsSpaceRootPath)) {
            final ProActiveRuntimeException x = new ProActiveRuntimeException(
                "VFS path of this DataSpacesFileObject does not start with its space VFS path");
            ProActiveLogger.logImpossibleException(logger, x);
            throw x;
        }
        final String relPath = path.substring(vfsSpaceRootPath.length());
        return spaceUri.toString() + relPath;
    }

    public void close() throws FileSystemException {
        try {
            adaptee.close();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public void copyFrom(DataSpacesFileObject srcFile, FileSelector selector) throws FileSystemException {
        final FileObject srcAdaptee = getVFSAdapteeOrWound(srcFile);
        final org.apache.commons.vfs.FileSelector vfsSelector = buildFVSSelector(selector);

        try {
            adaptee.copyFrom(srcAdaptee, vfsSelector);
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public void createFile() throws FileSystemException {
        try {
            adaptee.createFile();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public void createFolder() throws FileSystemException {
        try {
            adaptee.createFolder();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public boolean delete() throws FileSystemException {
        try {
            return adaptee.delete();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public int delete(FileSelector selector) throws FileSystemException {
        final org.apache.commons.vfs.FileSelector vfsSelector = buildFVSSelector(selector);

        try {
            return adaptee.delete(vfsSelector);
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public boolean exists() throws FileSystemException {
        try {
            return adaptee.exists();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public List<DataSpacesFileObject> findFiles(FileSelector selector) throws FileSystemException {
        final org.apache.commons.vfs.FileSelector vfsSelector = buildFVSSelector(selector);
        final List<DataSpacesFileObject> result = new ArrayList<DataSpacesFileObject>();

        try {
            final FileObject[] vfsResult = adaptee.findFiles(vfsSelector);

            adaptVFSResult(vfsResult, result);
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
        return result;
    }

    public void findFiles(FileSelector selector, boolean depthwise, List<DataSpacesFileObject> selected)
            throws FileSystemException {

        final org.apache.commons.vfs.FileSelector vfsSelector = buildFVSSelector(selector);

        try {
            final List<FileObject> vfsResult = new ArrayList<FileObject>();
            adaptee.findFiles(vfsSelector, depthwise, vfsResult);

            adaptVFSResult(vfsResult, selected);
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public DataSpacesFileObject getChild(String name) throws FileSystemException {
        try {
            return adaptVFSResult(adaptee.getChild(name));
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public List<DataSpacesFileObject> getChildren() throws FileSystemException {
        List<DataSpacesFileObject> adapted = new ArrayList<DataSpacesFileObject>();

        try {
            adaptVFSResult(adaptee.getChildren(), adapted);
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
        return adapted;
    }

    public FileContent getContent() throws FileSystemException {

        try {
            return new VFSContentAdapter(adaptee.getContent(), this);
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public FileSystem getFileSystem() {
        return new VFSFileSystemAdapter(adaptee.getFileSystem());
    }

    // TODO: check if the parent exists from DS point of veiw..
    public DataSpacesFileObject getParent() throws FileSystemException {
        try {
            final FileObject vfsParent = adaptee.getParent();
            adaptVFSResult(vfsParent);
            return null;
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public FileType getType() throws FileSystemException {
        try {
            return adaptVFSResult(adaptee.getType());
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public boolean isContentOpen() {
        return adaptee.isContentOpen();
    }

    public boolean isHidden() throws FileSystemException {
        try {
            return adaptee.isHidden();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public boolean isReadable() throws FileSystemException {
        try {
            return adaptee.isReadable();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public boolean isWriteable() throws FileSystemException {
        try {
            return adaptee.isWriteable();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public void moveTo(DataSpacesFileObject destFile) throws FileSystemException {
        final FileObject destAdaptee = getVFSAdapteeOrWound(destFile);

        try {
            adaptee.moveTo(destAdaptee);
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    private FileObject getVFSAdapteeOrWound(DataSpacesFileObject file) throws FileSystemException {

        if (file instanceof VFSFileObjectAdapter) {
            return ((VFSFileObjectAdapter) file).getAdaptee();
        }
        throw new FileSystemException("Operation unsupported: destination file system unknown");
    }

    public void refresh() throws FileSystemException {
        try {
            adaptee.refresh();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    // FIXME check if path is reasonable in sense of DS
    public DataSpacesFileObject resolveFile(String path) throws FileSystemException {
        try {
            return adaptVFSResult(adaptee.resolveFile(path));
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public FileObject getAdaptee() {
        return adaptee;
    }

    /**
     * @param array
     *            may be null
     * @param adapted
     *            may be null only if <code>array</code> is
     *
     */
    private <T extends Collection<DataSpacesFileObject>> void adaptVFSResult(FileObject[] array,
            T adapted) {
        if (array == null)
            return;
        for (int i = 0; i < array.length; i++) {
            adapted.add(new VFSFileObjectAdapter(array[i], this));
        }
    }

    /**
     * @param vfsResult
     *            cannot be null
     * @param adapted
     *            cannot be null
     */
    private <T extends Collection<DataSpacesFileObject>, E extends Collection<FileObject>> void adaptVFSResult(
            E vfsResult, T adapted) {

        for (FileObject fo : vfsResult) {
            adapted.add(new VFSFileObjectAdapter(fo, this));
        }
    }

    /**
     * @param vfsFileObject
     *            may be null
     */
    private VFSFileObjectAdapter adaptVFSResult(final FileObject vfsFileObject) {
        return vfsFileObject == null ? null : new VFSFileObjectAdapter(vfsFileObject, this);
    }

    private static FileType adaptVFSResult(org.apache.commons.vfs.FileType vfsResult) {
        if (vfsResult == org.apache.commons.vfs.FileType.FILE)
            return FileType.FILE;

        if (vfsResult == org.apache.commons.vfs.FileType.FOLDER)
            return FileType.FOLDER;

        if (vfsResult == org.apache.commons.vfs.FileType.IMAGINARY)
            return FileType.ABSTRACT;

        return null;
    }

    /**
     * @param selector
     *            may be null
     */
    private static org.apache.commons.vfs.FileSelector buildFVSSelector(FileSelector selector) {
        switch (selector) {
            case EXCLUDE_SELF:
                return Selectors.EXCLUDE_SELF;
            case SELECT_ALL:
                return Selectors.SELECT_ALL;
            case SELECT_FILES:
                return Selectors.SELECT_FILES;
            case SELECT_FOLDERS:
                return Selectors.SELECT_FOLDERS;
            case SELECT_CHILDREN:
                return Selectors.SELECT_CHILDREN;
            case SELECT_SELF:
                return Selectors.SELECT_SELF;
            case SELECT_SELF_AND_CHILDREN:
                return Selectors.SELECT_SELF_AND_CHILDREN;
            default:
                return null;
        }
    }
}
