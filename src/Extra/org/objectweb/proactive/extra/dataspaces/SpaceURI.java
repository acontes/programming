/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

/**
 * resp: - stores URI as fields: app id, type, name | rt and node - correctness
 * guarantee - immutable - used for lookup (comparator) - serializable (sent
 * across network) col: - SpacesDirectory - instantiated in resolveFile -
 * instantiated during DS configuration - SpaceInstanceInfo
 * 
 */
public class SpaceURI {

    protected long appId;

    protected SpaceType dsType;

    protected String name;

    protected String runtimeId;

    protected String nodeId;

    /*
     * * Constructor for input/output dataspaces.
     * 
     * @param appId
     * 
     * @param dsType
     * 
     * @param name
     */
    public SpaceURI(long appId, SpaceType dsType, String name) {
    }

    /*
     * * Constructor for scratch dataspaces.
     * 
     * @param appId
     * 
     * @param dsType
     * 
     * @param runtimeId
     * 
     * @param nodeId
     */
    public SpaceURI(long appId, SpaceType dsType, String runtimeId, String nodeId) {
    }

    public long getAppId() {
        return appId;
    }

    public SpaceType getSpaceType() {
        return dsType;
    }

    public String getName() {
        return name;
    }

    public String getRuntimeId() {
        return runtimeId;
    }

    public String getNodeId() {
        return nodeId;
    }
}
