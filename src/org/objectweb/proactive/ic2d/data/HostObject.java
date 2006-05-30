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

/**
 * Holder class for the host data representation
 */
public class HostObject extends AbstractDataObject {

    /** Name of this Host (machine's name:port) */
    protected String hostname;
    
    /** Name of Operating System */
    protected String os;
	
	//
    // -- CONSTRUCTORS -----------------------------------------------
    //
    
	public HostObject(WorldObject parent){
		super(parent);
		//TODO Change the hostnane!!!
		this.hostname = "***HostnameTEST***";
		this.os = "***OsTEST***";
	}
	
    //
    // -- PUBLIC METHODS -----------------------------------------------
    //
	
	public String getKey() {
		return hostname+":"+os;
	}
	
	
	/**
	 * Destroys this object
	 */
	public void destroyObject() {
		getTypedParent().removeHostObject(hostname);
	}
	
	
	/**
	 * @return Name of this Host
	 */
	public String getHostName(){
		return hostname;
	}
	
	
	/**
	 * Return the host's operating system
	 * @return a string representation of the host's operating system
	 */
	public String getOperatingSystem() {
        return os;
    }
	
	
	/**
	 * Returns a string representing this host
	 */
    public String toString() {
        return "Host: " + hostname + "\n" + super.toString();
    }
    
    //
    // -- PROTECTED METHOD -----------------------------------------------
    //
    
    /**
     * Returns the parent with the real type
     */
    protected WorldObject getTypedParent() {
        return (WorldObject) parent;
    }

   
}
