/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
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
package org.objectweb.proactive.extra.scheduler.job;

/**
 * 
 * 
 * @author ProActive Team
 * @version 1.0, Jun 11, 2007
 * @since ProActive 3.2
 */
public enum JobPriority implements java.io.Serializable {
	
	/**  */
	LOWEST ("Lowest",1),
	/**  */
	BELOW_NORMAL ("Below Normal",2),
	/**  */
	NORMAL ("Normal",3),
	/**  */
	ABOVE_NORMAL ("Above Normal",4),
	/**  */
	HIGHEST ("Highest",5);
	
	private String name;
	private int priority;
	
	JobPriority (String name, int priority){
		this.name = name;
		this.priority = priority;
	}
	
	public String toString(){
		return name;
	}
	
	public int getPriority(){
		return priority;
	}
	
}
