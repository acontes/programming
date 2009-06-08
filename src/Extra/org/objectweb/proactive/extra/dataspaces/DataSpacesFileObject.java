package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileObject;


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
public interface DataSpacesFileObject extends FileObject {

    /**
     * Returns the file's URI in the Data Spaces virtual file system. It remains valid when passed
     * to active ActiveObject, and hence can be resolved there trough
     * {@link PADataSpaces#resolveFile(String)} method call.
     * 
     * @return URI of a represented file
     */
    public abstract String getURI();
}