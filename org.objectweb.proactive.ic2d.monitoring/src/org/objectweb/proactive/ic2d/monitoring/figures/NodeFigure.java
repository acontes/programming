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
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;
import org.objectweb.proactive.ic2d.monitoring.data.Protocol;

public class NodeFigure extends AbstractRectangleFigure{
		
	protected final static int DEFAULT_WIDTH = 17;
	
	private IFigure contentPane;
	
	public static final Color RMI_COLOR;
	
	static {
		Display device = Display.getCurrent();
		RMI_COLOR = new Color(device, 208, 208, 224);
	}
	
    //
    // -- CONSTRUCTOR -----------------------------------------------
    //
	
	/**
	 * Create a new node figure
	 * @param text The text to display
	 * @param protocol The protocol used
	 */
	public NodeFigure(String text, Protocol protocol) {
		super(text);
		addMouseMotionListener(new NodeListener());
		setProtocol(protocol);
	}
	
	/**
	 * Used to display the legend
	 * @param protocol The protocol used
	 */
	public NodeFigure(Protocol protocol){
		super("Node");
		setProtocol(protocol);
	}
	//
    // -- PUBLIC METHOD --------------------------------------------
    //
	
	public IFigure getContentPane() {
		return contentPane;
	}
	
	public void setProtocol(Protocol protocol){
		switch(protocol) {
		case IBIS:
			// TODO
		case RMI:
			backgroundColor = RMI_COLOR;
			break;
		case RMISSH:
			backgroundColor = ColorConstants.white;
			break;
		case JINI:
			backgroundColor = ColorConstants.cyan;
			break;
		case HTTP:
			backgroundColor = ColorConstants.orange;
			break;
		default:
			// TODO
		}
	}
	
    //
    // -- PROTECTED METHODS --------------------------------------------
    //
	protected void initColor() {
		Device device = Display.getCurrent();
		borderColor = new Color(device, 0, 0, 128);
		shadowColor = new Color(device, 230, 230, 230);
	}

	protected void initFigure() {
		BorderLayout layout = new NodeBorderLayout();
		layout.setVerticalSpacing(5);
		setLayoutManager(layout);

		add(label, BorderLayout.TOP);
		
		contentPane = new Figure();
		ToolbarLayout contentPaneLayout = new NodeToolbarLayout();
		contentPaneLayout.setSpacing(5);
		contentPaneLayout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		contentPane.setLayoutManager(contentPaneLayout);
		add(contentPane, BorderLayout.CENTER);
	}
	
	@Override
	protected int getDefaultWidth() {
		return DEFAULT_WIDTH;
	}
	
	//
    // -- INNER CLASS --------------------------------------------
    //
	
	private class NodeBorderLayout extends BorderLayout {
		
		protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint){
			return super.calculatePreferredSize(container, wHint, hHint).expand(25,0);
		}
	}
	
	private class NodeToolbarLayout extends ToolbarLayout {
		
		public NodeToolbarLayout() {
			super(false);
		}

		
		protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint){
			return super.calculatePreferredSize(container, wHint, hHint).expand(10,15);
		}
		
	}
}
