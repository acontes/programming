package org.objectweb.proactive.extra.forwardingv2.client;

import java.util.HashMap;
import java.util.Iterator;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.EndpointID;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message;



public class EndpointImpl implements Endpoint {

    private final EndpointID endpointID;
    private final AgentID agentID;
    private final ForwardingAgent forwardingAgent;
    private final HashMap<Long, Message> mailbox;

    public EndpointImpl(EndpointID endpointID, AgentID agentID, ForwardingAgent forwardingAgent) {
        mailbox = new HashMap<Long, Message>();
        this.endpointID = endpointID;
        this.agentID = agentID;
        this.forwardingAgent = forwardingAgent;
    }

    public byte[] receiveMsg(long id) {
        // TODO Auto-generated method stub
        return null;
    }

    public long nextAvailableID() {
        synchronized (mailbox) {
            Iterator<Long> iter = mailbox.keySet().iterator();
            if (iter.hasNext()) {
                return iter.next();
            }
        }
        return 0;
    }

    public void sendMsg(AgentID agentID, EndpointID endpointID, long messageID, byte[] data) {
        Message m = new Message();
        // TODO fill in the message with usefull data
        forwardingAgent.sendMsg(m);
    }

    public EndpointID getID() {
        return endpointID;
    }

    public void addMessageToBox(Message msg) {
        synchronized (mailbox) {
            mailbox.put(msg.getID, msg);
        }
    }

}
