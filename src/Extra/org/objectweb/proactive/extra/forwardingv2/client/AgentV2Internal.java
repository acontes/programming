package org.objectweb.proactive.extra.forwardingv2.client;

import org.objectweb.proactive.extra.forwardingv2.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DataRequestMessage;


public interface AgentV2Internal extends AgentV2 {
    public void sendReply(DataRequestMessage request, byte[] data) throws MessageRoutingException;

}
