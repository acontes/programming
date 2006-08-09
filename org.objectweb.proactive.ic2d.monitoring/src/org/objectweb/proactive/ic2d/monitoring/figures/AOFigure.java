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
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;
import org.objectweb.proactive.ic2d.monitoring.data.State;
import org.objectweb.proactive.ic2d.monitoring.figures.listeners.AOListener;

public class AOFigure extends AbstractFigure {

	protected final static int DEFAULT_WIDTH = 40;

	public static final Color COLOR_WHEN_WAITING_FOR_REQUEST;
	public static final Color COLOR_WHEN_WAITING_BY_NECESSITY;
	public static final Color COLOR_WHEN_ACTIVE;
	public static final Color COLOR_WHEN_SERVING_REQUEST;
	public static final Color COLOR_WHEN_MIGRATING;
	public static final Color COLOR_WHEN_NOT_RESPONDING;

	private static final Color DEFAULT_BORDER_COLOR;
	
	static {
		Display device = Display.getCurrent();
		COLOR_WHEN_WAITING_FOR_REQUEST = new Color(device, 225, 225, 225);
		COLOR_WHEN_WAITING_BY_NECESSITY = new Color(device, 255, 205, 110);
		COLOR_WHEN_ACTIVE = new Color(device, 180, 255, 180);// green
		COLOR_WHEN_SERVING_REQUEST = new Color(device, 255, 255, 255);
		COLOR_WHEN_MIGRATING = new Color(device, 0, 0, 255);// blue
		COLOR_WHEN_NOT_RESPONDING = new Color(device, 255, 0, 0);// red
		
		DEFAULT_BORDER_COLOR = new Color(device, 200, 200, 200);
	}

	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	/**
	 * @param text Text to display
	 */
	public AOFigure(String text){
		super(text);
		addMouseListener(new AOListener());
	}
	
	/**
	 * Used to display the legend.
	 * @param state
	 */
	public AOFigure(int state){
		super();
		this.setState(state);
	}

	//
	// -- PUBLIC METHODS ----------------------------------------------
	//
	
	public ConnectionAnchor getAnchor() {
		return new /*EllipseAnchor(this)/*ChopboxAnchor(this)*/Anchor(this);
	}

	public void paintIC2DFigure(Graphics graphics){
		// Inits
		Rectangle bounds = this.getBounds().getCopy().resize(-1, -2);
		// Shadow
		if(showShadow){
			graphics.setBackgroundColor(shadowColor);
			graphics.fillOval(bounds.getTranslated(4, 4));
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


	public void setState(int state){
		switch (state) {
		// busy
		case State.SERVING_REQUEST:
			this.backgroundColor = AOFigure.COLOR_WHEN_SERVING_REQUEST;
			break;
			// waiting by necessity
		case State.WAITING_BY_NECESSITY_WHILE_ACTIVE:
		case State.WAITING_BY_NECESSITY_WHILE_SERVING:
			this.backgroundColor = AOFigure.COLOR_WHEN_WAITING_BY_NECESSITY;
			break;
			// waiting for request
		case State.WAITING_FOR_REQUEST:
			this.backgroundColor = AOFigure.COLOR_WHEN_WAITING_FOR_REQUEST;
			break;	
			// active
		case State.ACTIVE:
			this.backgroundColor = AOFigure.COLOR_WHEN_ACTIVE;
			break;
			// not responding
		case State.NOT_RESPONDING:
			this.backgroundColor = AOFigure.COLOR_WHEN_NOT_RESPONDING;
			break;
			// migrate
		case State.MIGRATING:
			this.backgroundColor = AOFigure.COLOR_WHEN_MIGRATING;
			break;
		default:
			break;
		}
		this.repaint();
	}

	//
	// -- PROTECTED METHODS -------------------------------------------
	//

	protected void initColor() {
		Device device = Display.getCurrent();
		
		borderColor = DEFAULT_BORDER_COLOR;
		backgroundColor = new Color(device, 225, 225, 225);
		shadowColor = new Color(device, 230, 230, 230);
	}

	protected void initFigure(){
		LayoutManager layout = new AOBorderLayout();
		setLayoutManager(layout);
		add(label, BorderLayout.CENTER);
	}

	@Override
	protected Color getDefaultBorderColor() {
		return DEFAULT_BORDER_COLOR;
	}
	
	//
	// -- INNER CLASS -------------------------------------------
	//

	private class AOBorderLayout extends BorderLayout {

		protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint) {
			//return super.calculatePreferredSize(container, wHint, hHint).expand(15, 15);
			return new Dimension(100,super.calculatePreferredSize(container, wHint, hHint).expand(0, 15).height);
		}

	}

}
