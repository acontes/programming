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
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.objectweb.proactive.ic2d.data.NodeObject;
import org.objectweb.proactive.ic2d.gui.popupmenu.NodePopupMenu;

public class NodePanel extends AbstractDataObjectPanel{

	//
	// -- CONSTANTS -----------------------------------------------
	//
    private static Border STANDARD_LINE_BORDER = BorderFactory.createLineBorder(new Color(0, 128, 128));
    private static Border ACCEPT_LINE_BORDER = BorderFactory.createLineBorder(Color.green);
    private static Border ACCEPTED_LINE_BORDER = BorderFactory.createLineBorder(Color.red);
    private static Border MARGIN_EMPTY_BORDER = BorderFactory.createEmptyBorder(4,4, 4, 3);
	private static Color DEFAULT_BACKGROUND_COLOR = new Color(0xd0, 0xd0, 0xe0);
	private static Color JINI_BACKGROUNG_COLOR = Color.CYAN;
	private static Color HTTP_BACKGROUNG_COLOR = Color.ORANGE;
	private static Color RMISSH_BACKGROUNG_COLOR = Color.WHITE;
	
	
	//
	// -- ATTRIBUTES -----------------------------------------------
	//
	/** The Object corresponding to this graphical interface (the model) */
	private NodeObject nodeObject;
	
    private final TitledBorder currentTitledBorder;
    private final Border currentBorder;
    
    
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	public NodePanel(AbstractDataObjectPanel parent, NodeObject nodeObject){
		super(parent,nodeObject.getKey());
		
		this.nodeObject = nodeObject;
		
		Color color;		
		if(nodeObject.getProtocol().equals("jini:"))
			color = JINI_BACKGROUNG_COLOR;
		else if(nodeObject.getProtocol().equals("http:"))
			color = HTTP_BACKGROUNG_COLOR;
		else if(nodeObject.getProtocol().equals("rmissh:"))
			color = RMISSH_BACKGROUNG_COLOR;
		else
			color = DEFAULT_BACKGROUND_COLOR;
		setBackground(color);
		
		setLayout(new GridLayout(0, 1, 2, 2));
		
		currentTitledBorder = BorderFactory.createTitledBorder(STANDARD_LINE_BORDER,this.name);
		currentBorder = BorderFactory.createCompoundBorder(currentTitledBorder,MARGIN_EMPTY_BORDER);
		setBorder(currentBorder);
		setMinimumSize(new Dimension(100, 20));
		
		NodePopupMenu popup = new NodePopupMenu(this, this.name);
		addMouseListener(popup.getMenuMouseListener());
	}
	

	//
	// -- PUBLIC METHODS ---------------------------------------------
	//
		
	public void destroyObject() {
		// TODO Auto-generated method stub
		
	}
}
