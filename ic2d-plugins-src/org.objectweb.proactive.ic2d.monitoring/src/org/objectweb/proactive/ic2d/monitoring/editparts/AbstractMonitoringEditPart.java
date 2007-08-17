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
package org.objectweb.proactive.ic2d.monitoring.editparts;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.widgets.Display;
import org.objectweb.proactive.ic2d.monitoring.data.AbstractDataObject;
import org.objectweb.proactive.ic2d.monitoring.figures.AbstractFigure;
import org.objectweb.proactive.ic2d.monitoring.views.MonitoringView;

public abstract class AbstractMonitoringEditPart extends AbstractGraphicalEditPart implements Observer, Runnable {
	
	protected static Map<AbstractDataObject, AbstractMonitoringEditPart> registry = new HashMap<AbstractDataObject, AbstractMonitoringEditPart>();
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	public AbstractMonitoringEditPart(AbstractDataObject model) {
		setModel(model);
		registry.put(model, this);
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
			((AbstractDataObject)getModel()).addObserver(this);
		super.activate();
	}
	
	/**
	 * When an EditPart is removed from the EditParts
	 * tree, the method deactivate() is called.
	 */
	public void deactivate(){
		if (isActive()) {
			((AbstractDataObject)getModel()).deleteObserver(this);
			((Figure)getFigure()).removeAll();
		}
		super.deactivate();
	}

	/**
	 * This method is called whenever the observed object is changed.
	 * It calls the method <code>refresh()</code>.
	 * @param o the observable object (instance of AbstractDataObject).
	 * @param arg an argument passed to the notifyObservers  method.
	 */
	public void update(Observable o, Object arg) {
		Display.getDefault().asyncExec(this);
	}
	
	@Override
	public IFigure getContentPane() {
		return ((AbstractFigure)getFigure()).getContentPane();
	}
	
	/**
	 * Returns the monitoring view. Or null if the parent of this object is null,
	 * or if its parent isn't an instance of AbstractMonitoringEditPart.
	 */
	public MonitoringView getMonitoringView(){
		EditPart parent = getParent();
		if(parent == null || !(parent instanceof AbstractMonitoringEditPart))
			return null;
		else
			return ((AbstractMonitoringEditPart) parent).getMonitoringView();
	}
	
	/**
	 * Returns the current World Edit Part
	 * @return The WorldEditPart, or null if the parent of this object is null.
	 */
	public WorldEditPart getWorldEditPart(){
		return ((AbstractMonitoringEditPart)getParent()).getWorldEditPart();
	}
	
	public void run(){
		refresh();
	}
}
