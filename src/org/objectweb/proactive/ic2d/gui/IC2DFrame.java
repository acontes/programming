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

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


public class IC2DFrame  extends JFrame {
	
	/** The events listener */
	private IC2DListener listener;
	
	/** the frame's default width and height */
	private static final int DEFAULT_WIDTH = 850;
	private static final int DEFAULT_HEIGHT = 600;
	
	/** Quit menu item */
	private JMenuItem quitItem;
	
	/** Monitoring menu */
	private JMenuItem rmiItem; //RMI menu item
	private JMenuItem legendItem; //Legend menu item
	
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	/**
	 * TODO comment
	 */
	public IC2DFrame() {
		
		/* sets the frame's title */
		super("new IC2D");
			
		/* sets the frame's dimensions */
		this.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

		this.listener = new IC2DListener(this);
		
		setJMenuBar(createMenuBar());
		
		this.addWindowListener(this.listener);
	}
	
	//
	// -- PUBLICS METHODS -----------------------------------------------
	//
	
	public JMenuItem getQuitItem() {
		return this.quitItem;
	}
	
	public JMenuItem getRMIItem(){
		return this.rmiItem;
	}
	
	public JMenuItem getLegendItem() {
		return this.legendItem;
	}
	
	public static int getDefaultHeight() {
		return DEFAULT_HEIGHT;
	}
	
	//
	// -- PRIVATE METHODS -----------------------------------------------
	//
	
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new javax.swing.JMenuBar();
		
		//
		// File menu
		//
		JMenu fileMenu = new JMenu("File");
		
		this.quitItem = new JMenuItem("Quit");
		quitItem.addActionListener(this.listener);
		
		fileMenu.add(quitItem);
		menuBar.add(fileMenu);
		
		
		//
		// monitoring menu
		//      
		JMenu monitoringMenu = new JMenu("Monitoring");
		
		// RMI item
		this.rmiItem = new JMenuItem("Monitor a new RMI host");
		this.rmiItem.addActionListener(this.listener);
		monitoringMenu.add(rmiItem);
		
		monitoringMenu.addSeparator();
		
		// Display the legend 
		this.legendItem = new JMenuItem("Legend");
		this.legendItem.setToolTipText("Display the legend");
		this.legendItem.addActionListener(this.listener);
		monitoringMenu.add(legendItem);
		
		
		menuBar.add(monitoringMenu);
		
		return menuBar;
	}
}
