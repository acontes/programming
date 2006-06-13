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
package org.objectweb.proactive.ic2d.monitoring.views;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.ic2d.monitoring.data.HostObject;
import org.objectweb.proactive.ic2d.monitoring.data.VMObject;
import org.objectweb.proactive.ic2d.monitoring.data.WorldObject;
import org.objectweb.proactive.ic2d.monitoring.editparts.IC2DEditPartFactory;

public class MonitoringView extends ViewPart {
	
	public static final String ID = "org.objectweb.proactive.ic2d.monitoring.views.MonitoringView";
	
	/** the graphical viewer */
	private ScrollingGraphicalViewer graphicalViewer;
	
	//
	// -- PUBLIC METHODS ----------------------------------------------
	//
	
	public void createPartControl(Composite parent){
		graphicalViewer = new ScrollingGraphicalViewer();
		initializeGraphicalViewer();
		graphicalViewer.createControl(parent);
		getViewSite().setSelectionProvider(graphicalViewer);
		getSite().setSelectionProvider(graphicalViewer);
		graphicalViewer.getControl().setBackground(ColorConstants.white);
		graphicalViewer.setContents(getContent());
	}

	
	public void initializeGraphicalViewer(){
		graphicalViewer.setRootEditPart(new ScalableFreeformRootEditPart());
		graphicalViewer.setEditPartFactory(new IC2DEditPartFactory());
	}
	
	//
	// -- PROTECTED METHODS -------------------------------------------
	//
	
	/**
	 * Returns the content of this editor
	 * @return the model object
	 */
	protected Object getContent(){
		HostObject host = new HostObject(new WorldObject(), "Essai", 0, 0);
		VMObject vm = new VMObject(host);
		return host;
	}
	
	/**
	 * Returns the <code>EditPartFactory</code> that the
	 * <code>GraphicalViewer</code> will use.
	 * @return the <code>EditPartFactory</code>
	 */
	protected EditPartFactory getEditPartFactory(){
		return new IC2DEditPartFactory();
	}
	
	public void setFocus() {
		// TODO Auto-generated method stub
	}	
}
