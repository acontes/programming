package org.objectweb.proactive.extra.forwardingv2.client;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.EndpointID;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message;

public class ForwardingAgent implements Agent {
	
	// SINGLETON 
    private static ForwardingAgent _singleton = null;

    synchronized public static ForwardingAgent getAgent() {
        if (_singleton == null) {
            _singleton = new ForwardingAgent();
        }
        return _singleton;
    }

    // FIELDS
    private final HashMap<EndpointID, Endpoint> endPoints;
    private final AtomicLong currentEndpointID;
    private AgentID agentID;
    
    protected ForwardingAgent() {
    	endPoints = new HashMap<EndpointID, Endpoint>();
    	currentEndpointID = new AtomicLong(0);
    }
    
    /**
     * Initialize the tunnel to the registry and get the agentID from it.
     * 
     * @param registryAddress {@link InetAddress} of the registry
     * @param registryPort port to connect.
     */
    public void init(InetAddress registryAddress, int registryPort) {
    	// TODO Connect to the registry
    	// TODO send a registration message
    	// TODO Waits for an agentID attribution
    }
    
	public Endpoint getEndpoint() {
		Endpoint ep = new EndpointImpl(new EndpointID(currentEndpointID.incrementAndGet()), getAgentID(), this);
		synchronized (endPoints) {
			endPoints.put(ep.getID(), ep);
		}
		return ep;
	}

	public void sendMsg(Message msg) {
		// TODO Put message in the tunnel
	}
	
	public AgentID getAgentID(){
		return agentID;
	}

}
