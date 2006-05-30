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

import java.awt.Color;

import org.objectweb.proactive.ic2d.data.HostObject;

public class HostPanel extends AbstractDataObjectPanel {
	
	/** The Object corresponding to this graphical interface (the model) */
	private HostObject hostObject;
	
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	public HostPanel(AbstractDataObjectPanel parentPanel, HostObject hostObject) {
		super(parentPanel, "HostObject");
		
		this.hostObject = hostObject;
		
		setBackground(new Color(0xd0, 0xd0, 0xd0));
		createBorder(hostObject.getOperatingSystem());
	}
	
	//
	// -- PUBLICS METHODS -----------------------------------------------
	//
	
	public void destroyObject() {
		destroy();
	}
	
	//
	// -- PRIVATE METHODS -----------------------------------------------
	//
	
	/**
	 * TODO
	 * @param os name of the host's operating system 
	 */
	private void createBorder(String os) {
		if (os == null) {
			os = "OS Undefined";
		}
		setBorder(javax.swing.BorderFactory.createTitledBorder(
				javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1,
						new java.awt.Color(0, 0, 128)), name + ":" + os,
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION, defaultFont));
	}
	
}
