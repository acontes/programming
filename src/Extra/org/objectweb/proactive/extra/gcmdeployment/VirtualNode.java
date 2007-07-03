package org.objectweb.proactive.extra.gcmdeployment;

import org.objectweb.proactive.annotation.PublicAPI;


/**
 * Careful synchronization is required since Virtual Node can be returned
 * to the user before nodes registration.
 *
 * @author cmathieu
 *
 */
@PublicAPI
public interface VirtualNode {

    /**
     * A magic number to indicate that a Virtual Node is asking
     * for every available nodes
     */
    static final public long MAX_CAPACITY = -2;

    /**
     * Returns the capacity asked by this Virtual Node
     *
     * @return the capacity asked by this Virtual Node. If max is specified
     * in the GCM Application Descriptor then MAX_CAPACITY is returned.
     */
    public long getRequiredCapacity();
}
