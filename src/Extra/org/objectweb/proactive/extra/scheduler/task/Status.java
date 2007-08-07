/*
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2006 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.extra.scheduler.task;


/**
 * The status of each task submitted by the user
 * 
 * @author ProActive Team
 * @version 1.1, Jun 28, 2007
 * @since ProActive 3.2
 */
public enum Status implements java.io.Serializable {
    /**
     *
     * The task has just been submitted by the user
     */
    SUBMITTED ("Submitted"),
    /**
     * The task is in the scheduler pending queue
     */
    PENDING ("Pending"),
    /**
     * The task is paused
     */
    PAUSED ("Paused"),
    /**
     * The task is paused from a submitted status
     */
    PAUSED_S ("Paused"),
    /**
     * The task is paused from a pending status
     */
    PAUSED_P ("Paused"),
    /**
     * The task is executing
     */
    RUNNNING ("Runnning"),
    /**
     * The task has finished execution
     */
    FINISHED ("Finished");
    
    private String name;
    
    
    Status (String name) {
    	this.name = name;
    }
    
    
    @Override
    public String toString(){
    	return name;
    }
}
