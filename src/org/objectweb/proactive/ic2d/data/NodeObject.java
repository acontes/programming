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
package org.objectweb.proactive.ic2d.data;

import org.objectweb.proactive.core.node.Node;

public class NodeObject extends AbstractDataObject{

    protected Node node;
	
    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    
	public NodeObject(VMObject parent, Node node){
		super(parent, node.getNodeInformation().getName());
		this.node = node;
	}
	
	
    //
    // -- PUBLIC METHOD -----------------------------------------------
    //
	
	public String getKey() {
		return this.abstractDataObjectName;
	}

	public String getFullName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void destroyObject() {
		// TODO Auto-generated method stub
		
	}
		
	/**
	 * Returns the node's protocol
	 * @return The protocol used
	 */
    public String getProtocol() {
        return node.getNodeInformation().getProtocol();
    }


}
