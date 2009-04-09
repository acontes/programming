/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.net.URL;


/**
 * - for mapping URI -> URL - stores configuration - url, path, hostname, -
 * gives information about: type, name (impl: from mounting point) - stores
 * mounting point (SpaceURI)
 * 
 */
public class SpaceInstanceInfo {

    protected URL url;

    protected String path;

    protected String hostname;

    protected SpaceURI mountingPoint;

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
        // TODO
        return mountingPoint.getName();
    }

    public SpaceType getType() {
        // TODO
        return mountingPoint.getSpaceType();
    }
}
