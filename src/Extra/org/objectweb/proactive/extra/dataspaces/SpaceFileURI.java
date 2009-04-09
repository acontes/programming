/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

/**
 * 
 *
 */
public class SpaceFileURI extends SpaceURI {

    public SpaceFileURI(long appId, SpaceType dsType, String name, String path) {
        super(appId, dsType, name);

        // TODO Auto-generated constructor stub
    }

    public SpaceFileURI(long appId, SpaceType dsType, String runtimeId, String nodeId, String path) {

        super(appId, dsType, runtimeId, nodeId);
    }

}
