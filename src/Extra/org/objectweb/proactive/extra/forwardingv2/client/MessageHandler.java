package org.objectweb.proactive.extra.forwardingv2.client;

import org.objectweb.proactive.extra.forwardingv2.protocol.DataRequestMessage;


public interface MessageHandler {

    public void pushMessage(DataRequestMessage message);
}