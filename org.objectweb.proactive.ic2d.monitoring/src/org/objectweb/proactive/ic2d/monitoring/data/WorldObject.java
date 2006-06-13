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


/**
 * Holder class for all hosts
 */
public class WorldObject extends AbstractDataObject {

	
	//
    // -- CONSTRUCTORS -----------------------------------------------
    //
    
	/**
	 * Create a new WorldObject
	 */
	public WorldObject() {
        super(null);
    }
	
	
    //
    // -- PUBLICS METHODS -----------------------------------------------
    //
	
	
	public String getKey() {
		// A WorldObject doesn't need a key because it is the only son of IC2DObject.
		return "WorldObject";
	}
	
	
	public String getFullName(){
		return "WorldObject";
	}
	
	/**
	 * Creates a new HostObject
	 * @param hostname machine's name:port
	 * @param os Operating System
	 */
	public HostObject addHostObject(String hostname, int port, int protocol) {
		HostObject host = new HostObject(this, hostname, port, protocol);
		this.putChild(host.getKey(), host);
		return host;
    }
	
	
	/**
	 * TODO comment
	 * @param hostname
	 */
	public void removeHostObject(String hostname) {
        removeChild(hostname);
    }
	
	
	/**
	 * TODO comment
	 * @param hostname
	 * @return
	 */
	public HostObject getHostObject(String hostname) {
        return (HostObject) getChild(hostname);
    }
	
	
	/**
	 * Destroys this object
	 */
    public void destroyObject() {
		destroy();
	}
	
	
	//
    // -- PROTECTED METHOD -----------------------------------------------
    //
    

}
