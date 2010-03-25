package org.objectweb.proactive.core.group.spmd.topology;

/**
 * Represent a leaf of a topology tree, ie a host.
 */
public class TopologyLeaf extends TopologyNode {

	public TopologyLeaf(int hostRank) {
		super(hostRank);
	}

	@Override
	public String toString() {
		if(getParent() == null) return "(" + getId().toString() + ")";
		String value = new Double(getParent().getChildLength(getId())).toString();
		if (value.length() > 5)
			value = value.substring(0, 5);
		return getId().toString() + "_" + value;
	}

	@Override
	public void addChild(TopologyNode n, double length) {
	}

	@Override
	public int nbLeaf() {
		return 1;
	}

	@Override
	public String printIndexOfTrust() {
		String value = new Double(getIndexOfTrust()).toString();
		if (value.length() > 5)
			value = value.substring(0, 5);
		return getId().toString() + "_[" + value + "]";
	}
}
