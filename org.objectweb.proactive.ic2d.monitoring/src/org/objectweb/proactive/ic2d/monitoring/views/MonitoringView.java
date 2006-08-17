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
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.ic2d.monitoring.actions.HorizontalLayoutAction;
import org.objectweb.proactive.ic2d.monitoring.actions.MonitoringContextMenuProvider;
import org.objectweb.proactive.ic2d.monitoring.actions.NewHostAction;
import org.objectweb.proactive.ic2d.monitoring.actions.RefreshAction;
import org.objectweb.proactive.ic2d.monitoring.actions.RefreshHostAction;
import org.objectweb.proactive.ic2d.monitoring.actions.RefreshJVMAction;
import org.objectweb.proactive.ic2d.monitoring.actions.RefreshNodeAction;
import org.objectweb.proactive.ic2d.monitoring.actions.SetDepthAction;
import org.objectweb.proactive.ic2d.monitoring.actions.SetTTRAction;
import org.objectweb.proactive.ic2d.monitoring.actions.SetUpdateFrequenceAction;
import org.objectweb.proactive.ic2d.monitoring.actions.StopMonitoringAction;
import org.objectweb.proactive.ic2d.monitoring.actions.VerticalLayoutAction;
import org.objectweb.proactive.ic2d.monitoring.data.MonitorThread;
import org.objectweb.proactive.ic2d.monitoring.data.WorldObject;
import org.objectweb.proactive.ic2d.monitoring.editparts.IC2DEditPartFactory;
import org.objectweb.proactive.ic2d.monitoring.figures.listeners.WorldListener;


public class MonitoringView extends ViewPart {

	public static final String ID = "org.objectweb.proactive.ic2d.monitoring.views.MonitoringView";

	private static MonitoringView instance = null; 
	
	/** the graphical viewer */
	private MonitoringViewer graphicalViewer;
	
	/** the overview outline page */
    //private OverviewOutlinePage overviewOutlinePage;
	
	
	//
	// -- CONSTRUCTOR ----------------------------------------------
	//
	
	public MonitoringView () {
		super();
		instance = this;
	}
	
	//
	// -- PUBLIC METHODS ----------------------------------------------
	//

	public static MonitoringView getInstance() {
		return instance;
	}
	
