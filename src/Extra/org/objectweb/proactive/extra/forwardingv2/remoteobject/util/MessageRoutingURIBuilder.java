package org.objectweb.proactive.extra.forwardingv2.remoteobject.util;

import java.net.URI;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public class MessageRoutingURIBuilder {
    public final static String MESSAGE_ROUTING_PROTO = "pamr";

    public static URI create(AgentID agentID, String objectName) {
        return URI.create(MESSAGE_ROUTING_PROTO + ":/" + agentID + "/" + objectName);
    }
}
