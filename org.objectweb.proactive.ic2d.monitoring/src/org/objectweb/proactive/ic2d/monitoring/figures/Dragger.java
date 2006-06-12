package org.objectweb.proactive.ic2d.monitoring.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

public class Dragger extends MouseMotionListener.Stub implements MouseListener {
	
	Point last;
	
    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
	public Dragger(IFigure figure){
		figure.addMouseMotionListener(this);
		figure.addMouseListener(this);
	}
	
    //
    // -- PUBLIC METHODS ---------------------------------------------
    //
	
	public void mouseReleased(MouseEvent e){/*Do nothing*/}
	
	public void mouseClicked(MouseEvent e){/*Do nothing*/}
	
	public void mouseDoubleClicked(MouseEvent e){/*Do nothing*/}
	
	public void mousePressed(MouseEvent e){
		last = e.getLocation();
	}
	
	public void mouseDragged(MouseEvent e){
		Point p = e.getLocation();
		if(p!=null && last!=null){
			Dimension delta = p.getDifference(last);
			last = p;
			AbstractFigure f = ((AbstractFigure)e.getSource());

			f.setBounds(f.getBounds().getTranslated(delta.width, delta.height));
		}
		else{
			if(p==null)
				System.err.println("Ooups p est null");
			if(last==null)
				System.err.println("Ooups last est null");
		}
	}
}
