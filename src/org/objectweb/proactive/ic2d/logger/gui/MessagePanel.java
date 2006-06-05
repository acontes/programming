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
package org.objectweb.proactive.ic2d.logger.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MessagePanel extends JPanel {
	
	private TextPaneMessageLogger messageLogger;
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	public MessagePanel(String title) {
		
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(title));
		
		messageLogger = new TextPaneMessageLogger();
		
		JPanel topPanel = new JPanel(new BorderLayout());
		
		// clear log button
		JButton clearLogButton = new JButton("clear messages");
		clearLogButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				messageLogger.setText("");
			}
		});
		topPanel.add(clearLogButton, BorderLayout.WEST);
		add(topPanel, BorderLayout.NORTH);
		JScrollPane pane = new JScrollPane(messageLogger);
		add(pane, BorderLayout.CENTER);
	}
	
}
