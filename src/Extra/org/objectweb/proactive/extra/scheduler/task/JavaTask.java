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
package org.objectweb.proactive.extra.scheduler.task;

import java.util.Map;

/**
 * 
 * 
 * @author ProActive Team
 * @version 1.0, Jun 4, 2007
 * @since ProActive 3.2
 */
public abstract class JavaTask implements Task {
	
	/**
	 * Initialization default method for a task.
	 * By default it puts the parameters set in the task descriptor
	 * in the class variables if their names are correctly mapped.
	 * You can override this method to make your own initialisation.
	 * 
	 * @param args a map containing the differents variables names and values.
	 */
	public void init(Map<String, Object> args){
		try{
			//for (String key : args.keySet()){
				//TODO make the mapping automatically (seems not to be possible)
				//Field f = this.getClass().getDeclaredField(key);
				//f.set(this, f.getClass().cast(args.get(key)));
				//f.set(this, args.get(key));
			//}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
