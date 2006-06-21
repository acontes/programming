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

import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Display;

public class VMFigure extends AbstractRectangleFigure{

	protected final static int DEFAULT_WIDTH = 160;
	
    //
    // -- CONSTRUCTOR -----------------------------------------------
    //
	
	public VMFigure(HostFigure parent, String text) {
		super(parent, text);
		addMouseMotionListener(new VMListener());
	}

    //
    // -- PROTECTED METHOD --------------------------------------------
    //
	
	protected void initColor() {
		Device device = Display.getCurrent();
		borderColor = new Color(device, 140, 200, 225);
		backgroundColor = new Color(device, 240, 240, 240);
		shadowColor = new Color(device, 230, 230, 230);
	}

	protected void initFigure() {
		IC2DToolbarLayout layout = new IC2DToolbarLayout(false);
		layout.setSpacing(10);
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		setLayoutManager(layout);
		add(label);
	}
}
