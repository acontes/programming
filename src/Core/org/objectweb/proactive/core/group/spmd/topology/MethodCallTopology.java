/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.group.spmd.topology;

import java.lang.reflect.InvocationTargetException;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.group.MethodCallControlForGroup;
import org.objectweb.proactive.core.group.spmd.ProActiveSPMDGroupManager;
import org.objectweb.proactive.core.mop.MethodCallExecutionFailedException;

/**
 * Used to diffuse the topology tree
 */
public class MethodCallTopology extends MethodCallControlForGroup {

	private int treeId;
	private TopologyNode tree;

	/**
	 * Constructor
	 * @param idname - the id name of the barrier
	 * @param wave - the wave of synchronization
	 */
	public MethodCallTopology(TopologyNode tree, int treeId) {
		this.tree = tree;
		this.treeId = treeId;
	}

	/**
	 * Returns the name of the call
	 * @return the String "MethodCallBarrier"
	 */
	@Override
	public String getName() {
		return "_ImmediateMethodCallDummy"; // Immediate service
	}


	/**
	 * The execution of a wave call give the wave of synchronization of the
	 * barrier to the current active object.
	 * @param target this object is not used.
	 * @return null
	 */
	@Override
	public Object execute(Object target) throws InvocationTargetException, MethodCallExecutionFailedException {
		try {
			UniversalBody uBody = PAActiveObject.getBodyOnThis();
			if (AbstractBody.class.isAssignableFrom(uBody.getClass())) {
				AbstractBody body = (AbstractBody) uBody;
				ProActiveSPMDGroupManager spmdManager = body.getProActiveSPMDGroupManager();
				if(spmdManager != null){
					ProActiveSPMDTopologyManager topologyManager = spmdManager.getTopologyManager();
					int myRank = topologyManager.getMyRank();
					for(TopologyNode node : tree.getChildren()){
						if(node.getId() == myRank){
							if(node.getClass().equals(TopologyNode.class)){
								topologyManager.getThreadTopologySender().diffuseTopologyTreeByThread(node, treeId);
							}
							else{
								topologyManager.setTopologyTree(tree, treeId);
							}
							break;
						}
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
