package org.objectweb.proactive.extra.infrastructuremanager.dataresource.database;

import java.util.ArrayList;

import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extra.infrastructuremanager.dataresource.IMNode;


public class IMArrayListFree extends ArrayList<IMNode> {
    /**  */
	private static final long serialVersionUID = 3684261120280774416L;

	@Override
	public boolean add(IMNode imNode) {
        try {
            imNode.setFree();
        } catch (NodeException e) {
            e.printStackTrace();
        }
        return super.add(imNode);
    }
}
