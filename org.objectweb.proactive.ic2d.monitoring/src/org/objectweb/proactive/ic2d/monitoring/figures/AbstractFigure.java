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

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Color;

public abstract class AbstractFigure extends Figure{
	
	// the space between top and the first child
	protected final static int topShift = 25;
	// The space between borders and children
	protected final static int shift = 10;
	
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
	protected AbstractFigure(AbstractFigure parent, String text){
		super();
		
		// Initialisation
		this.label = new Label(text);
		initColor();
		initFigure();
		
		/* Add child to his parent */
		this.parent = parent;
		if(this.parent != null)
			this.parent.addFigureChild(this);
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
	
	protected abstract void initFigure();
	
	protected abstract void initColor();
	
	protected abstract void paintIC2DFigure(Graphics graphics);
	
	/* Update his size according to his child */
	protected void updateSize(){
		//layoutContainer.invalidate();
		//container.setSize(this.layoutContainer.getPreferredSize(container,shift, shift));
		//layout.invalidate();
		//setSize(this.layout.getPreferredSize(this,shift, shift));
		
		this.getLayoutManager().invalidate();
		this.setSize(this.getLayoutManager().getPreferredSize(this,shift, shift));
		
		if(this.parent != null)
			this.parent.updateSize();
	}
	
	protected void addFigureChild(AbstractFigure child){
		
		if(this != child){
			children.add(child);
			this.add(child);
			this.updateSize();
		}
	}
	
	protected static ToolbarLayout createToolbarLayout(boolean horizontal){
		IC2DToolbarLayout layout = new IC2DToolbarLayout(horizontal);
		layout.setSpacing(10);
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		return layout;
	}
}
