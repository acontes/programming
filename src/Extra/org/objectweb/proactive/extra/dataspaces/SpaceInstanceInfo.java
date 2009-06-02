/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.io.Serializable;

import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;


/**
 * Stores complete description of data space instance, i.e. mounting point URI with information
 * contained there (type, application id...), and space access description (remote access URL,
 * optional local path and hostname).
 * <p>
 * Instances of this class are immutable, therefore thread-safe. <code>hashCode</code> and
 * <code>equals</code> methods are defined.
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
     *            scratch data space configuration; must be complete - with access URL specified
     * @throws ConfigurationException
     *             when provided information is not enough to build a complete space definition - no
     *             remote access URL is defined.
     * @see SpaceConfiguration#isComplete()
     */
    public SpaceInstanceInfo(long appid, String runtimeId, String nodeId, ScratchSpaceConfiguration config)
            throws ConfigurationException {
        this(config, DataSpacesURI.createScratchSpaceURI(appid, runtimeId, nodeId));
    }

    /**
     * Creates SpaceInstanceInfo for input/output data space.
     * 
     * @param appid
     *            application identifier
     * @param config
     *            input or output data space configuration; must be complete - with access URL
     *            specified
     * @throws ConfigurationException
     *             when provided information is not enough to build a complete space definition - no
     *             remote access URL is defined.
     * @see SpaceConfiguration#isComplete()
     */
    public SpaceInstanceInfo(long appid, InputOutputSpaceConfiguration config) throws ConfigurationException {
        this(config, DataSpacesURI.createInOutSpaceURI(appid, config.getType(), config.getName()));
    }

    private SpaceInstanceInfo(final SpaceConfiguration config, final DataSpacesURI mountingPoint)
            throws ConfigurationException {
        if (!config.isComplete())
            throw new ConfigurationException(
                "Space configuration is not complete, no remote access URL provided");
        if (!mountingPoint.isSpacePartFullyDefined() || !mountingPoint.isSpacePartOnly()) {
            throw new RuntimeException(
                "Unexpectedly constructed mounting point URI does not have space part fully defined");
        }

        this.mountingPoint = mountingPoint;
        this.url = config.getUrl();
        this.hostname = config.getHostname();
        this.path = config.getPath();
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
     * Returns local access path, that can be used for host with hostname as returned by
     * {@link #getHostname()}.
     * <p>
     * Local access path may not be defined.
     * 
     * @return local access path; <code>null</code> if local access is undefined
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns hostname where local access path may be used. This hostname should be comparable to
     * {@link Utils#getHostname()}.
     * 
     * @return hostname where local access path may be used; <code>null</code> if local access is
     *         undefined
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Returns mounting point URI of this data space.
     * <p>
     * Returned URI has always space part fully defined and nothing else.
     * 
     * @return mounting point URI
     */
    public DataSpacesURI getMountingPoint() {
        return mountingPoint;
    }

    /**
     * Returns the name of a space, if such makes sense for that type of data space.
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[VFS URI: ");

        sb.append(mountingPoint);
        sb.append("; ");

        if (path == null) {
            sb.append(" no local-specific access");
        } else {
            sb.append("local access path: ");
            sb.append(path);
            sb.append(" at host: ");
            sb.append(hostname);
        }
        sb.append("; ");

        sb.append("remote access URL: ");
        sb.append(url);
        sb.append(']');

        return sb.toString();
    }
}