	@Override
	public void createPartControl(Composite parent){
		
		FormLayout form = new FormLayout ();
		parent.setLayout (form);
		
		final int limit = 50;
		
		VirtualNodesGroup virtualNodesGroup = new VirtualNodesGroup(parent);
        WorldObject.getInstance().addObserver(virtualNodesGroup);
        FormData vnData = new FormData();
        vnData.left = new FormAttachment (0, 0);
		vnData.right = new FormAttachment (100, 0);
		vnData.top = new FormAttachment (0, 0);
		vnData.bottom = new FormAttachment (0, limit);
		virtualNodesGroup.getGroup().setLayoutData(vnData);
		
		createGraphicalViewer(parent);
		
		FormData graphicalViewerData = new FormData ();
		graphicalViewerData.left = new FormAttachment (0, 0);
		graphicalViewerData.right = new FormAttachment (100, 0);
		graphicalViewerData.top = new FormAttachment (virtualNodesGroup.getGroup(), 0);
		graphicalViewerData.bottom = new FormAttachment (100, 0);
		graphicalViewer.getControl().setLayoutData(graphicalViewerData);
		
		
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		
		// Adds refresh action to the view's toolbar
		RefreshAction toolBarRefresh = new RefreshAction();
		toolBarManager.add(toolBarRefresh);
		
		// Adds Zoom-in and Zoom-out actions to the view's toolbar
		ZoomManager zoomManager = ((ScalableFreeformRootEditPart)graphicalViewer.getRootEditPart()).getZoomManager();
		zoomManager.setZoomLevels(new double[]{0.25, 0.5, 0.75, 1.0, 1.5});
		
		ZoomInAction zoomIn = new ZoomInAction(zoomManager);
		zoomIn.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "zoom-in-2.gif"));
		graphicalViewer.getActionRegistry().registerAction(zoomIn);
		toolBarManager.add(zoomIn);
		
		ZoomOutAction zoomOut = new ZoomOutAction(zoomManager);
		zoomOut.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "zoom-out-2.gif"));
		graphicalViewer.getActionRegistry().registerAction(zoomIn);
		toolBarManager.add(zoomOut);
		
		
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

	
    /**
     * Returns the <code>GraphicalViewer</code> of this editor.
     * @return the <code>GraphicalViewer</code>
     */
    public MonitoringViewer getGraphicalViewer() {
        return graphicalViewer;
    }
    

	public void setFocus() {
		// TODO Auto-generated method stub
	}

	
	/* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
 /*   public Object getAdapter(Class adapter) {
    	if (adapter == IContentOutlinePage.class) {
            return getOverviewOutlinePage();
    	}
    	// the super implementation handles the rest
        return super.getAdapter(adapter);
    }*/
	
	
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


    /**
     * Returns the overview for the outline view.
     * 
     * @return the overview
     */
   /* protected OverviewOutlinePage getOverviewOutlinePage() {
        if (null == overviewOutlinePage && null != getGraphicalViewer()) {
            RootEditPart rootEditPart = getGraphicalViewer().getRootEditPart();
            if (rootEditPart instanceof ScalableFreeformRootEditPart) {
                overviewOutlinePage =
                    new OverviewOutlinePage((ScalableFreeformRootEditPart) rootEditPart);
            }
        }

        return overviewOutlinePage;
    }*/
	
    //
	// -- PRIVATE METHODS -------------------------------------------
	//
    
	private void createGraphicalViewer(Composite parent) {
		// create graphical viewer
		graphicalViewer = new MonitoringViewer();
		graphicalViewer.createControl(parent);

		// configure the viewer
		graphicalViewer.getControl().setBackground(ColorConstants.white);
		ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart();
		root.getFigure().addMouseListener(new WorldListener());
		graphicalViewer.setRootEditPart(root);

		// activate the viewer as selection provider for Eclipse
		getSite().setSelectionProvider(graphicalViewer);

		// initialize the viewer with input
		graphicalViewer.setEditPartFactory(getEditPartFactory());
		WorldObject world = WorldObject.getInstance();
		world.addObserver(MonitorThread.getInstance());
		graphicalViewer.setContents(world);
		
		initContextMenu(parent.getDisplay());
		ContextMenuProvider contextMenu = new MonitoringContextMenuProvider(graphicalViewer);
		graphicalViewer.setContextMenu(contextMenu);
		getSite().registerContextMenu(contextMenu, graphicalViewer);
	}
	
	private void initContextMenu(Display display) {
		ActionRegistry registry = graphicalViewer.getActionRegistry();
		
		registry.registerAction(new NewHostAction(display));
		registry.registerAction(new SetDepthAction(display));
		registry.registerAction(new RefreshAction());
		registry.registerAction(new SetTTRAction(display));
		registry.registerAction(new RefreshHostAction());
		registry.registerAction(new RefreshJVMAction());
		registry.registerAction(new RefreshNodeAction());
		registry.registerAction(new StopMonitoringAction());
		registry.registerAction(new SetUpdateFrequenceAction(display));
		registry.registerAction(new VerticalLayoutAction());
		registry.registerAction(new HorizontalLayoutAction());
	}
    
	//
	// -- INNER CLASSES -------------------------------------------
	//
	
	
	public class MonitoringViewer extends ScrollingGraphicalViewer {
		
		private ActionRegistry registry;
		
		public MonitoringViewer() {
			this.registry = new ActionRegistry();
		}
		
		public ActionRegistry getActionRegistry () {
			return this.registry;
		}
		
	}
}
