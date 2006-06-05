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
package org.objectweb.proactive.ic2d.gui.monitoring;

import java.awt.Color;
import java.awt.GridLayout;

import org.objectweb.proactive.ic2d.data.VMObject;
import org.objectweb.proactive.ic2d.gui.popupmenu.VMPopupMenu;

public class VMPanel extends AbstractDataObjectPanel {
	
	//
    // -- CONSTRUCTORS -----------------------------------------------
    //
	
	public VMPanel(AbstractDataObjectPanel parent, VMObject vmObject) {
		super(parent, "VM id="/* + vmObject.getVMid() */);
		
		alignLayout(((HostPanel)parent).getAlignLayout());
		createBorder(name, Color.BLACK);
		
		
		VMPopupMenu popup = new VMPopupMenu(this, this.name);
		addMouseListener(popup.getMenuMouseListener());
	}
	
	//
    // -- PUBLIC METHODS -----------------------------------------------
    //
	
	public void destroyObject(){
		destroy();
	}
	
	/**
	 * TODO comment
	 * @param align
	 */
	public void alignLayout(String align) {
        setPreferredSize(null); //TODO ???
        if (align.compareTo("H") == 0) {
            setLayout(new GridLayout(1, 0, 4, 4));
        } else {
            this.setLayout(new GridLayout(0, 1, 4, 4));
        }
        revalidate();
        repaint();
        this.parent.revalidate();
        this.parent.repaint();
	}

}
