/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.io.Serializable;

import javax.naming.ConfigurationException;

/**
 * Stores complete description of data space instance, i.e. mounting point URI
 * with information contained there (type, application id...), and space access
 * description (remote access URL, optional local path and hostname).
 * 
 * Instances of this class are immutable, therefore thread-safe.
 * <code>hashCode</code> and <code>equals</code> methods are defined.
 */
public final class SpaceInstanceInfo implements Serializable {

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
	 * @throws ConfigurationException
	 *             when provided information is not enough to build a complete
	 *             space definition (no hostname for path etc.)
	 */
	public SpaceInstanceInfo(long appid, String runtimeId, String nodeId, SpaceConfiguration config)
			throws ConfigurationException {
		if (config.getType() != SpaceType.SCRATCH)
			throw new ConfigurationException("This constructor must be used for scratch data space.");

		this.mountingPoint = DataSpacesURI.createScratchSpaceURI(appid, runtimeId, nodeId);
		this.url = config.getUrl();
		this.hostname = config.getHostname();
		this.path = config.getPath();
		check();
	}

	/**
	 * Creates SpaceInstanceInfo for input/output data space.
	 * 
	 * @param appid
	 *            application identifier
	 * @param config
	 *            input or output data space configuration
	 * @throws ConfigurationException
	 *             when provided information is not enough to build a complete
	 *             space definition (no hostname for path etc.)
	 */
	public SpaceInstanceInfo(long appid, SpaceConfiguration config) throws ConfigurationException {
		if (config.getType() == SpaceType.SCRATCH)
			throw new ConfigurationException("This constructor cannot be used for scratch data space.");

		this.mountingPoint = DataSpacesURI.createInOutSpaceURI(appid, config.getType(), config.getName());
		this.url = config.getUrl();
		this.hostname = config.getHostname();
		this.path = config.getPath();
		check();
	}

	/**
	 * Remote access URL. Always defined.
	 * 
	 * @return remote access URL to this data space
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Returns local access path, that can be used for host with hostname as
	 * returned by {@link #getHostname()}.
	 * 
	 * Local access path may not be defined.
	 * 
	 * @return local access path; <code>null</code> if local access is undefined
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns hostname where local access path may be used. This hostname
	 * should be comparable to {@link Utils#getHostnameForThis()}.
	 * 
	 * @return hostname where local access path may be used; <code>null</code>
	 *         if local access is undefined
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Returns mounting point URI of this data space.
	 * 
	 * @return mounting point URI
	 */
	public DataSpacesURI getMountingPoint() {
		return mountingPoint;
	}

	/**
	 * Returns the name of a space, if such makes sense for that type of data
	 * space.
	 * 
	 * @return name of a space; may be <code>null</code> for scratch data space
	 */
	public String getName() {
		return mountingPoint.getName();
	}

	/**
	 * @return data space type
	 */
	public SpaceType getType() {
		return mountingPoint.getSpaceType();
	}

	/**
	 * @return application id of data space
	 */
	public long getAppId() {
		return mountingPoint.getAppId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mountingPoint.hashCode();
		result = prime * result + url.hashCode();
		result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof SpaceInstanceInfo))
			return false;

		final SpaceInstanceInfo other = (SpaceInstanceInfo) obj;
		if (mountingPoint == null) {
			if (other.mountingPoint != null)
				return false;
		} else if (!mountingPoint.equals(other.mountingPoint))
			return false;

		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;

		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;

		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	private void check() throws ConfigurationException {
		if (!mountingPoint.isComplete())
			throw new ConfigurationException("Constructed mounting point URI must be complete");
		if (url == null)
			throw new ConfigurationException("No remote access URL provided");
		if (path != null && hostname == null)
			throw new ConfigurationException("Local path provided without hostname specified");
	}
}
