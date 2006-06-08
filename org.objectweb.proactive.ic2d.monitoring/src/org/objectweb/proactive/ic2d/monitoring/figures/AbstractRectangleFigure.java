package org.objectweb.proactive.ic2d.monitoring.figures;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

public abstract class AbstractRectangleFigure extends AbstractFigure{

    //
    // -- CONSTRUCTOR -----------------------------------------------
    //
	protected AbstractRectangleFigure(String text, Integer posText, int posX, int posY, int width, int length){
		super(text,posText, posX, posY, width, length);
	}
	
	protected AbstractRectangleFigure(String text, Integer textPos, int width, int length){
		this(text, textPos,0, 0, width, length);
	}
    //
    // -- PUBLIC METHOD ---------------------------------------------
    //
	public ConnectionAnchor getAnchor() {
		return new ChopboxAnchor(this) {
			protected Rectangle getBox()
			{
				Rectangle base = super.getBox();
				return base.getResized(-4, -4).getTranslated(4, 4);
			}
		};
	}
	
    //
    // -- PROTECTED METHOD --------------------------------------------
    //
	protected void paintIC2DFigure(Graphics graphics) {
		// Inits
		Rectangle bounds = getBounds().getCopy().resize(-7, -9)/*.translate(4, 4)*/;
		final int round = 25;
		final int sround = 30;
		
		// Shadow
		graphics.setBackgroundColor(this.shadowColor);
		graphics.fillRoundRectangle(bounds.getTranslated(4, 4), round, sround);
		
		// Drawings
		graphics.setForegroundColor(this.borderColor);
		graphics.setBackgroundColor(this.backgroundColor);
		graphics.fillRoundRectangle(bounds, round, round);
		graphics.drawRoundRectangle(bounds, round, round);
		
		// Cleanups
		graphics.restoreState();		
	}
}
