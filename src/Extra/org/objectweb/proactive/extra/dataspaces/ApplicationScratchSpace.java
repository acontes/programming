/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

/**
 * - returns scratch data space for AO (in form od DataSpacesURI) (from AO id)
 * creates it when needed - (to discuss) registers data space instance in
 * CachingSpacesDirectory on creation; unregisters and removes directory on
 * finalization
 * 
 */
public interface ApplicationScratchSpace {

	public DataSpacesURI getScratchForAO(String aoid);

	/**
	 * TODO stays unchanged during application run? (hence can be called only
	 * once)
	 * 
	 * @return
	 */
	public SpaceInstanceInfo getSpaceInstanceInfo();

	/**
	 * removes scratch data space directory content
	 */
	public void close();
}
