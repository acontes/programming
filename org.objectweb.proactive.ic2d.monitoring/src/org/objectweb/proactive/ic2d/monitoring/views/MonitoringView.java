package org.objectweb.proactive.ic2d.monitoring.views;


import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.ic2d.monitoring.data.HostObject;
import org.objectweb.proactive.ic2d.monitoring.data.VMObject;
import org.objectweb.proactive.ic2d.monitoring.data.WorldObject;
import org.objectweb.proactive.ic2d.monitoring.editparts.IC2DEditPartFactory;

public class MonitoringView extends ViewPart {
	
	public static final String ID = "org.objectweb.proactive.ic2d.monitoring.views.MonitoringView";
	
	
	private Canvas root;
	private LightweightSystem lws;
	private Figure panel;
	
	
	/** the graphical viewer */
	private ScrollingGraphicalViewer graphicalViewer;
	
	public void createPartControl(Composite parent){
		
		GridLayout layout = new GridLayout();
		this.root= new Canvas(parent, SWT.BORDER);
		this.root.setLayout(layout);
		Device device = Display.getCurrent();
		this.root.setBackground(new Color(device, 255,255,255));
		this.lws = new LightweightSystem(this.root);
		this.panel = new Figure();
		this.lws.setContents(panel);
		
		
		initializeGraphicalViewer();
		getGraphicalViewer().createControl(parent);
		getViewSite().setSelectionProvider(getGraphicalViewer());
		getSite().setSelectionProvider(getGraphicalViewer());
		graphicalViewer.setContents(getContent());
	}
	
	/**
	 * Returns the <code>GraphicalViewer</code> of this editor.
	 * @return the <code>GraphicalViewer</code>
	 */
	public ScrollingGraphicalViewer getGraphicalViewer(){
		if(graphicalViewer == null)
			graphicalViewer = new ScrollingGraphicalViewer();
		return graphicalViewer;
	}
	
	public void initializeGraphicalViewer(){
		getGraphicalViewer().setRootEditPart(new ScalableFreeformRootEditPart());
		getGraphicalViewer().setEditPartFactory(new IC2DEditPartFactory());
	}
	
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
