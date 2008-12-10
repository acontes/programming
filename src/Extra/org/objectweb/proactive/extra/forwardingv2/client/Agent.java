package org.objectweb.proactive.extra.forwardingv2.client;

import org.objectweb.proactive.extra.forwardingv2.protocol.Message;


public interface Agent {

    public Endpoint getEndpoint();

    /* Method signature will probably change
     * - Error handling ?
     * - Alreay build msg or not ?
     */
    public void sendMsg(Message msg);
}
