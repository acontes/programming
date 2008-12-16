package org.objectweb.proactive.extra.forwardingv2.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;

import org.objectweb.proactive.extra.forwarding.exceptions.ForwardingException;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public interface AgentV2 {

    public void initialize(InetAddress registryAddress, int registryPort) throws IOException;

    public byte[] sendMsg(AgentID targetID, byte[] data, boolean oneWay) throws ForwardingException;

    public byte[] sendMsg(URI targetURI, byte[] data, boolean oneWay) throws ForwardingException;

    public AgentID getAgentID();

}
