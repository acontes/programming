package org.objectweb.proactive.extra.infrastructuremanager.nodesource.frontend;

import java.util.ArrayList;

import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;

public interface NodeSourceInterface {
	
	// Free nodes
	public ArrayList<IMNode> getFreeNodes();
	public IntWrapper getNbFreeNodes();
	
	// Busy nodes
	public ArrayList<IMNode> getBusyNodes();
	public IntWrapper getNbBusyNodes();
	
	// Down nodes
	public ArrayList<IMNode> getDownNodes();
	public IntWrapper getNbDownNodes();
	
	// All Nodes
	public ArrayList<IMNode> getAllNodes();
	public IntWrapper getNbAllNodes();

}
