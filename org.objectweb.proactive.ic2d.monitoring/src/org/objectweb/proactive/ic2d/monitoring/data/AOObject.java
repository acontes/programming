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

import java.util.Comparator;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.ic2d.console.Console;
import org.objectweb.proactive.ic2d.monitoring.Activator;

public class AOObject extends AbstractDataObject{

	/**
	 * State of the object (ex: WAITING_BY_NECESSITY)
	 */
	private int state;

	private String name;

	/** the object's name */
	private String fullName;

	private static int counter = 0;

	private UniqueID id;
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//

	public AOObject(NodeObject parent, String name, UniqueID id){
		super(parent);

		if ( name == null) 
			name = this.getClass().getName() ;
		this.name = name;
		this.fullName = name + "#" + counter();
		this.id = id;
	}

	//
	// -- PUBLIC METHODS ---------------------------------------------
	//

	public String getKey() {
		return this.id.toString();
	}

	public String getName() {
		return this.name;
	}

	public String getFullName() {
		return fullName;
	}

	/**
	 * Change the current state
	 */
	public void setState(int newState) {
		this.state = newState;
		setChanged();
		notifyObservers(new Integer(this.state));
	}

	public int getState(){
		return this.state;
	}

	public String toString() {
		return this.getFullName();
	}

	public String getType() {
		return "ao";
	}

	//
	// -- PROTECTED METHODS ---------------------------------------------
	//

	protected static synchronized void cancelCreation() {
		counter--;
	}

	//
	// -- PRIVATE METHODS ---------------------------------------------
	//

	private static synchronized int counter() {
		return ++counter;
	}

	public void explore() {/* Do nothing */}


	public static class AOComparator implements Comparator{
		
		/**
		 * Compare two active objects.
		 * (For Example: ao#3 and ao#5 give -1 because ao#3 has been discovered before ao#5.)
		 * @return -1, 0, or 1 as the first argument is less than, equal to, or greater than the second.
		 */
		public int compare(Object ao1, Object ao2) {
			String ao1Name = (String)ao1;
			String ao2Name = (String)ao2;
			return -(ao1Name.compareTo(ao2Name));
		}	
	}

	@Override
	protected void foundForTheFirstTime() {
		Console.getInstance(Activator.CONSOLE_NAME).
		log("AOObject "+fullName+" created based on ActiveObject "+id.toString());
	}
	
	@Override
	protected void alreadyMonitored() {
		Console.getInstance(Activator.CONSOLE_NAME).
		log("AOObject "+fullName+" already monitored");
		AOObject.cancelCreation();
	}
}
