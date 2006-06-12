package org.objectweb.proactive.ic2d.monitoring.figures;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;

public class VMFigure extends AbstractRectangleFigure{

    //
    // -- CONSTRUCTOR -----------------------------------------------
    //
	public VMFigure(HostFigure parent, String text) {
		super(parent, text,BorderLayout.TOP,145,50);
		addMouseMotionListener(new VMListener());
	}

    //
    // -- PROTECTED METHOD --------------------------------------------
    //
	protected void initColor() {
		Device device = Display.getCurrent();
		borderColor = new Color(device, 140, 200, 225);
		backgroundColor = new Color(device, 240, 240, 240);
		shadowColor = new Color(device, 230, 230, 230);
	}

	public String toString() {
		return "VM";
	}
}
