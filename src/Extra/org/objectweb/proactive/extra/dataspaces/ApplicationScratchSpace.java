/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileSystemException;
import org.objectweb.proactive.Body;


/**
 * Supports scratch data space. Implementations of this interface are thread-safe.
 */
public interface ApplicationScratchSpace {

    /**
     * Returns DataSpacesURI for a specified body of an ActiveObject.
     * 
     * First call of this method removes all already existing files in a scratch data space.
     * 
     * @param body
     * @return
     * @throws FileSystemException
     */
    public DataSpacesURI getScratchForAO(Body body) throws FileSystemException;

    /**
     * Instance stays unchanged during application run.
     * 
     * @return
     */
    public SpaceInstanceInfo getSpaceInstanceInfo();

    /**
     * Mounting point stays unchanged during application run.
     * 
     * @return
     */
    public DataSpacesURI getSpaceMountingPoint();

    /**
     * Removes scratch data space directory content. Any subsequent call to closed instance may have
     * undefined results.
     * 
     * @throws FileSystemException
     */
    public void close() throws FileSystemException;
}
