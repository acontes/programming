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
	 * @param nodeId
	 * @param runtimeId
	 * @param config
	 *            valid scratch data space configuration (@see
	 *            {@link DataSpacesURI#createScratchSpaceURI(long, String, String)}
	 *            )
	 */
	public SpaceInstanceInfo(long appid, String nodeId, String runtimeId, SpaceConfiguration config) {

		if (config == null)
			throw new IllegalArgumentException("Space configuration is null");

		if (config.getDsType() != SpaceType.SCRATCH)
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
	 * @param config
	 *            valid input/output data space configuration (@see
	 *            {@link DataSpacesURI#createInOutSpaceURI(long, SpaceType, String)}
	 *            )
	 */
	public SpaceInstanceInfo(long appid, SpaceConfiguration config) {

		if (config == null)
			throw new IllegalArgumentException("Space configuration is null");

		if (config.getDsType() == SpaceType.SCRATCH)
			throw new IllegalArgumentException("This constructor cannot be used for scratch data space.");

		this.mountingPoint = DataSpacesURI.createInOutSpaceURI(appid, config.getDsType(), config.getName());
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
