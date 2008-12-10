package org.objectweb.proactive.extra.forwardingv2.protocol;

import java.io.Serializable;


/**
 * An unique identifier for Agents
 */
@SuppressWarnings("serial")
public class EndpointID implements Serializable {
    final private long id;

    public EndpointID(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
