package org.objectweb.proactive.ic2d.monitoring.figures;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;

public class NodeFigure extends AbstractRectangleFigure{
		
    //
    // -- CONSTRUCTOR -----------------------------------------------
    //
	public NodeFigure(String text) {
		super(text,BorderLayout.TOP,130,50);
		addMouseMotionListener(new NodeListener());
	}
		
    //
    // -- PROTECTED METHOD --------------------------------------------
    //
	protected void initColor() {
		Device device = Display.getCurrent();
		borderColor = new Color(device, 0, 0, 128);
		backgroundColor = new Color(device, 208, 208, 224);
		shadowColor = new Color(device, 230, 230, 230);
	}

	public String toString() {
		return "Node";
	}
}
