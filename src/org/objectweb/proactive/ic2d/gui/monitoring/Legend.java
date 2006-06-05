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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;


public class Legend extends JFrame {
	
	// Use of the singleton pattern
	private static Legend uniqueInstance;
	
	//
	// -- CONTRUCTORS -----------------------------------------------
	//
	
	private Legend() {
		super("World Panel Legend");
		setSize(500, 80);//setSize(500, 500);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		getContentPane().setLayout(gridBagLayout);
		
		// Hosts panel
		JPanel hostPanel = new JPanel(new GridLayout(-1, 2, 5, 5));
		getContentPane().add(hostPanel);
		hostPanel.setBorder(new TitledBorder("Hosts"));
		gridBagLayout.setConstraints(hostPanel,
				new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
		
		{
			JComponent comp = new JPanel() {
				public void paintComponent(Graphics g) {
					Dimension dim = getSize();
					int w = dim.width;
					int h = dim.height;
					g.setColor(HostPanel.getColor());
					g.fillRect(w / 4, 0, w / 2, h);
				}
			};
			hostPanel.add(comp);
			hostPanel.add(new JLabel("Standard Host"));
		}
	}
	
	//
	// -- PUBLIC METHODS -----------------------------------------------
	//
	
	public static Legend getInstance() {
		return (uniqueInstance == null) ? (uniqueInstance = new Legend())
				: uniqueInstance;
	}
	
}
