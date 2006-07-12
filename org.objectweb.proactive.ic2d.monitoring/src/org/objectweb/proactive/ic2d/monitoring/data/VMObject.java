/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.ic2d.monitoring.data;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeImpl;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.util.UrlBuilder;


public class VMObject extends AbstractDataObject {
	
	private String key;
	
	private ProActiveRuntime runtime;
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	public VMObject(HostObject parent, ProActiveRuntime runtime) {
		super(parent);
		System.out.println("VMObject : constructor");
		this.runtime = runtime;
		this.key = this.runtime.getVMInformation().getVMID().toString();
		this.runtime = runtime;
	}
	
	//
	// -- PUBLIC METHOD -----------------------------------------------
	//
	
	/**
	 * Explores a ProActiveRuntime, in order to find all nodes known by this one
	 * @param vm The VMObject corresponding to the runtime given in parameter
	 */
	public void explore(){
		String[] namesOfNodes = null;
		try {
			namesOfNodes = runtime.getLocalNodeNames();
		} catch (ProActiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < namesOfNodes.length; ++i) {
			String nodeName = namesOfNodes[i];
			if (nodeName.indexOf("SpyListenerNode") == -1) {
				handleNode(nodeName);
			}
		}
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getFullName() {
		return "VM id=" + this.key;
	}
	
	public String toString() {
		return "JVM " + this.getKey();
	}
	
	public String getType() {
		return "jvm";
	}
	
	public ProActiveRuntime getRuntime() {
		return this.runtime;
	}
	
	//
	// -- PROTECTED METHOD -----------------------------------------------
	//
	
	/**
	 * Returns the parent of the VM, that's to say the host where is the VM
	 * @return the host of this VM
	 */
	protected HostObject getTypedParent() {
		return (HostObject) parent;
	}
	
	/**
	 * Get the ProActiveRuntime associated with this VMObject
	 * @return The ProActiveRuntime associated with this VMObject
	 */
	protected ProActiveRuntime getProActiveRuntime(){
		return this.runtime;
	}
	
	/**
	 * Explore the child
	 * @param child The child to explore
	 */
	protected void exploreChild(AbstractDataObject child) {
		if(skippedChildren.containsKey(child.getKey())){
			System.out.println("VMObject : exploreChild");
			return;
		}
		else if (!monitoredChildren.containsKey(child.getKey())){
			this.filterAndPutChild(child);
			((NodeObject)child).addSpy();
		}
		else //parent.monitoredChildren.containsKey(vm.getKey())
			child = (AbstractDataObject)monitoredChildren.get(child.getKey());
		child.explore();
	}
	
	//
	// -- PRIVATE METHOD -----------------------------------------------
	//
	
	/**
	 * TODO
	 */
	private void handleNode(String nodeName){
		System.out.println("VMObject : handleNode");
		HostObject parent = getTypedParent();
		String nodeUrl = UrlBuilder.buildUrl(parent.getHostName(), nodeName,
				parent.toString()+":", parent.getPort());
		
		Node node = null;
		try {
			node = new NodeImpl(runtime, nodeUrl,UrlBuilder.getProtocol(nodeUrl), runtime.getJobID(nodeUrl));
		} catch (ProActiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NodeObject nodeObject = new NodeObject(this, node);
		this.exploreChild(nodeObject);
	}
}
