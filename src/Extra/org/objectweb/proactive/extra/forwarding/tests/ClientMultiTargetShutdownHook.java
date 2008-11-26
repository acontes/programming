package org.objectweb.proactive.extra.forwarding.tests;

import java.util.Vector;

import org.objectweb.proactive.extra.forwarding.localforwarder.ForwardingAgent;

public class ClientMultiTargetShutdownHook implements Runnable {

	ForwardingAgent agent;
	Vector<ConnectionRunnable> connectionVector;
	
	public ClientMultiTargetShutdownHook(ForwardingAgent agent, Vector<ConnectionRunnable> connectionVector) {
		this.agent = agent;
		this.connectionVector = connectionVector;
	}
	
	@Override
	public void run() {
		for (ConnectionRunnable connection : connectionVector) {
			connection.setRunning(false);
		}
		agent.cleanupAndExit();
	}

}
