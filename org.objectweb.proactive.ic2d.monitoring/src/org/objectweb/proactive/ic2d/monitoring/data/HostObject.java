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

import java.rmi.RemoteException;
import java.util.List;

import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.ic2d.monitoring.exceptions.HostAlreadyExistsException;
import org.objectweb.proactive.ic2d.monitoring.finder.HostRTFinder;
import org.objectweb.proactive.ic2d.monitoring.finder.HostRTFinderFactory;


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
    private Protocol protocol;
    
	//
    // -- CONSTRUCTORS -----------------------------------------------
    //
    
    /**
     * Creates a new HostObject
     * @param parent His parent
     * @parent hostname machine's name
     * @param port
     * @param protocol to use
     * @throws HostAlreadyExistsException 
     */
	public HostObject(String hostname, int port, Protocol protocol) throws HostAlreadyExistsException{
		super(WorldObject.getInstance());
		System.out.println("HostObject : constructor");
		this.hostname = hostname;
		this.port = port;
		this.protocol = protocol;
		
		HostObject hostAlreadyExists = (HostObject) this.parent.monitoredChildren.get(this.getKey());
		if(hostAlreadyExists != null)
			throw new HostAlreadyExistsException(hostAlreadyExists);
		
		this.parent.putChild(this);
		this.explore();
	}
	
    //
    // -- PUBLIC METHODS -----------------------------------------------
    //

	
	/**
	 * Explores the host, in order to find all VMs known by this one
	 * @return Virtual machines found in the host
	 */
	public void explore() {
		System.out.println("HostObject : explore");
		HostRTFinder runtimeFinder = HostRTFinderFactory.createHostRTFinder(this.getProtocol());
		List<ProActiveRuntime> foundRuntimes = null;
		try {
			foundRuntimes = runtimeFinder.findPARuntime(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(foundRuntimes != null){
			for (int i = 0; i < foundRuntimes.size(); ++i) {
				ProActiveRuntime proActiveRuntime = (ProActiveRuntime) foundRuntimes.get(i);
				handleProActiveRuntime(proActiveRuntime);
			}
		}		
	}
	
	public String getKey() {
		return hostname+":"+port;
	}
	
	
	public String getFullName(){
		return hostname+":"+port+":"+os;
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
	 * Returns the host's protocol
	 * @return The host's protocol
	 */
	public Protocol getProtocol(){
		return this.protocol;
	}
	
	/**
	 * Returns a string representing this host
	 */
    public String toString() {
        return "Host " + hostname;
    }

    public String getType() {
    	return "host";
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
    
    //
    // -- PRIVATE METHOD -----------------------------------------------
    //
    
    /**
     * TODO
     */
	private void handleProActiveRuntime(ProActiveRuntime runtime){
		System.out.println("HostObject : handleProActiveRuntime");
		VMObject vm = null;
		vm = new VMObject(this, runtime);
		exploreChild(vm);
	}
    
}
