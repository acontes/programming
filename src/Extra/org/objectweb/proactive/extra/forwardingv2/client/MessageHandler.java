package org.objectweb.proactive.extra.forwardingv2.client;

import org.objectweb.proactive.extra.forwardingv2.protocol.Message;


public interface MessageHandler {

    public void setAgent(AgentV2 agent);

    public void pushMessage(Message message);
}
