package org.objectweb.proactive.extra.infrastructuremanager.dataresource;

import java.util.List;

public interface IMState {
	
	public List<IMNode> getFreeNodes();
	public List<IMNode> getBusyNodes();
	public List<IMNode> getDownNodes();

}
