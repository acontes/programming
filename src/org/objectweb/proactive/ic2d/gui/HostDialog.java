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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.ic2d.IC2D;
import org.objectweb.proactive.ic2d.controller.AbstractWorldController;

public class HostDialog extends javax.swing.JDialog{
	
	private int protocol;
	
	private static String defaultMaxDepth = "3";
	
	private JTextField jTextFieldHostIp;
	private JButton jButtonOK;
	private JButton jButtonCancel;
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	/** Creates new form HostDialog */
	public HostDialog(Frame parent, String initialHostValue, int protocol) {
		super(parent, true);
		this.protocol = protocol;
		initComponents(initialHostValue);
		setSize(450, 200);
		setLocationRelativeTo(parent);
	}
	
	//
	// -- PRIVATE METHODS -----------------------------------------------
	//
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents(String initialHostValue) {
		Container container = getContentPane();
		container.setLayout(new java.awt.FlowLayout());
		
		setTitle("Adding host and depth to monitor");
		
		container.add(Box.createRigidArea(new Dimension(430,10)));
		
		/** To enter the name or the IP of the host to monitor */
		JLabel jLabelHostIp = new JLabel("Please enter the name or the IP of the host to monitor :");
		container.add(jLabelHostIp);
		this.jTextFieldHostIp = new JTextField(30);
		jTextFieldHostIp.setText(initialHostValue);
		container.add(jTextFieldHostIp);
		
		container.add(Box.createRigidArea(new Dimension(430,5)));
		
		/** To enter the depth */
		JLabel jLabelDepth = new JLabel("Hosts will be recursively searched up to a depth of :");
		container.add(jLabelDepth);
		JTextField jTextFielddepth = new JTextField(defaultMaxDepth);
		container.add(jTextFielddepth);
		JLabel jLabel = new JLabel("You can change it there or from \"Menu Control, Set Depth Control\" ");
		container.add(jLabel);
		
		container.add(Box.createRigidArea(new Dimension(430,10)));
		
		/** Ok Button */
		this.jButtonOK = new JButton("OK");
		jButtonOK.addActionListener(new HostDialogListener(this));
		container.add(jButtonOK);
		getRootPane().setDefaultButton(jButtonOK);
		
		
		/** Cancel Button */
		this.jButtonCancel = new JButton("Cancel");
		jButtonCancel.addActionListener(new HostDialogListener(this));
		container.add(jButtonCancel);
		
		setResizable(false);
	}
	
	//
	// -- INNER CLASSES -----------------------------------------------
	//
	
	private class HostDialogListener implements ActionListener {
		
		private HostDialog hostDialog;
		
		public HostDialogListener(HostDialog hostDialog) {
			this.hostDialog = hostDialog;
		}
		
		public void actionPerformed(ActionEvent event) {
			
			// OK
			if(event.getSource() == hostDialog.jButtonOK) {
				
				String host = hostDialog.jTextFieldHostIp.getText();
				int port = UrlBuilder.getPortFromUrl(host);
				String hostname = UrlBuilder.removePortFromHost(host);
				try {
					host = UrlBuilder.getHostNameorIP(InetAddress.getByName(hostname));
				} catch (UnknownHostException e) {
					// TODO log
					e.printStackTrace();
					return;
				}

				List worldControllers = IC2D.getWorldControllerList();
				for(int i=0; i<worldControllers.size(); i++){
					AbstractWorldController controller = ((AbstractWorldController) worldControllers.get(i));
					controller.addNewHost(hostname, port, protocol);
				}
				hostDialog.setVisible(false);
			}
			
			// CANCEL
			else if(event.getSource() == hostDialog.jButtonCancel) {
				hostDialog.setVisible(false);
			}
		}
	}
}
