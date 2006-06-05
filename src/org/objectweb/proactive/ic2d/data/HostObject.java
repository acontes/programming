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

import java.util.List;

/**
 * Holder class for the host data representation
 */
public class HostObject extends AbstractDataObject {

    /** Name of this Host */
    private String hostname;
    
    /** Number of the port */
    private int port;
    
    /** Name of Operating System */
    private String os = "OS undefined";
	
    /** Host's protocol */
    private int protocol;
	//
    // -- CONSTRUCTORS -----------------------------------------------
    //
    
    /**
     * Creates a new HostObject
     * @param parent His parent
     * @parent hostname achine's name:port
     * @param os Host's operating sytem
     */
	protected HostObject(WorldObject parent, String hostname, int port, int protocol){
		super(parent);
		this.hostname = hostname;
		this.port = port;
		this.protocol = protocol;
	}
	
    //
    // -- PUBLIC METHODS -----------------------------------------------
    //

	/**
	 * Explore the current host to discover which elements it contains.
	 * @return A VMObject list
	 */
	public List explore(){
		//TODO
		return null;
		
		//Explorer explorer = new Explorer();
		//List proActiveRuntimeList = explorer.exploreHost(this);
		
		//List resul = new ArrayList();
		//for(int i=0, size=proActiveRuntimeList.size(); i < size ; i++){
		//	
		//}
	}
	
	public String getKey() {
		return hostname+":"+port;
	}
	
	
	public String getFullName(){
		return hostname+":"+port+":"+os;
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
	 * @return Number of the port
	 */
	public int getPort(){
		return this.port;
	}
	
	/**
	 * Return the host's operating system
	 * @return a string representation of the host's operating system
	 */
	public String getOperatingSystem() {
        return os;
    }
	
	/**
	 * Retuens the host's protocol
	 * @return The host's protocol
	 */
	public int getProtocol(){
		return this.protocol;
	}
	
	/**
	 * Returns a string representing this host
	 */
    public String toString() {
        return "Host: " + hostname+":"+ port + "\n" + super.toString();
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
