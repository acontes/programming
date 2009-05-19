package org.objectweb.proactive.extra.dataspaces;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileContentInfo;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.NameScope;
import org.apache.commons.vfs.RandomAccessContent;
import org.apache.commons.vfs.impl.DecoratedFileObject;
import org.apache.commons.vfs.util.RandomAccessMode;


/**
 * Abstract FileObject decorator, trying to limit any write access to the file basing on pluggable
 * rule.
 * <p>
 * Decorator limits direct write access (like deleting file, opening output stream from
 * getContent()), write checks (like isWriteable()), but also decorates every returned FileObject.
 * Way of decorating returned files is also pluggable.
 * <p>
 * Implementors should provide {@link #isReadOnly()} and {@link #doDecorateFile(FileObject)}
 * methods.
 * <p>
 * <strong>Known limitations:</strong>
 * <ul>
 * <li>canRenameTo() invoked on non-decorated object with decorated object as target may return
 * false information</li>
 * <li>returned FileContent returns undecorated file for getFile(); depends on VFS bug: VFS-259</li>
 * <li>moveTo() invoked on non-decorated object with decorated object as target may not work for
 * some buggy providers; depends on VFS bug: VFS-258</li>
 * </ul>
 */
public abstract class BaseWriteLimitingFileObject extends DecoratedFileObject {
    public BaseWriteLimitingFileObject(final FileObject decoratedFileObject) {
        super(decoratedFileObject);
    }

    /**
     * @return <code>true</code> if file is read-only, <code>false</code> otherwise
     */
    protected abstract boolean isReadOnly();

    /**
     * @param file
     *            file to decorate for calls returning FileObjects; never <code>null</code>
     * @return decorated FileObject
     */
    protected abstract FileObject doDecorateFile(FileObject file);

    private void checkIsNotReadOnly() throws FileSystemException {
        if (isReadOnly())
            throw new FileSystemException("File is read-only");
    }

    @Override
    public boolean canRenameTo(FileObject newfile) {
        return !isReadOnly();
    }

    @Override
    public boolean isWriteable() throws FileSystemException {
        return !isReadOnly();
    }

    @Override
    public void copyFrom(FileObject srcFile, FileSelector selector) throws FileSystemException {
        checkIsNotReadOnly();
        super.copyFrom(srcFile, selector);
    }

    @Override
    public void createFile() throws FileSystemException {
        checkIsNotReadOnly();
        super.createFile();
    }

    @Override
    public void createFolder() throws FileSystemException {
        checkIsNotReadOnly();
        super.createFolder();
    }

    @Override
    public boolean delete() throws FileSystemException {
        checkIsNotReadOnly();
        return super.delete();
    }

    @Override
    public int delete(FileSelector selector) throws FileSystemException {
        checkIsNotReadOnly();
        return super.delete(selector);
    }

    @Override
    public void moveTo(FileObject destFile) throws FileSystemException {
        checkIsNotReadOnly();
        super.moveTo(destFile);
    }

    @Override
    public FileObject resolveFile(String name, NameScope scope) throws FileSystemException {
        return decorateFile(super.resolveFile(name, scope));
    }

    @Override
    public FileObject resolveFile(String path) throws FileSystemException {
        return decorateFile(super.resolveFile(path));
    }

    @Override
    public FileObject[] findFiles(FileSelector selector) throws FileSystemException {
        return decorateFiles(super.findFiles(selector));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void findFiles(FileSelector selector, boolean depthwise, List selected) throws FileSystemException {
        final List<FileObject> selectedList = new ArrayList<FileObject>();
        super.findFiles(selector, depthwise, selectedList);
        selected.addAll(decorateFiles(selectedList));
    }

    @Override
    public FileObject getChild(String name) throws FileSystemException {
        final FileObject child = super.getChild(name);
        if (child == null)
            return null;
        return decorateFile(child);
    }

    @Override
    public FileObject[] getChildren() throws FileSystemException {
        return decorateFiles(super.getChildren());
    }

    @Override
    public FileObject getParent() throws FileSystemException {
        return decorateFile(super.getParent());
    }

    @Override
    public FileContent getContent() throws FileSystemException {
        return new LimitingFileContent(super.getContent());
    }

    private FileObject decorateFile(final FileObject file) {
        if (file == null)
            return null;
        return doDecorateFile(file);
    }

    private FileObject[] decorateFiles(final FileObject files[]) throws FileSystemException {
        if (files == null)
            return null;

        final FileObject result[] = new FileObject[files.length];
        for (int i = 0; i < files.length; i++)
            result[i] = decorateFile(files[i]);
        return result;
    }

    private List<FileObject> decorateFiles(final List<FileObject> files) throws FileSystemException {
        final List<FileObject> result = new ArrayList<FileObject>(files.size());
        for (final FileObject fo : files)
            result.add(decorateFile(fo));
        return result;
    }

    private class LimitingFileContent implements FileContent {
        private FileContent content;

        public LimitingFileContent(FileContent content) {
            this.content = content;
        }

        public void close() throws FileSystemException {
            content.close();
        }

        public Object getAttribute(String attrName) throws FileSystemException {
            return content.getAttribute(attrName);
        }

        public String[] getAttributeNames() throws FileSystemException {
            return content.getAttributeNames();
        }

        @SuppressWarnings("unchecked")
        public Map getAttributes() throws FileSystemException {
            return content.getAttributes();
        }

        public Certificate[] getCertificates() throws FileSystemException {
            return content.getCertificates();
        }

        public FileContentInfo getContentInfo() throws FileSystemException {
            return content.getContentInfo();
        }

        public FileObject getFile() {
            // FIXME: we should return decorated file object, but it would break some down-casting in
            // providers implementations (see HttpFileContentInfoFactory and WebdavFileContentInfoFactory).
            // They would require change to use something like FileObjectUtils.getAbstractFileObject()
            // instead of casting. Patch proposed, depends on VFS-259
            // return decorateFile(content.getFile()); 
            return content.getFile();
        }

        public InputStream getInputStream() throws FileSystemException {
            return content.getInputStream();
        }

        public long getLastModifiedTime() throws FileSystemException {
            return content.getLastModifiedTime();
        }

        public OutputStream getOutputStream() throws FileSystemException {
            checkIsNotReadOnly();
            return content.getOutputStream();
        }

        public OutputStream getOutputStream(boolean append) throws FileSystemException {
            checkIsNotReadOnly();
            return content.getOutputStream(append);
        }

        public RandomAccessContent getRandomAccessContent(RandomAccessMode mode) throws FileSystemException {
            if (mode.requestWrite())
                checkIsNotReadOnly();
            return content.getRandomAccessContent(mode);
        }

        public long getSize() throws FileSystemException {
            return content.getSize();
        }

        public boolean hasAttribute(String attrName) throws FileSystemException {
            return content.hasAttribute(attrName);
        }

        public boolean isOpen() {
            return content.isOpen();
        }

        public void removeAttribute(String attrName) throws FileSystemException {
            checkIsNotReadOnly();
            content.removeAttribute(attrName);
        }

        public void setAttribute(String attrName, Object value) throws FileSystemException {
            checkIsNotReadOnly();
            content.setAttribute(attrName, value);
        }

        public void setLastModifiedTime(long modTime) throws FileSystemException {
            checkIsNotReadOnly();
            content.setLastModifiedTime(modTime);
        }
    }
}
