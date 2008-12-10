package org.objectweb.proactive.extra.forwardingv2.client;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.EndpointID;

public class EndpointImpl implements Endpoint {
	
	private final EndpointID endpointID;
	private final AgentID agentID;
	private final ForwardingAgent forwardingAgent;
	
	public EndpointImpl(EndpointID endpointID, AgentID agentID,
			ForwardingAgent forwardingAgent) {
		this.endpointID = endpointID;
		this.agentID = agentID;
		this.forwardingAgent = forwardingAgent;
	}

	public byte[] receiveMsg(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public long sendMsg(AgentID agentID, EndpointID endpointID, byte[] data) {
		// TODO Auto-generated method stub
		return 0;
	}

	public EndpointID getID() {
		return endpointID;
	}

}
