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

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.EllipseAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;

public class AOFigure extends AbstractFigure {
	
	protected final static int DEFAULT_WIDTH = 40;
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	public AOFigure(String text){
		super(text);
		
		
		System.out.println("AOFigure : constructor");
		addMouseMotionListener(new AOListener());
	}
	
	//
	// -- PUBLIC METHODS ----------------------------------------------
	//
	public ConnectionAnchor getAnchor() {
		return new EllipseAnchor(this)/*ChopboxAnchor(this)*/;
	}
	
	public void paintIC2DFigure(Graphics graphics){
		// Inits
		Rectangle bounds = this.getBounds().getCopy().resize(-1, -2);
		// Shadow
		if(showShadow){
			graphics.setBackgroundColor(shadowColor);
			graphics.fillOval(this.getBounds().getTranslated(4, 4));
		}
		
		// Drawings
		graphics.setForegroundColor(this.borderColor);
		graphics.setBackgroundColor(this.backgroundColor);
		graphics.fillOval(bounds);
		graphics.drawOval(bounds);
		
		// Cleanups
		graphics.restoreState();
	}
	
	public IFigure getContentPane() {
		System.out.println("AOFigure : getContentPane");
		return this;
	}
	
	//
	// -- PROTECTED METHODS -------------------------------------------
	//
	
	protected void initColor() {
		Device device = Display.getCurrent();
		borderColor = new Color(device, 0, 0, 128);
		backgroundColor = new Color(device, 225, 225, 225);
		shadowColor = new Color(device, 230, 230, 230);
	}
	
	protected void initFigure(){
		LayoutManager layout = new AOBorderLayout();
		setLayoutManager(layout);
		add(label, BorderLayout.CENTER);
	}
	
	//
	// -- INNER CLASS -------------------------------------------
	//
	
	private class AOBorderLayout extends BorderLayout {
		
		protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint) {
			return super.calculatePreferredSize(container, wHint, hHint).expand(15, 15);
		}
		
	}
	
}
