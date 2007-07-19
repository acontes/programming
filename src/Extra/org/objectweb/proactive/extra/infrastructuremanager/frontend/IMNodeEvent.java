package org.objectweb.proactive.extra.infrastructuremanager.frontend;

import org.objectweb.proactive.extra.infrastructuremanager.dataresource.IMNode;

public interface IMNodeEvent {
	
	public IMNode getIMNode();

	public int getNBAllNodes();
	
	public int getNBFreeNodes();
	
	public int getNBBusyNodes();
	
	public int getNBDownNodes();
}
