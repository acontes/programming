package org.objectweb.proactive.extra.infrastructuremanager.dataresource;

import java.util.List;

import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;

public interface IMState {
	
	public List<IMNode> getFreeNodes();
	public List<IMNode> getBusyNodes();
	public List<IMNode> getDownNodes();

}
