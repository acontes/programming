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
package org.objectweb.proactive.ic2d.gui;

import javax.swing.JFrame;

import org.objectweb.proactive.ic2d.controller.IC2DListener;

public class IC2DFrame  extends JFrame {

	/** the frame's default width and height */
	private static final int DEFAULT_WIDTH = 850;
    private static final int DEFAULT_HEIGHT = 600;
	
	
	//
    // -- CONSTRUCTORS -----------------------------------------------
    //
	
	/**
	 * TODO comment
	 */
	public IC2DFrame() {
		
		/* sets the frame's title */
		super("IC2D - MVC");
		
		/* sets the frame's dimensions */
		this.setSize(new java.awt.Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		
		this.addWindowListener(new IC2DListener());
	}
	
}
