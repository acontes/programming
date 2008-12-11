package org.objectweb.proactive.extra.forwardingv2.client;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.EndpointID;


public interface Endpoint {
    /**
     * Send a message to an endpoint.
     * 
     * The unique identifier returned can be used to wait for a reply.
     * 
     * @param agentID Destination agent
     * @param endpointID Destination endpoint 
     * @param messageID An unique identifier associated to this message
     * @param data A byte array to be send
     */
    public void sendMsg(AgentID agentID, EndpointID endpointID, long messageID, byte[] data);

    /**
     * Wait for a reply message
     * 
     * @param id The unique ID associated to the request
     * 
     * @return The reply as a byte array
     */
    public byte[] receiveMsg(long id);

    /**
     * Return the next available ID in the message box
     * 
     * @return id The unique ID available
     */
    public long nextAvailableID();

    /**
     * Return the EndpointID of this Endpoint
     * 
     * @return the EndpointID
     */
    public EndpointID getID();
}
