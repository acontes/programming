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
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.ic2d.monitoring.data.MonitorThread;
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
		// create graphical viewer
		graphicalViewer = new ScrollingGraphicalViewer();
		graphicalViewer.createControl(parent);

		// configure the viewer
		graphicalViewer.getControl().setBackground(ColorConstants.white);
		graphicalViewer.setRootEditPart(new ScalableFreeformRootEditPart());

		// activate the viewer as selection provider for Eclipse
		getSite().setSelectionProvider(graphicalViewer);

		// initialize the viewer with input
		graphicalViewer.setEditPartFactory(new IC2DEditPartFactory());
		WorldObject world = WorldObject.getInstance();
		world.addObserver(MonitorThread.getInstance());
		graphicalViewer.setContents(world);

		// Adds Zoom-in and Zoom-out actions to the view's toolbar		
		getViewSite().getActionBars().getToolBarManager().add(new ZoomIn());
		getViewSite().getActionBars().getToolBarManager().add(new ZoomOut());

		//graphicalViewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.NONE), MouseWheelZoomHandler.SINGLETON);

//		FigureCanvas fc = (FigureCanvas)graphicalViewer/*root.getViewer()*/.getControl();
//
//		if(fc != null) {
//			fc.addListener(SWT.MouseWheel, new Listener() {
//				public void handleEvent(Event event) {
//					ActionRegistry registry = getActionRegistry();
//					IAction action = registry.getAction((event.count > 0) ? 
//							GEFActionConstants.ZOOM_IN :
//							GEFActionConstants.ZOOM_OUT);
//
//					action.run();
//				}
//			});
//		} 
	}


	public void setFocus() {
		// TODO Auto-generated method stub
	}

	//
	// -- PROTECTED METHODS -------------------------------------------
	//

	/**
	 * Returns the <code>EditPartFactory</code> that the
	 * <code>GraphicalViewer</code> will use.
	 * @return the <code>EditPartFactory</code>
	 */
	protected EditPartFactory getEditPartFactory(){
		return new IC2DEditPartFactory();
	}

	//
	// -- INNER CLASSES -------------------------------------------
	//

	public class ZoomIn extends ZoomInAction {

		public ZoomIn() {
			super(((ScalableFreeformRootEditPart)graphicalViewer.getRootEditPart()).getZoomManager());
			this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "zoom-in-2.gif"));
		}

		public void init(IWorkbenchWindow window) {/*do nothing*/}

		public void run(IAction action) {
			super.run();
		}

		public void selectionChanged(IAction action, ISelection selection) {/*do nothing*/}

		public void dispose() {
			super.dispose();
		}
	}

	public class ZoomOut extends ZoomOutAction {

		public ZoomOut() {
			super(((ScalableFreeformRootEditPart)graphicalViewer.getRootEditPart()).getZoomManager());
			this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "zoom-out-2.gif"));
		}

		public void init(IWorkbenchWindow window) {/*do nothing*/}

		public void run(IAction action) {
			super.run();
		}

		public void selectionChanged(IAction action, ISelection selection) {/*do nothing*/}

		public void dispose() {
			super.dispose();
		}
	}

}
