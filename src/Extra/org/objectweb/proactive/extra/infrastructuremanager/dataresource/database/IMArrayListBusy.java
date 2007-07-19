package org.objectweb.proactive.extra.infrastructuremanager.dataresource.database;

import java.util.ArrayList;

import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extra.infrastructuremanager.dataresource.IMNode;


public class IMArrayListBusy extends ArrayList<IMNode> {
    /**  */
	private static final long serialVersionUID = 5201313059900868798L;

	@Override
	public boolean add(IMNode imNode) {
        try {
            imNode.setBusy();
        } catch (NodeException e) {
            e.printStackTrace();
        }
        return super.add(imNode);
    }
}
