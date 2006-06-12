package org.objectweb.proactive.ic2d.monitoring.views;


import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.ic2d.monitoring.figures.AOFigure;
import org.objectweb.proactive.ic2d.monitoring.figures.HostFigure;
import org.objectweb.proactive.ic2d.monitoring.figures.IC2DConnection;
import org.objectweb.proactive.ic2d.monitoring.figures.NodeFigure;
import org.objectweb.proactive.ic2d.monitoring.figures.VMFigure;

public class MonitoringView extends ViewPart {
	
	public static final String ID = "org.objectweb.proactive.ic2d.monitoring.views.MonitoringView";

	
	private Canvas root;
	private LightweightSystem lws;
	private Figure panel;
	
	
	public void createPartControl(Composite parent) {
        
		GridLayout layout = new GridLayout();
		this.root= new Canvas(parent, SWT.BORDER);
		this.root.setLayout(layout);
		Device device = Display.getCurrent();
		this.root.setBackground(new Color(device, 255,255,255));
		this.lws = new LightweightSystem(this.root);
		this.panel = new Figure();
		this.lws.setContents(panel);
		
		/*-------- Host test -----------*/
		HostFigure host = new HostFigure("trans12:1099:linux", 120, 100);
		panel.add(host);
		/*---------------------------------*/
		
		
		/*--------- VM test ------------*/
		VMFigure vm = new VMFigure(host,"VM id=11d1def534e");
		/*---------------------------------*/
		
		
		/*-------- Node test -----------*/
		NodeFigure node = new NodeFigure(vm,"Node1058928077");
		/*---------------------------------*/
		
		
		/*-------- Active object test -----*/
		AOFigure ao1 = new AOFigure(node,"Test#1");
		
		AOFigure ao2 = new AOFigure(node,"Test#2");
		/*---------------------------------*/
		
		//////////////////////////////////////////////////////////////////////////
		/*-------- Host test -----------*/
		HostFigure host2 = new HostFigure("trans10:1099:linux", 350, 100);
		panel.add(host2);
		/*---------------------------------*/
		
		
		/*--------- VM test ------------*/
		VMFigure vm2 = new VMFigure(host2,"VM id=25d7tee974a");
		/*---------------------------------*/
		
		
		/*-------- Node test -----------*/
		NodeFigure node2 = new NodeFigure(vm2,"Node107433708");
		/*---------------------------------*/
		
		
		/*-------- Active object test -----*/
		AOFigure ao3 = new AOFigure(node2,"Test#3");
		/*---------------------------------*/
		
		
		
		/* The road */
		FanRouter router = new FanRouter();
		router.setSeparation(40);

		////////// Test Connection ///////
		IC2DConnection conn1 = new IC2DConnection();
		conn1.setSourceAnchor(ao1.getAnchor());
		conn1.setTargetAnchor(ao2.getAnchor());
		conn1.setConnectionRouter(router);
		panel.add(conn1);
		
		IC2DConnection conn2 = new IC2DConnection();
		conn2.setSourceAnchor(ao1.getAnchor());
		conn2.setTargetAnchor(ao3.getAnchor());
		conn2.setConnectionRouter(router);
		panel.add(conn2);
		
		//////////////////////////////////
		
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		//do nothing
	}
}
