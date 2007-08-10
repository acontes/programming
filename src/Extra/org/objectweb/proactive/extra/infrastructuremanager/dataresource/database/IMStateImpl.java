package org.objectweb.proactive.extra.infrastructuremanager.dataresource.database;

import java.util.List;

import org.objectweb.proactive.extra.infrastructuremanager.dataresource.IMState;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;

public class IMStateImpl implements IMState {
	
	private List<IMNode> free;
	private List<IMNode> busy;
	private List<IMNode> down;
	
	public IMStateImpl (List<IMNode> free, List<IMNode> busy, List<IMNode> down) {
		this.free = free;
		this.busy = busy;
		this.down = down;
	}

	public List<IMNode> getBusyNodes() {
		return free;
	}

	public List<IMNode> getDownNodes() {
		return busy;
	}

	public List<IMNode> getFreeNodes() {
		return down;
	}

}
