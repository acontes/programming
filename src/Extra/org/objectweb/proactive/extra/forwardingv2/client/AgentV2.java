package org.objectweb.proactive.extra.forwardingv2.client;

import java.net.URI;

import org.objectweb.proactive.extra.forwardingv2.exceptions.ExecutionException;
import org.objectweb.proactive.extra.forwardingv2.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public interface AgentV2 {

    /**
     * Send a message to the target AgentID, containing data.
     *
     * if oneWay, the result returned is null.
     * if not, this call is blocked until an answer is provided.
     *
     * @param targetID the destination of the data.
     * @param data the data to send.
     * @param oneWay
     * @return the data response.
     * @throws ForwardingException if the timeout is reached.
     */
    public byte[] sendMsg(AgentID targetID, byte[] data, boolean oneWay) throws MessageRoutingException,
            ExecutionException;

    /**
     * Send a message to the target AgentID, containing data.
     *
     * if oneWay, the the result returned is null.
     * if not, the this call is blocked until an answer is provided.
     *
     * @param targetID the destination of the data.
     * @param data the data to send.
     * @param oneWay
     * @return the data response.
     * @throws ForwardingException if the timeout is reached.
     */
    public byte[] sendMsg(URI targetURI, byte[] data, boolean oneWay) throws MessageRoutingException,
            ExecutionException;

    public AgentID getAgentID();

}