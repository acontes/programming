/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
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
package org.objectweb.proactive.core.group.spmd;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.group.MethodCallControlForGroup;


/**
 * This class represents a call of strong synchronization between the member of a SPMD Group.
 * @author Laurent Baduel
 */
public class MethodCallBarrier extends MethodCallControlForGroup {

	private String IDName;
	private int awaitedCalls;

	/**
	 * Constructor
	 * @param idname - the id name of the barrier 
	 * @param nbCalls - the number of call need to finish the barrier 
	 */
	public MethodCallBarrier(String idname, int nbCalls) {
		this.IDName = idname;
		this.awaitedCalls = nbCalls;
	}

	/**
	 * Constructor
	 * @param idname - the id name of the barrier 
	 */
    public MethodCallBarrier(String idname) {
    	this.IDName = idname;
    	this.awaitedCalls = ((AbstractBody) ProActive.getBodyOnThis()).getSPMDGroupSize();
    }

	/**
	 * Returns the name of the call
	 * @return the String "MethodCallBarrier"
	 */
	public String getName() {
		return "MethodCallBarrier";
	}

	/**
	 * Returns the number of awaited call for this barrier
	 * @return the number of awaited call for this barrier
	 */
	public int getAwaitedCalls() {
		return this.awaitedCalls;
	}
    
    /**
     * Returns the ID name of the barrier
     * @return the ID name of the barrier
     */
    public String getIDName() {
    	return this.IDName;
    }

}
