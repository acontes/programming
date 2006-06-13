package org.objectweb.proactive.ic2d.monitoring.figures;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.EllipseAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;

public class AOFigure extends AbstractFigure {
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	public AOFigure(String text){
		super(text,BorderLayout.CENTER,110, 40);
		addMouseMotionListener(new AOListener());
	}
	
	//
	// -- PROTECTED METHODS -------------------------------------------
	//
	protected void initColor() {
		Device device = Display.getCurrent();
		borderColor = new Color(device, 0, 0, 128);
		backgroundColor = new Color(device, 225, 225, 225);
		shadowColor = new Color(device, 230, 230, 230);
	}
	
	//
	// -- PUBLIC METHODS ----------------------------------------------
	//
	public ConnectionAnchor getAnchor() {
		return new EllipseAnchor(this)/*ChopboxAnchor(this)*/;
	}
	
	public void paintIC2DFigure(Graphics graphics){
		// Inits
		Rectangle bounds = getBounds().getCopy().resize(-12, -9).translate(4, 4);
		// Shadow
		if(showShadow){
			graphics.setBackgroundColor(shadowColor);
			graphics.fillOval(bounds.getTranslated(4, 4));
		}
		
		// Drawings
		graphics.setForegroundColor(this.borderColor);
		graphics.setBackgroundColor(this.backgroundColor);
		graphics.fillOval(bounds);
		graphics.drawOval(bounds);
		
		// Cleanups
		graphics.restoreState();
	}
	
	public String toString() {
		return "AO";
	}
}
