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
package org.objectweb.proactive.ic2d.monitoring.editparts;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.objectweb.proactive.ic2d.monitoring.data.AOObject;
import org.objectweb.proactive.ic2d.monitoring.figures.AOFigure;
import org.objectweb.proactive.ic2d.monitoring.figures.NodeFigure;

public class AOEditPart extends AbstractIC2DEditPart{

	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	public AOEditPart(AOObject model) {
		super(model);
		System.out.println("AOEditPart : constructor");
	}
	
	//
	// -- PUBLICS METHODS -----------------------------------------------
	//

	/**
     * Convert the result of EditPart.getModel()
     * to AOObject (the real type of the model).
     * @return the casted model
     */
	public AOObject getCastedModel(){
		return (AOObject)getModel();
	}
	
	//
	// -- PROTECTED METHODS -----------------------------------------------
	//
	
 	/**
 	 * Returns a new view associated
 	 * with the type of model object the
 	 * EditPart is associated with. So here, it returns a new NodeFigure.
 	 * @return a new NodeFigure view associated with the NodeObject model.
 	 */
	protected IFigure createFigure() {
		System.out.println("AOEditPart : createFigure");
		NodeFigure parent = (NodeFigure)((NodeEditPart)getParent()).getFigure();
		return new AOFigure(parent, getCastedModel().getFullName());
	}
	
	/**
	 * Returns a List containing the children model objects.
	 * @return the List of children
	 */
	protected List getModelChildren() {
		return getCastedModel().getMonitoredChildren();
	}

	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}
	
	
}
