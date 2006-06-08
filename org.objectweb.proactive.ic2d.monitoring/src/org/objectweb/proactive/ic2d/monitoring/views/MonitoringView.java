package org.objectweb.proactive.ic2d.monitoring.views;

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

public class MonitoringView extends ViewPart {
	
	public static final String ID = "org.objectweb.proactive.ic2d.monitoring.views.MonitoringView";

	
	private Canvas root;
	private LightweightSystem lws;
	private Figure contents;
	
	
	public void createPartControl(Composite parent) {
        
		GridLayout layout = new GridLayout();
		this.root= new Canvas(parent, SWT.BORDER);
		this.root.setLayout(layout);
		Device device = Display.getCurrent();
		this.root.setBackground(new Color(device, 255,255,255));
		this.lws = new LightweightSystem(this.root);
		this.contents = new Figure();
		
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		//do nothing
	}
}
