/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

/**
 * Stores information needed to configure an instance of a data space.
 */
public class SpaceConfiguration {

	// TODO final?
	protected String url;
	protected String path;
	protected String hostname;
	protected SpaceType spaceType;
	protected String name;

	// TODO change it, check semantics?
	public SpaceConfiguration(String url, String path, String hostname, SpaceType spaceType, String name) {
		this.url = url;
		this.path = path;
		this.hostname = hostname;
		this.spaceType = spaceType;
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public String getPath() {
		return path;
	}

	public String getHostname() {
		return hostname;
	}

	public SpaceType getType() {
		return spaceType;
	}

	public String getName() {
		return name;
	}
}
