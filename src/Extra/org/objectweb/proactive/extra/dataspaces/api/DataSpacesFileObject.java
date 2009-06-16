package org.objectweb.proactive.extra.dataspaces.api;

import java.util.List;

import org.objectweb.proactive.extra.dataspaces.PADataSpaces;
import org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException;


/**
 * Instances of this interface represent files within the Data Spaces framework and allows to
 * perform context specific file system operations and file access.
 * <p>
 * Instances of this interface are to be returned by resolve* methods from {@link PADataSpaces}
 * class, and therefore refer to its documentation.
 * <p>
 * Some operations may be limited according to the caller's context and granted privileges, see
 * {@link PADataSpaces} documentation for the details.
 */
public interface DataSpacesFileObject {

    /**
     * Returns the file's URI in the Data Spaces virtual file system. It remains valid when passed
     * to active ActiveObject, and hence can be resolved there trough
     * {@link PADataSpaces#resolveFile(String)} method call.
     * 
     * @return URI of a represented file without the trailing slash
     * @throws FileSystemException
     */
    public abstract String getURI() throws FileSystemException;

    /**
     * Determines if this file exists.
     *
     * @return <code>true</code> if this file exists, <code>false</code> if not.
     * @throws FileSystemException
     *             On error determining if this file exists.
     */
    public abstract boolean exists() throws FileSystemException;

    /**
     * Determines if this file is hidden.
     *
     * @return <code>true</code> if this file is hidden, <code>false</code> if not.
     * @throws FileSystemException
     *             On error determining if this file exists.
     */
    public abstract boolean isHidden() throws FileSystemException;

    /**
     * Determines if this file can be read.
     *
     * @return <code>true</code> if this file is readable, <code>false</code> if not.
     * @throws FileSystemException
     *             On error determining if this file exists.
     */
    public abstract boolean isReadable() throws FileSystemException;

    /**
     * Determines if this file can be written to.
     *
     * @return <code>true</code> if this file is writeable, <code>false</code> if not.
     * @throws FileSystemException
     *             On error determining if this file exists.
     */
    public abstract boolean isWriteable() throws FileSystemException;

    /**
     * Returns this file's type.
     *
     * @return One of the {@link FileType} enums. Never returns null.
     * @throws FileSystemException
     *             On error determining the file's type.
     */
    public abstract FileType getType() throws FileSystemException;

    /**
     * Determines if this file's data space has a particular capability.
     *
     * @param capability
     *            The capability to check for.
     * @return true if this file's data space has the requested capability.
     * @todo Move this to another interface, so that set of capabilities can be queried.
     */
    public abstract boolean hasSpaceCapability(Capability capability);

    /**
     * Returns the folder that contains this file.
     *
     * @return The folder that contains this file. Returns null if this file is the root of a file
     *         system.
     * @throws FileSystemException
     *             On error finding the file's parent.
     */
    public abstract DataSpacesFileObject getParent() throws FileSystemException;

    /**
     * Lists the children of this file.
     *
     * @return An array containing the children of this file. The array is unordered. If the file
     *         does not have any children, a zero-length array is returned. This method never
     *         returns null.
     * @throws FileSystemException
     *             If this file does not exist, or is not a folder, or on error listing this file's
     *             children.
     */
    public abstract List<DataSpacesFileObject> getChildren() throws FileSystemException;

    /**
     * Returns a child of this file. Note that this method returns <code>null</code> when the child
     * does not exist.
     *
     * @param name
     *            The name of the child.
     * @return The child, or null if there is no such child.
     * @throws FileSystemException
     *             If this file does not exist, or is not a folder, or on error determining this
     *             file's children.
     */
    public abstract DataSpacesFileObject getChild(String name) throws FileSystemException;

    /**
     * Finds a file, relative to this file. Equivalent to calling
     * <code>resolveFile( path, NameScope.FILE_SYSTEM )</code>.
     *
     * @param path
     *            The path of the file to locate. Can either be a relative path or an absolute path.
     * @return The file.
     * @throws FileSystemException
     *             On error parsing the path, or on error finding the file.
     */
    public abstract DataSpacesFileObject resolveFile(String path) throws FileSystemException;

