package org.objectweb.proactive.extensions.dataspaces.vfs.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.NameScope;
import org.apache.commons.vfs.Selectors;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.dataspaces.api.Capability;
import org.objectweb.proactive.extensions.dataspaces.api.DataSpacesFileObject;
import org.objectweb.proactive.extensions.dataspaces.api.FileContent;
import org.objectweb.proactive.extensions.dataspaces.api.FileSelector;
import org.objectweb.proactive.extensions.dataspaces.api.FileType;
import org.objectweb.proactive.extensions.dataspaces.core.DataSpacesURI;
import org.objectweb.proactive.extensions.dataspaces.exceptions.FileSystemException;


/**
 * VFS {@link FileObject} adapter to {@link DataSpacesFileObject} interface, adding getURI
 * functionality.
 * <p>
 * Adapted FileObject should provide any access limitation as required by Data Spaces specification.
 */
public class VFSFileObjectAdapter implements DataSpacesFileObject {
    private final static Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES);

    private final FileObject adaptee;

    private final DataSpacesURI dataSpaceURI;

    private FileName dataSpaceVFSFileName;

    /**
     * @param adaptee
     *            file object that is going to be represented as DataSpacesFileObject; cannot be
     *            <code>null</code>
     * @param dataSpaceURI
     *            Data Spaces URI of this file object's space; must have space part fully defined
     *            and only this part; cannot be <code>null</code>
     * @param dataSpaceVFSFileName
     *            VFS file name of the space root FileObject; cannot be <code>null</code>
     * @throws FileSystemException
     *             when data space file name does not match adaptee's name
     */
    public VFSFileObjectAdapter(FileObject adaptee, DataSpacesURI dataSpaceURI, FileName dataSpaceVFSFileName)
            throws FileSystemException {
        this.dataSpaceURI = dataSpaceURI;
        this.dataSpaceVFSFileName = dataSpaceVFSFileName;
        this.adaptee = adaptee;
        checkFileNamesConsistencyOrWound();
    }

    private VFSFileObjectAdapter(FileObject adaptee, VFSFileObjectAdapter fileObjectAdapter)
            throws FileSystemException {
        this(adaptee, fileObjectAdapter.dataSpaceURI, fileObjectAdapter.dataSpaceVFSFileName);
    }

    public String getURI() {
        String relativePath;
        try {
            relativePath = dataSpaceVFSFileName.getRelativeName(adaptee.getName());
        } catch (org.apache.commons.vfs.FileSystemException e) {
            ProActiveLogger.logImpossibleException(logger, e);
            throw new ProActiveRuntimeException(e);
        }
        if (".".equals(relativePath))
            relativePath = null;

        return dataSpaceURI.withRelativeToSpace(relativePath).toString();
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

    public DataSpacesFileObject getParent() throws FileSystemException {
        final FileObject vfsParent;
        try {
            vfsParent = adaptee.getParent();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }

        if (vfsParent == null)
            throw new FileSystemException("Operation cannot be performed due to file system limitations");
        return adaptVFSResult(vfsParent);
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

    public boolean isWritable() throws FileSystemException {
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

    public void refresh() throws FileSystemException {
        try {
            adaptee.refresh();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public DataSpacesFileObject resolveFile(String path) throws FileSystemException {
        if (path.startsWith("/"))
            throw new FileSystemException("Cannot resolve an absolute path");
        try {
            return adaptVFSResult(adaptee.resolveFile(path));
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public FileObject getAdaptee() {
        return adaptee;
    }

    public boolean hasSpaceCapability(Capability capability) {
        final org.apache.commons.vfs.Capability vfsCapability = buildVFSCapability(capability);
        final org.apache.commons.vfs.FileSystem vfsFileSystem = adaptee.getFileSystem();

        return vfsFileSystem.hasCapability(vfsCapability);
    }

    /**
     * @param array
     *            may be null
     * @param adapted
     *            may be null only if <code>array</code> is
     * @throws FileSystemException
     *
     */
    private <T extends Collection<DataSpacesFileObject>> void adaptVFSResult(FileObject[] array, T adapted)
            throws FileSystemException {
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
     * @throws FileSystemException
     */
    private <T extends Collection<DataSpacesFileObject>, E extends Collection<FileObject>> void adaptVFSResult(
            E vfsResult, T adapted) throws FileSystemException {

        for (FileObject fo : vfsResult) {
            adapted.add(new VFSFileObjectAdapter(fo, this));
        }
    }

    /**
     * @param vfsFileObject
     *            may be null
     * @throws FileSystemException
     */
    private VFSFileObjectAdapter adaptVFSResult(final FileObject vfsFileObject) throws FileSystemException {
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

    /**
     * @param capability
     *            may be null
     */
    private static org.apache.commons.vfs.Capability buildVFSCapability(Capability capability) {
        switch (capability) {
            case APPEND_CONTENT:
                return org.apache.commons.vfs.Capability.APPEND_CONTENT;
            case ATTRIBUTES:
                return org.apache.commons.vfs.Capability.ATTRIBUTES;
            case COMPRESS:
                return org.apache.commons.vfs.Capability.COMPRESS;
            case CREATE:
                return org.apache.commons.vfs.Capability.CREATE;
            case DELETE:
                return org.apache.commons.vfs.Capability.DELETE;
            case DIRECTORY_READ_CONTENT:
                return org.apache.commons.vfs.Capability.DIRECTORY_READ_CONTENT;
            case FS_ATTRIBUTES:
                return org.apache.commons.vfs.Capability.FS_ATTRIBUTES;
            case GET_LAST_MODIFIED:
                return org.apache.commons.vfs.Capability.GET_LAST_MODIFIED;
            case GET_TYPE:
                return org.apache.commons.vfs.Capability.GET_TYPE;
            case LAST_MODIFIED:
                return org.apache.commons.vfs.Capability.LAST_MODIFIED;
            case LIST_CHILDREN:
                return org.apache.commons.vfs.Capability.LIST_CHILDREN;
            case MANIFEST_ATTRIBUTES:
                return org.apache.commons.vfs.Capability.MANIFEST_ATTRIBUTES;
            case RANDOM_ACCESS_READ:
                return org.apache.commons.vfs.Capability.RANDOM_ACCESS_READ;
            case RANDOM_ACCESS_WRITE:
                return org.apache.commons.vfs.Capability.RANDOM_ACCESS_WRITE;
            case READ_CONTENT:
                return org.apache.commons.vfs.Capability.READ_CONTENT;
            case RENAME:
                return org.apache.commons.vfs.Capability.RENAME;
            case SET_LAST_MODIFIED_FILE:
                return org.apache.commons.vfs.Capability.SET_LAST_MODIFIED_FILE;
            case SET_LAST_MODIFIED_FOLDER:
                return org.apache.commons.vfs.Capability.SET_LAST_MODIFIED_FOLDER;
            case SIGNING:
                return org.apache.commons.vfs.Capability.SIGNING;
            case URI:
                return org.apache.commons.vfs.Capability.URI;
            case VIRTUAL:
                return org.apache.commons.vfs.Capability.VIRTUAL;
            case WRITE_CONTENT:
                return org.apache.commons.vfs.Capability.WRITE_CONTENT;
            default:
                return null;
        }
    }

    private void checkFileNamesConsistencyOrWound() throws FileSystemException {
        final FileName adapteeName = adaptee.getName();

        if (!dataSpaceVFSFileName.isDescendent(adapteeName, NameScope.DESCENDENT_OR_SELF))
            throw new FileSystemException("Specified data space file name does not match adaptee's name");
    }

    private FileObject getVFSAdapteeOrWound(DataSpacesFileObject file) throws FileSystemException {

        if (file instanceof VFSFileObjectAdapter) {
            return ((VFSFileObjectAdapter) file).getAdaptee();
        }
        throw new FileSystemException("Operation unsupported: destination file system unknown");
    }

    public String getURL() {
        try {
            return adaptee.getURL().toExternalForm();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object candidate) {

        if (!(candidate instanceof DataSpacesFileObject))
            return false;

        final DataSpacesFileObject file = (DataSpacesFileObject) candidate;
        return this.getURI().equals(file.getURI());
    }
}
