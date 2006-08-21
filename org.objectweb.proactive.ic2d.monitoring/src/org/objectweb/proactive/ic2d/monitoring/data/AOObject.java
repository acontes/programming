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
import org.objectweb.proactive.ic2d.monitoring.spy.SpyMessageEvent;

public class AOObject extends AbstractDataObject{

	/** Counter used to add a number after the name of the object
	 * if counter=3, and if the next object's name is "ao", then it's fullname
	 * will be "ao#3"
	 */
	private static int counter = 0;


	/** State of the object (ex: WAITING_BY_NECESSITY) */
	private State state;

	/** the object's name (ex: ao) */
	private String name;
	/** the object's fullname (ex: ao#3) */
	private String fullName;

	/** id used to identify the active object globally, even in case of migration */
	private UniqueID id;

	/** request queue length */
	private int requestQueueLength; // -1 = not known

	//
	// -- CONSTRUCTORS -----------------------------------------------
	//

	/**
	 * @param parent the Node containing the active object
	 * @param name the active object's name
	 * @param id the active object's id
	 */
	public AOObject(NodeObject parent, String name, UniqueID id){
		super(parent);

		if ( name == null) 
			name = this.getClass().getName() ;
		this.name = name;
		this.fullName = name + "#" + counter();
		this.id = id;
		this.requestQueueLength = -1;
	}

	//
	// -- PUBLIC METHODS ---------------------------------------------
	//

	/**
	 * Returns the id of the active object.
	 * @return the id of the active object.
	 */
	public UniqueID getID(){
		return this.id;
	}

	/**
	 * Returns the object's key. It is an unique identifier.
	 * @return the object's key
	 * @see AbstractDataObject#getKey()
	 */
	public String getKey() {
		return this.id.toString();
	}

	/**
	 * Returns the object's name. (ex: ao)
	 * @return the object's name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the object's full name. (ex: ao#3)
	 * @return the object's full name.
	 * @see AbstractDataObject#getFullName()
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Change the current state
	 * @param newState
	 */
	public void setState(State newState) {
		this.state = newState;
		setChanged();
		notifyObservers(this.state);
	}

	public State getState(){
		return this.state;
	}

	/**
	 * Add a communication between two active objects.
	 * @param message
	 */
	public void addCommunication(SpyMessageEvent message){
		setChanged();
		notifyObservers(message);
	}

	public String toString() {
		return this.getFullName();
	}

	/**
	 * Returns a string representing the type ActiveObject : "ao"
	 * @return ao
	 * @see AbstractDataObject#getType()
	 */
	public String getType() {
		return "ao";
	}

	
	/**
	 * 
	 * @param length
	 * @see #getRequestQueueLength()
	 */
	public void setRequestQueueLength(int length) {
		if (requestQueueLength != length) {
			requestQueueLength = length;
			this.setChanged();
			this.notifyObservers(new Integer(length));
		}
	}
	
	
	/**
	 * Returns the request queue length
	 * @return the request queue lenght
	 * @see #setRequestQueueLength(int)
	 */
	public int getRequestQueueLength() {
        return requestQueueLength;
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


	public static class AOComparator implements Comparator<String>{

		/**
		 * Compare two active objects.
		 * (For Example: ao#3 and ao#5 give -1 because ao#3 has been discovered before ao#5.)
		 * @return -1, 0, or 1 as the first argument is less than, equal to, or greater than the second.
		 */
		public int compare(String ao1, String ao2) {
			String ao1Name = ao1;
			String ao2Name = ao2;
			return -(ao1Name.compareTo(ao2Name));
		}	
	}

	@Override
	protected void foundForTheFirstTime() {
		// Add a MessageEventListener to the spy
		try {
			((NodeObject)this.parent).getSpy().addMessageEventListener(this.id);
		} catch (Exception e) {
			this.parent.notResponding();
			// TODO spy not responding
			/*Console.getInstance(Activator.CONSOLE_NAME).err("AOObject.foundForTheFirstTime() -> not responding");
			e.printStackTrace();*/
		}

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
