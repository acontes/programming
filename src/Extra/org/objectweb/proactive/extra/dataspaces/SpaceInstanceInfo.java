/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.io.Serializable;
import java.net.URL;

/**
 * Stores mapping from mounting point URI to access description (like URL, path
 * and hostname) along with mounting point information.
 */
public class SpaceInstanceInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7402632454423044845L;

	protected final URL url;

	protected final String path;

	protected final String hostname;

	protected final DataSpacesURI mountingPoint;

	public SpaceInstanceInfo(DataSpacesURI mountingPoint, SpaceConfiguration config) {

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

	public DataSpacesURI getMountingPoint() {
		return mountingPoint;
	}

	public String getName() {
		return mountingPoint.getName();
	}

	public SpaceType getType() {
		return mountingPoint.getSpaceType();
	}

	public long getAppId() {
		return mountingPoint.getAppId();
	}
}
