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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.objectweb.proactive.ic2d.monitoring.data.HostObject;
import org.objectweb.proactive.ic2d.monitoring.figures.HostFigure;

public class HostEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener{
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	public HostEditPart(HostObject model) {
		setModel(model);
	}
	
	//
	// -- PUBLICS METHODS -----------------------------------------------
	//
	
	/**
	 * When an EditPart is added to the EditParts tree
	 * and when its figure is added to the figure tree,
	 * the method EditPart.activate() is called.
	 */
	public void activate(){
		if (!isActive())
			getCastedModel().addPropertyChangeListener(this);
		super.activate();
	}
	
	/**
	 * When an EditPart is removed from the EditParts
	 * tree, the method deactivate() is called.
	 */
	public void deactivate(){
		if (isActive())
			getCastedModel().removePropertyChangeListener(this);
		super.deactivate();
	}
	
	/**
	 * Convert the result of EditPart.getModel()
	 * to the actual class.
	 */
	public HostObject getCastedModel(){
		return (HostObject)getModel();
	}
	
	/**
	 * 
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Must fill the view with data extracted
	 * from the model object associated with
	 * the EditPart, it will be called just
	 * after the creation of the figure
	 */
	//public void refreshVisuals(){
	/*HostFigure figure = (HostFigure)getFigure();
	 HostObject model = (HostObject)getModel();
	 figure.setTitle(model.getFullName());*/
	//}
	
	
	
	/** 
	 * To update the children EditParts according
	 * to the getModelChildren() method of the EditPart
	 * (changes in the model which need this kind of
	 * refreshing are called structural changes).
	 */
	/*public void refreshChildren(){
	 
	 }*/
	
	//
	// -- PROTECTED METHODS -----------------------------------------------
	//
	
	/**
	 * Must return a new view associated
	 * with the type of model object the
	 * EditPart is associated with
	 */
	protected IFigure createFigure() {
		//TODO parent world object?
		return new HostFigure(getCastedModel().getFullName(), 10, 80);
	}
	
	
	
	/**
	 * Must return the list of model objects that
	 * should be represented as children of the
	 * content pane of the view (if you don't override
	 * this method, the empty list is returned).
	 */
	protected List getModelChildren(){
		return getCastedModel().getChildren();
	}
	
	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
