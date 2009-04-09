/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

/**
 * 	- returns scratch data space for AO (in form od SpaceURI) (from AO id)
	  creates it when needed
	- (to discuss) registers data space instance in CachingSpacesDirectory
	  on creation; unregisters and removes directory on finalization
 *
 */
public interface ApplicationScratchSpace {

    public SpaceFileURI getScratchForAO(String aoid);

    public SpaceInstanceInfo getSpaceInstanceInfo();

    public void close();
}
