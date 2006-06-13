/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
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
import org.eclipse.swt.graphics.Color;

public abstract class AbstractFigure extends Figure{
	
	// the space between top and the first child
	private final static int topShift = 25;
	// The space between borders and children
	private final static int shift = 10;
	
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
	protected AbstractFigure(AbstractFigure parent, String text, Integer textPos, int width, int height){
		super();
		
		BorderLayout layout = new BorderLayout();
		setLayoutManager(layout);
		
		// Add the label
		label.setForegroundColor(ColorConstants.black);
		add(label, textPos);
		
		// Initialisation
		initColor();
		label.setText(text);
		this.setSize(width, height);
		
		/* Add child to his parent */
		this.parent = parent;
		if(this.parent != null)
			this.parent.addFigureChild(this);
	}
	
		
	protected AbstractFigure(AbstractFigure parent, String text, Integer textPos, int x, int y, int width){
		this(parent, text, textPos, width, topShift);
		this.setLocation(new Point(x, y));
	}
	
	protected AbstractFigure(AbstractFigure parent, String text, Integer textPos, int width){
		this(parent, text, textPos, width, topShift);
	}
	
	//
	// -- PUBLIC METHODS ---------------------------------------------
	//
	
	public abstract ConnectionAnchor getAnchor();
	
	public void paintFigure(Graphics graphics){
		super.paintFigure(graphics);
		paintIC2DFigure(graphics);
	}
	
	public void setTitle(String title){
		this.label.setText(title);
	}
	
	//
	// -- PROTECTED METHODS --------------------------------------------
	//
	
	protected abstract void initColor();
	
	protected abstract void paintIC2DFigure(Graphics graphics);
	
	/* Update his size according to his child */
	protected void updateSize(){
		int height = topShift;
		for(int i=0 ; i<children.size() ; i++) {
			height += ((AbstractFigure)children.get(i)).getSize().height + shift;
		}
		this.setSize(this.getSize().width, height);
		if(this.parent != null)
			this.parent.updateSize();
	}
	
	protected void addFigureChild(AbstractFigure child){
		if(this != child){
			children.add(child);
			child.setLocation(this.getLocation().getTranslated(shift, this.bounds.height));
			this.setSize(this.getSize().width, this.getSize().height + child.getSize().height + shift);
			if(this.parent != null)
				this.parent.updateSize();
			add(child);
		}
	}

}