    /**
     * Finds the set of matching descendents of this file, in depthwise order.
     *
     * @param selector
     *            The selector to use to select matching files.
     * @return The matching files. The files are returned in depthwise order (that is, a child
     *         appears in the list before its parent). Is never <code>null</code> but may represent
     *         an empty list in some cases (e.g. the file does not exist).
     */
    public abstract List<DataSpacesFileObject> findFiles(FileSelector selector) throws FileSystemException;

    /**
     * Finds the set of matching descendents of this file.
     *
     * @param selector
     *            the selector used to determine if the file should be selected
     * @param depthwise
     *            controls the ordering in the list. e.g. deepest first
     * @param selected
     *            container for selected files. list needs not to be empty.
     * @throws FileSystemException
     */
    public abstract void findFiles(FileSelector selector, boolean depthwise,
            List<DataSpacesFileObject> selected) throws FileSystemException;

    /**
     * Deletes this file. Does nothing if this file does not exist of if it is a folder that has
     * children. Does not delete any descendents of this file, use {@link #delete(FileSelector)} for
     * that.
     *
     * @return true if this object has been deleted
     * @throws FileSystemException
     *             If this file is a non-empty folder, or if this file is read-only, or on error
     *             deleteing this file.
     */
    public abstract boolean delete() throws FileSystemException;

    /**
     * Deletes all descendents of this file that match a selector. Does nothing if this file does
     * not exist.
     * <p/>
     * <p>
     * This method is not transactional. If it fails and throws an exception, this file will
     * potentially only be partially deleted.
     *
     * @param selector
     *            The selector to use to select which files to delete.
     * @return the number of deleted objects
     * @throws FileSystemException
     *             If this file or one of its descendents is read-only, or on error deleting this
     *             file or one of its descendents.
     */
    public abstract int delete(FileSelector selector) throws FileSystemException;

    /**
     * Creates this folder, if it does not exist. Also creates any ancestor folders which do not
     * exist. This method does nothing if the folder already exists.
     *
     * @throws FileSystemException
     *             If the folder already exists with the wrong type, or the parent folder is
     *             read-only, or on error creating this folder or one of its ancestors.
     */
    public abstract void createFolder() throws FileSystemException;

    /**
     * Creates this file, if it does not exist. Also creates any ancestor folders which do not
     * exist. This method does nothing if the file already exists and is a file.
     *
     * @throws FileSystemException
     *             If the file already exists with the wrong type, or the parent folder is
     *             read-only, or on error creating this file or one of its ancestors.
     */
    public abstract void createFile() throws FileSystemException;

    /**
     * Copies another file, and all its descendents, to this file.
     * <p/>
     * If this file does not exist, it is created. Its parent folder is also created, if necessary.
     * If this file does exist, it is deleted first.
     * <p/>
     * <p>
     * This method is not transactional. If it fails and throws an exception, this file will
     * potentially only be partially copied.
     *
     * @param srcFile
     *            The source file to copy.
     * @param selector
     *            The selector to use to select which files to copy.
     * @throws FileSystemException
     *             If this file is read-only, or if the source file does not exist, or on error
     *             copying the file.
     */
    public abstract void copyFrom(DataSpacesFileObject srcFile, FileSelector selector)
            throws FileSystemException;

    /**
     * Move this file.
     * <p>
     * If the destFile exists, it is deleted first</b>
     *
     * @param destFile
     *            the New filename.
     * @throws FileSystemException
     *             If this file is read-only, or if the source file does not exist, or on error
     *             copying the file.
     */
    public abstract void moveTo(DataSpacesFileObject destFile) throws FileSystemException;

    /**
    * Returns this file's content. The {@link FileContent} returned by this method can be used to
    * read and write the content of the file.
    * <p/>
    * <p>
    * This method can be called if the file does not exist, and the returned {@link FileContent}
    * can be used to create the file by writing its content.
    *
    * @return This file's content.
    * @throws FileSystemException
    *             On error getting this file's content.
    */
    public abstract FileContent getContent() throws FileSystemException;

    /**
     * Closes this file, and its content. This method is a hint to the implementation that it can
     * release any resources associated with the file.
     * <p/>
     * <p>
     * The file object can continue to be used after this method is called.
     *
     * @throws FileSystemException
     *             On error closing the file.
     * @see FileContent#close
     */
    public abstract void close() throws FileSystemException;

    /**
     * This will prepare the fileObject to get resynchronized with the underlaying filesystem if
     * required
     */
    public abstract void refresh() throws FileSystemException;

    /**
     * check if someone reads/write to this file
     */
    public abstract boolean isContentOpen();
}