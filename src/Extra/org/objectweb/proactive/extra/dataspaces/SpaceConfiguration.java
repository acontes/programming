/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.net.URL;


/**
 * Stores information needed to configure an instance of a dataspace.
 */
public class SpaceConfiguration {

    protected URL url;
    protected String path;
    protected String hostname;
    protected SpaceType dsType;
    protected String name;

    public URL getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public String getHostname() {
        return hostname;
    }

    public SpaceType getDsType() {
        return dsType;
    }

    public String getName() {
        return name;
    }
}
