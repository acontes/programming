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
package org.objectweb.proactive.ic2d.jobmonitoring.editparts;

import java.util.List;

import org.objectweb.proactive.ic2d.monitoring.data.AbstractDataObject;
import org.objectweb.proactive.ic2d.monitoring.data.WorldObject;

/**
 * @author Mich&egrave;le Reynier and Jean-Michael Legait
 *
 */
public class WorldTreeEditPart extends JobMonitoringTreeEditPart {

	//
	// -- CONSTRUCTOR ------------------------------------------------
	//
	
	/**
	 * @param model
	 */
	public WorldTreeEditPart(AbstractDataObject model) {
		super(model);
	}
	
	//
	// -- PROTECTED METHODS -------------------------------------------
	//
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	@Override
	protected List getModelChildren() {
//		return getCastedModel().getVNChildren();
		return getCastedModel().getVNChildren();
	}
	
	//
	// -- PRIVATE METHODS -------------------------------------------
	//

	private WorldObject getCastedModel() {
		return (WorldObject)getModel();
	}
	
}
