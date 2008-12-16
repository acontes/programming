package org.objectweb.proactive.extra.forwardingv2.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;

import org.objectweb.proactive.extra.forwarding.exceptions.ForwardingException;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public interface AgentV2 {

    public void initialize(InetAddress registryAddress, int registryPort) throws IOException;

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
    public byte[] sendMsg(AgentID targetID, byte[] data, boolean oneWay) throws ForwardingException;

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
    public byte[] sendMsg(URI targetURI, byte[] data, boolean oneWay) throws ForwardingException;

    public AgentID getAgentID();

}
