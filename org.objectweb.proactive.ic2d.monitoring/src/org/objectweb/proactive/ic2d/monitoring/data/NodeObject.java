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

import java.util.List;

import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;

public class NodeObject extends AbstractDataObject{

	/* The node name */
	private String key;
	
	/* A ProActive Node */
    protected Node node;
	
    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    
	public NodeObject(VMObject parent, Node node){
		super(parent, node.getNodeInformation().getName());
		System.out.println("NodeObject : constructor");
		this.node = node;
		this.key = node.getNodeInformation().getName();
		//this.explore();
	}
	
	
    //
    // -- PUBLIC METHOD -----------------------------------------------
    //
	
	/**
	 * Explores itself, in order to find all active objects known by this one
	 */
	public void explore(){
		System.out.println("NodeObject : explore()");
		VMObject parent = getTypedParent();
		List activeObjects = null;
		try {
			activeObjects = parent.getProActiveRuntime().getActiveObjects(this.key);
		} catch (ProActiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		handleActiveObjects(activeObjects);
	}
	
	public String getKey() {
		return this.key;
	}

	public String getFullName() {
		return this.key;
	}
	
		
	/**
	 * Returns the node's protocol
	 * @return The protocol used
	 */
    public String getProtocol() {
        return node.getNodeInformation().getProtocol();
    }


	//
	// -- PROTECTED METHOD -----------------------------------------------
	//
	    
	/**
	 * Get the typed parent
	 * @return the typed parent
	 */
	protected VMObject getTypedParent() {
		return (VMObject) parent;
	}
	
	
	protected void exploreChild(AbstractDataObject child) {
		if(skippedChildren.containsKey(child.getKey())){
			System.out.println("NodeObject : exploreChild");
			return;
		}
		else if (!monitoredChildren.containsKey(child.getKey()))
			this.putChild(child);
		else { //parent.monitoredChildren.containsKey(vm.getKey())
			AOObject.cancelCreation();
			child = (AbstractDataObject)monitoredChildren.get(child.getKey());
		}
		child.explore();
	}
	
    //
	// -- PRIVATE METHOD -----------------------------------------------
	//
	
	/**
	 * TODO
	 * @param activeObjects names' list of active objects containing in this NodeObject
	 */
	private void handleActiveObjects(List activeObjects){
		System.out.println("NodeObject : handleActiveobject");
		for (int i = 0, size = activeObjects.size(); i < size; ++i) {
			List aoWrapper = (List) activeObjects.get(i);
			UniversalBody ub = (UniversalBody)aoWrapper.get(0);
			
			String className = (String) aoWrapper.get(1);
			
			/* We don't monitor spies */
            if (className.equalsIgnoreCase(
                        "org.objectweb.proactive.ic2d.spy.Spy")) {
                continue;
            }
            AOObject ao = new AOObject(this,className.substring(className.lastIndexOf(".")+1), ub.getID());
            exploreChild(ao);
		}
	}


}
