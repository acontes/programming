/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.net.URL;

/**
 * Stores mapping from mounting point URI to access description (like URL, path
 * and hostname) along with mounting point information.
 */
public class SpaceInstanceInfo {

	protected final URL url;

	protected final String path;

	protected final String hostname;

	protected final SpaceURI mountingPoint;

	public SpaceInstanceInfo(SpaceURI mountingPoint, SpaceConfiguration config) {

		if (mountingPoint == null)
			throw new IllegalArgumentException("Mounting point uri is null");

		if (config == null)
			throw new IllegalArgumentException("Space configuration is null");

		this.mountingPoint = mountingPoint;
		this.url = config.getUrl();
		this.hostname = config.getHostname();
		this.path = config.path;
	}

	public URL getUrl() {
		return url;
	}

	public String getPath() {
		return path;
	}

	public String getHostname() {
		return hostname;
	}

	public SpaceURI getMountingPoint() {
		return mountingPoint;
	}

	public String getName() {
		return mountingPoint.getName();
	}

	public SpaceType getType() {
		return mountingPoint.getSpaceType();
	}
}
