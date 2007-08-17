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
package org.objectweb.proactive.ic2d.monitoring.extpoints;

import org.eclipse.jface.action.IAction;
import org.objectweb.proactive.ic2d.monitoring.data.AOObject;
import org.objectweb.proactive.ic2d.monitoring.data.AbstractDataObject;

/**
 * This interface is used to export an action from an extension point.
 * 
 * @author vbodnart
 *
 */
public interface IActionExtPoint extends IAction{	
	
	/**
	 * Sets the reference to an AbstractDataObject. This reference is provided
	 * to the extension that plugs to an extension point. 
	 * @param object The AbstractDataObject that will be provided to the extension
	 */
	public void setAbstractDataObject(AbstractDataObject object);
	
	/**
	 * Sets the selected active object.
	 * @param The reference on the active object 
	 */
	public void setActiveSelect(AOObject ref);
	
}