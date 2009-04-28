/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileSystemException;
import org.objectweb.proactive.Body;


/**
 * - returns scratch data space for AO (in form od DataSpacesURI) (from AO id) creates it when
 * needed - (to discuss) registers data space instance in CachingSpacesDirectory on creation;
 * unregisters and removes directory on finalization
 * 
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
     * removes scratch data space directory content
     * 
     * @throws FileSystemException
     */
    public void close() throws FileSystemException;
}
