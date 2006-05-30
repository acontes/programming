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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import org.objectweb.proactive.ic2d.data.IC2DObject;
import org.objectweb.proactive.ic2d.data.WorldObject;

public class IC2DPanel extends AbstractDataObjectPanel{
	
    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    
    public IC2DPanel(Frame parentFrame, IC2DObject ic2dObject){
    	super(parentFrame, "IC2D");
    	
    	// Set the background color
        setBackground(java.awt.Color.white);
        
        // Create the worldPanel
        WorldPanel worldPanel = new WorldPanel(this, );
        putChild(ic2dObject.getWorldObject().getKey(), worldPanel);
        
        // Set the layout
        setLayout(new java.awt.BorderLayout());
        
        // Create panel to host WorldPanel
        JScrollPane scrollableWorldPanel = new JScrollPane(worldPanel,
        		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollableWorldPanel.setBorder(BorderFactory.createTitledBorder(
                "World Panel"));
        scrollableWorldPanel.setBackground(Color.white);
        add(scrollableWorldPanel, BorderLayout.CENTER);
        
    }
    
    //
    // -- PUBLIC METHODS -----------------------------------------------
    //
    
	public void destroyObject() {
		destroy();		
	}

}