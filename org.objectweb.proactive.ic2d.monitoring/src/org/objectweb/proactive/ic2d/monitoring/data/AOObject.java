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


public class AOObject extends AbstractDataObject{

	private static int id = 0;
	
    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    
	public AOObject(NodeObject parent, String className){
		super(parent, className + "#" + counter());
		this.parent.putChild(this.getKey(), this);
		System.out.println("Constructor AOObject : className = "+className+", id = "+id);
	}
	
    //
    // -- PUBLIC METHODS ---------------------------------------------
    //
	
	public String getKey() {
		return abstractDataObjectName;
	}

	public String getFullName() {
		return abstractDataObjectName;
	}

	public void destroyObject() {
		// TODO Auto-generated method stub
		
	}
	
	
    //
    // -- PRIVATE METHODS ---------------------------------------------
    //
	
    private static synchronized int counter() {
    	return ++id;
    }
	
}
