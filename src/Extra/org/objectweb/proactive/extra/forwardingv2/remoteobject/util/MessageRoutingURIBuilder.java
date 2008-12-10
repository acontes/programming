package org.objectweb.proactive.extra.forwardingv2.remoteobject.util;

import java.net.URI;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.EndpointID;


public class MessageRoutingURIBuilder {
    public final static String MESSAGE_ROUTING_PROTO = "pamr";

    public static URI create(AgentID agentID, EndpointID endpointID, String objectName) {
        return URI.create(MESSAGE_ROUTING_PROTO + ":/" + agentID + "/" + endpointID + "/" + objectName);
    }
}
