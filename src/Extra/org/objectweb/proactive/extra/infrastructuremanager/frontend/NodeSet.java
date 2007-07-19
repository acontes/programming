package org.objectweb.proactive.extra.infrastructuremanager.frontend;

import java.util.ArrayList;
import java.util.Collection;

import org.objectweb.proactive.core.node.Node;

/**
 * Representation of a node set.
 * In this first version, the node set contains only the nodes given by the Infrastructure Manager.
 * In a future version, it will give also further informations like why nodes haven't been given,
 * or more specifications on the nodes, like distance between them, etc...
 * 
 * @author ProActive Team
 * @version 1.0, Jun 11, 2007
 * @since ProActive 3.2
 */
public class NodeSet extends ArrayList<Node> {

    public NodeSet() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NodeSet(Collection<? extends Node> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	public NodeSet(int initialCapacity) {
		super(initialCapacity);
		// TODO Auto-generated constructor stub
	}

	/**  */
    private static final long serialVersionUID = 4372709972508178428L;
}
