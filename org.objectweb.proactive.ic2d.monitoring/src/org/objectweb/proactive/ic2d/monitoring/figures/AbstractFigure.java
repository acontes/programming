package org.objectweb.proactive.ic2d.monitoring.figures;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

public abstract class AbstractFigure extends Figure{
	
	/* The shift to do when we add the first child */
	private int horizontalShift = 6;
	private int verticalShift = 20;
	private int defaultWidth;
	private int defaultHeight;
	
	protected List children = new ArrayList();
	protected AbstractFigure parent;
	
	protected Label label = new Label();
	
	/* Colors to use */
	protected Color borderColor;
	protected Color backgroundColor;
	protected Color shadowColor;
	
	protected static boolean showShadow = false; 
	
		
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	protected AbstractFigure(String text, Integer posText, int xPos, int yPos, int width, int height){
		super();
		
		this.defaultWidth = width;
		this.defaultHeight = height;
		
			
		// Add the label
		BorderLayout layout = new BorderLayout();
		
		
		/*FlowLayout layout = new FlowLayout();
		 layout.setHorizontal(false);
		 layout.setMinorAlignment(FlowLayout.ALIGN_CENTER);*/
		
		//layout.setMinorSpacing(20);
		
		
		/*layout.setHorizontalSpacing(4);
		 layout.setVerticalSpacing(4);*/
		setLayoutManager(layout);
		label.setForegroundColor(ColorConstants.black);
		add(label, posText);
		
		// Initialisation
		initColor();
		label.setText(text);
		setBounds(new Rectangle(xPos, yPos, this.defaultWidth, this.defaultHeight));
		
		/* Add child to his parent */
		/*this.parent = parent;
		 if(this.parent != null){
		 this.parent.addFigureChild(this);
		 }*/
	}
	
	protected AbstractFigure(String text, Integer textPos, int width, int height){
		this(text, textPos, 0, 0, width, height);
	}
	
	//
	// -- PROTECTED METHODS --------------------------------------------
	//
	
	protected abstract void initColor();
	
	protected abstract void paintIC2DFigure(Graphics graphics);
	
	/* Update his size according to his child */
	protected void updateSize(AbstractFigure child){
		Dimension childDimension = child.getSize();
		childDimension.expand(2*this.horizontalShift,2*this.verticalShift);
		this.setSize(childDimension);
		
		if(this.parent != null)
			this.parent.updateSize(this);
	}
	
	protected void addFigureChild(AbstractFigure child){
		if(this != child){
			children.add(child);
			this.updateSize(child);
			child.setLocation(this.getLocation().getTranslated(new Point(this.horizontalShift, this.verticalShift)));
			
			add(child);
		}
	}
	
	//
	// -- PUBLIC METHODS ---------------------------------------------
	//
	
	public abstract String toString();
	
	public abstract ConnectionAnchor getAnchor();
	
	public void paintFigure(Graphics graphics){
		super.paintFigure(graphics);
		paintIC2DFigure(graphics);
	}
	
	public void setTitle(String title){
		this.label.setText(title);
	}
	
	public IFigure getContentPane(){
		//TODO
		return null;
	}
}
