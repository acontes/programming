package org.objectweb.proactive.extra.forwardingv2.client;

import org.objectweb.proactive.extra.forwardingv2.exceptions.RoutingException;
import org.objectweb.proactive.extra.forwardingv2.protocol.DataRequestMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message;

public interface AgentV2Internal extends AgentV2 {
	 public void sendReply(Message request, byte[] data) throws RoutingException;

	 public void sendExceptionReply(DataRequestMessage request, Exception e) throws RoutingException;

}
