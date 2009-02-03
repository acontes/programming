package org.objectweb.proactive.extra.forwardingv2.router;

import java.nio.ByteBuffer;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public interface Router extends Runnable {

    public void handleAsynchronously(ByteBuffer message, Attachment attachment);

    public Client getClient(AgentID agentId);

    public void addClient(Client client);

    //	public Client getClient(long attachmentId);

    //	public Attachment getAttachment(long attachmentId);

    public int getLocalPort();
}
