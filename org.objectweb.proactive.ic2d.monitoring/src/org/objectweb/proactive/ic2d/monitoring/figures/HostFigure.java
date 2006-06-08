package org.objectweb.proactive.ic2d.monitoring.figures;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;

public class HostFigure extends AbstractRectangleFigure{
	
    //
    // -- CONSTRUCTOR -----------------------------------------------
    //
	public HostFigure(String text, int xPos, int yPos) {
		super(text,BorderLayout.TOP,xPos,yPos,160,260);
		new Dragger(this);
	}

    //
    // -- PROTECTED METHOD --------------------------------------------
    //
	protected void initColor() {
		Device device = Display.getCurrent();
		borderColor = new Color(device, 0, 0, 128);
		backgroundColor = new Color(device, 208, 208, 208);
		shadowColor = new Color(device, 230, 230, 230);
	}
}
