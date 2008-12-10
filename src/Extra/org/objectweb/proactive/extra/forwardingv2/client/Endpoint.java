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
     * @param data A byte array to be send
     * @return An unique identifier associated to this message
     */
    public long sendMsg(AgentID agentID, EndpointID endpointID, byte[] data);

    /**
     * Wait for a reply message
     * 
     * @param id The unique ID associated to the request
     * 
     * @return The reply as a byte array
     */
    public byte[] receiveMsg(long id);
    
    /**
     * Return the EndpointID of this Endpoint
     * 
     * @return the EndpointID
     */
    public EndpointID getID();
}
