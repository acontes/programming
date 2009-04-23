/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

/**
 * Stores information needed to configure an instance of a data space.
 */
public abstract class SpaceConfiguration {

	protected final String path;

	protected final SpaceType spaceType;

	protected SpaceConfiguration(String path, SpaceType spaceType) {
		this.path = path;
		this.spaceType = spaceType;
	}

	public abstract String getUrl();

	public String getPath() {
		return path;
	}

	public abstract String getHostname();

	public SpaceType getType() {
		return spaceType;
	}
}
