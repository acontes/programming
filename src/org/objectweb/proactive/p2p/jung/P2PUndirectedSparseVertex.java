package org.objectweb.proactive.p2p.jung;

import edu.uci.ics.jung.graph.impl.UndirectedSparseVertex;

public class P2PUndirectedSparseVertex extends UndirectedSparseVertex {

	protected int noa;
	protected int maxNOA;
	
	public P2PUndirectedSparseVertex() {
		super();
	}

	public int getMaxNOA() {
		return maxNOA;
	}

	public void setMaxNOA(int maxNOA) {
		this.maxNOA = maxNOA;
	}

	public int getNoa() {
		return noa;
	}

	public void setNoa(int noa) {
		this.noa = noa;
	}
	
	



}
