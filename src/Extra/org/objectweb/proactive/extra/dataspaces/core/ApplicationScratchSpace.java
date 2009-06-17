/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces.core;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException;


/**
 * Supports scratch data space. Implementations of this interface are thread-safe.
 */
public interface ApplicationScratchSpace {

    /**
     * Returns DataSpacesURI for a specified body of an ActiveObject.
     * <p>
     * First call of this method removes all already existing files in a scratch data space.
     * 
     * @param body
     *            of an ActiveObject
     * @return URI of an ActiveObject's scratch that can be used for resolving files
     * @throws FileSystemException
     *             when any file system related exception occurres during scratch creation (first
     *             method call)
     */
    public DataSpacesURI getScratchForAO(Body body) throws FileSystemException;

    /**
     * Instance stays unchanged during application run.
     * 
     * @return description of a scratch data space
     */
    public SpaceInstanceInfo getSpaceInstanceInfo();

    /**
     * Mounting point instance stays unchanged during application run.
     * 
     * @return URI of a scratch data space's mounting point
     */
    public DataSpacesURI getSpaceMountingPoint();

    /**
     * Removes scratch data space directory content. Any subsequent call to closed instance may have
     * undefined results.
     * 
     * @throws FileSystemException
     *             when any file system related exception occurres
     */
    public void close() throws FileSystemException;
}
