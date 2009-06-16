/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces.core;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException;


/**
 * Supports scratch data space. Implementations of this interface are thread-safe.
 */
// TODO improve/complete docs
public interface ApplicationScratchSpace {

    /**
     * Returns DataSpacesURI for a specified body of an ActiveObject.
     * 
     * First call of this method removes all already existing files in a scratch data space.
     * <p>
     * FIXME: is it really true (here) ??
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
