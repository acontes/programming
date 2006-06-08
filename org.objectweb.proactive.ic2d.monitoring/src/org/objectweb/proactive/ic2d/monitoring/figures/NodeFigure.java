package org.objectweb.proactive.ic2d.monitoring.figures;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;

public class NodeFigure extends AbstractRectangleFigure{
		
    //
    // -- CONSTRUCTOR -----------------------------------------------
    //
	public NodeFigure(String text) {
		super(text,BorderLayout.TOP,130,180);
	}
	
	public void addFigureChild(AbstractFigure child){
		if(this.children.size() != 0 && this != child){
			AOFigure lastChild = (AOFigure)children.get(children.size()-1);
			child.setLocation(lastChild.getLocation().getTranslated(new Point(0, 70)));
			add(child);
		}
		else
			super.addFigureChild(child);
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
}
