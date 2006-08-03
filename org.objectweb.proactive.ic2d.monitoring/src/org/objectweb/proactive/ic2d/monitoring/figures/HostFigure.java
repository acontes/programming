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
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;

public class HostFigure extends AbstractRectangleFigure{
	
	protected final static int DEFAULT_WIDTH = 25;
	
	private IFigure contentPane;
	
	private final static Color DEFAULT_BORDER_COLOR;
	
	static {
		Display device = Display.getCurrent();
		DEFAULT_BORDER_COLOR = new Color(device, 0, 0, 128);
	}
	
    //
    // -- CONSTRUCTOR -----------------------------------------------
    //
	public HostFigure(String text) {
		super(text, null);
		new Dragger(this);
	}
	
	/**
	 * Used to display the legend.
	 *
	 */
	public HostFigure() {
		super();
	}
	
	//
    // -- PUBLIC METHOD --------------------------------------------
    //
	
	public IFigure getContentPane() {
		return contentPane;
	}
	
    //
    // -- PROTECTED METHODS --------------------------------------------
    //
	protected void initColor() {
		Device device = Display.getCurrent();
		borderColor = DEFAULT_BORDER_COLOR;
		backgroundColor = new Color(device, 208, 208, 208);
		shadowColor = new Color(device, 230, 230, 230);
	}

	protected void initFigure() {
		BorderLayout layout = new HostBorderLayout();
		//layout.setHorizontalSpacing(10);
		//layout.setVerticalSpacing(5);
		setLayoutManager(layout);
		add(label, BorderLayout.TOP);
		
		contentPane = new Figure();
		ToolbarLayout contentPaneLayout = new HostToolbarLayout();
		contentPaneLayout.setSpacing(5);
		contentPaneLayout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		//contentPaneLayout.setStretchMinorAxis(false);
		contentPane.setLayoutManager(contentPaneLayout);
		
		add(contentPane, BorderLayout.CENTER);
	}

	@Override
	protected int getDefaultWidth() {
		return DEFAULT_WIDTH;
	}
	
	@Override
	protected Color getDefaultBorderColor() {
		// TODO Auto-generated method stub
		return null;
	}
	
	//
    // -- INNER CLASS --------------------------------------------
    //
	
	private class HostBorderLayout extends BorderLayout {
		
		protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint){
			if(legend)
				return super.calculatePreferredSize(container, wHint, hHint).expand(100, 10);
			
			return super.calculatePreferredSize(container, wHint, hHint).expand(10,0);
		}
		
	}
	
	private class HostToolbarLayout extends ToolbarLayout {
		
		public HostToolbarLayout(){
			super(true);
		}
		
		protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint){
			return super.calculatePreferredSize(container, wHint, hHint).expand(0, 13);
		}
		
		public void layout(IFigure figure) {
			super.layout(figure);
			figure.translate(5, 0);
		}
		
		
	}
}
