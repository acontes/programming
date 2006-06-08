package org.objectweb.proactive.ic2d.monitoring.figures;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

public abstract class AbstractFigure extends Figure{
	
	protected List children = new ArrayList();
	
	protected Label label = new Label();
	
	protected Color borderColor;
	protected Color backgroundColor;
	protected Color shadowColor;
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	protected AbstractFigure(String text, Integer posText, int xPos, int yPos, int width, int length){
		super();
		
		// Add the label
		BorderLayout layout = new BorderLayout();
		/*layout.setHorizontalSpacing(4);
		 layout.setVerticalSpacing(4);*/
		setLayoutManager(layout);
		label.setForegroundColor(ColorConstants.black);
		add(label, posText);
		
		// Initialisation
		initColor();
		label.setText(text);
		setBounds(new Rectangle(xPos, yPos, width, length));
	}
	
	protected AbstractFigure(String text, Integer textPos, int width, int length){
		this(text, textPos, 0, 0, width, length);
	}
	
	//
	// -- PROTECTED METHODS --------------------------------------------
	//
	
	protected abstract void initColor();
	
	protected abstract void paintIC2DFigure(Graphics graphics);
	
	public abstract ConnectionAnchor getAnchor();
	
	//
	// -- PUBLIC METHODS ---------------------------------------------
	//
	
	public void paintFigure(Graphics graphics){
		super.paintFigure(graphics);
		paintIC2DFigure(graphics);
	}
	
	public void addFigureChild(AbstractFigure child){
		if(this != child){
			children.add(child);
			child.setLocation(this.getLocation().getTranslated(new Point(6, 20)));
			add(child);
		}
	}
}
