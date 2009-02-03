package org.objectweb.proactive.extra.forwardingv2.router;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


/** A router instance
 *  
 * A router receives messages from client and forward them to another client.
 * 
 * This intended to be used by router components.
 */
public interface Router extends Runnable {
	/** Submit a job to be executed asynchronously. 
	 * 
	 * All time consuming tasks should be submitted by using this method. The single threaded
	 * front end should not execute any other code than reading data chunk from {@link SocketChannel}.
	 * 
	 * @param message the received message to be handled
	 * @param attachment the attachment used to received the message
	 */
    public void handleAsynchronously(ByteBuffer message, Attachment attachment);

    /** Returns the client corresponding to a given {@link AgentID}
     * 
     * @param agentId the {@link AgentID}
     * @return the corresponding client or null is unknonwn
     */
    public Client getClient(AgentID agentId);

    /** Add a new client to the routing table
     * 
     * @param client the new client
     */
    public void addClient(Client client);
}
