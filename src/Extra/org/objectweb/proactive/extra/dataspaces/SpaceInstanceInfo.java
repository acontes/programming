/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.io.Serializable;

/**
 * Stores mapping from mounting point URI to access description (like URL, path
 * and hostname) along with mounting point information.
 */
public class SpaceInstanceInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7402632454423044845L;

	protected final String url;

	protected final String path;

	protected final String hostname;

	protected final DataSpacesURI mountingPoint;

	/**
	 * Creates SpaceInstanceInfo for scratch data space.
	 * 
	 * @param appid
	 *            application identifier
	 * @param runtimeId
	 *            runtime identifier
	 * @param nodeId
	 *            node identifier
	 * @param config
	 *            scratch data space configuration
	 */
	public SpaceInstanceInfo(long appid, String runtimeId, String nodeId, SpaceConfiguration config) {

		if (runtimeId == null || nodeId == null || config == null)
			throw new IllegalArgumentException("Configuration can not be null");

		if (config.getType() != SpaceType.SCRATCH)
			throw new IllegalArgumentException("This constructor must be used for scratch data space.");

		this.mountingPoint = DataSpacesURI.createScratchSpaceURI(appid, runtimeId, nodeId);
		this.url = config.getUrl();
		this.hostname = config.getHostname();
		this.path = config.path;
	}

	/**
	 * Creates SpaceInstanceInfo for input/output data space.
	 * 
	 * @param appid
	 *            application id
	 * @param config
	 *            input or output data space configuration
	 */
	public SpaceInstanceInfo(long appid, SpaceConfiguration config) {

		if (config == null)
			throw new IllegalArgumentException("Space configuration is null");

		if (config.getType() == SpaceType.SCRATCH)
			throw new IllegalArgumentException("This constructor cannot be used for scratch data space.");

		this.mountingPoint = DataSpacesURI.createInOutSpaceURI(appid, config.getType(), config.getName());
		this.url = config.getUrl();
		this.hostname = config.getHostname();
		this.path = config.path;
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
