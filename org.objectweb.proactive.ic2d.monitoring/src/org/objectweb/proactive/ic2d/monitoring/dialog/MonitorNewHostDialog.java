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
package org.objectweb.proactive.ic2d.monitoring.dialog;

import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.ic2d.console.Console;
import org.objectweb.proactive.ic2d.monitoring.data.HostObject;
import org.objectweb.proactive.ic2d.monitoring.data.MonitorThread;
import org.objectweb.proactive.ic2d.monitoring.data.Protocol;
import org.objectweb.proactive.ic2d.monitoring.exceptions.HostAlreadyExistsException;


public class MonitorNewHostDialog extends Dialog {


	private Protocol protocol;

	private Shell shell = null;
	private Shell parent =null;

	private Text hostText;
	private Text portText;
	private Text depthText;
	private Button okButton;
	private Button cancelButton;

	//
	// -- CONSTRUCTORS -----------------------------------------------
	//

	public MonitorNewHostDialog(Shell parent, Protocol protocol) {
		// Pass the default styles here
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

		this.parent = parent;
		
		this.protocol = protocol;

		String initialHostValue = "localhost";
		String port = "";

		/* Get the machine's name */
		try {
			initialHostValue = UrlBuilder.getHostNameorIP(java.net.InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			// TODO catch this exception, and do something
			e.printStackTrace();
		}

		/* Load the proactive default configuration */
		ProActiveConfiguration.load();

		/* Get the machine's port */
		port = System.getProperty("proactive.rmi.port");

		/* Init the display */
		Display display = getParent().getDisplay();
		shell = new Shell(getParent(), SWT.BORDER | SWT.CLOSE);
		shell.setText("Adding host and depth to monitor");
		shell.setSize(new Point(300, 200));


		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);


		Group hostGroup = new Group(shell, SWT.NONE);
		hostGroup.setText("Host to monitor");
		FormLayout hostLayout = new FormLayout();
		hostLayout.marginHeight = 5;
		hostLayout.marginWidth = 5;
		hostGroup.setLayout(hostLayout);
		FormData hostFormData1 = new FormData();
		hostFormData1.left = new FormAttachment(0, 0);
		hostFormData1.right = new FormAttachment(100, 0);
		hostGroup.setLayoutData(hostFormData1);

		Label hostLabel = new Label(hostGroup, SWT.NONE);
		hostLabel.setText("Name or IP :");

		this.hostText = new Text(hostGroup, SWT.BORDER);
		hostText.setText(initialHostValue);
		FormData hostFormData = new FormData();
		hostFormData.top = new FormAttachment(0, -1);
		hostFormData.left = new FormAttachment(hostLabel, 5);
		hostFormData.right = new FormAttachment(70, -10);
		hostText.setLayoutData(hostFormData);

		Label portLabel = new Label(hostGroup, SWT.NONE);
		portLabel.setText("Port :");
		FormData portFormData = new FormData();
		portFormData.left = new FormAttachment(70, 10);
		portLabel.setLayoutData(portFormData);

		this.portText = new Text(hostGroup, SWT.BORDER);
		if(port != null) portText.setText(port);
		FormData portFormData2 = new FormData();
		portFormData2.top = new FormAttachment(0, -1);
		portFormData2.left = new FormAttachment(portLabel, 5);
		portFormData2.right = new FormAttachment(100, 0);
		portText.setLayoutData(portFormData2);

		Label depthLabel = new Label(shell, SWT.NONE);
		depthLabel.setText("Hosts will be recursively searched up to a depth of :");
		FormData depthFormData = new FormData();
		depthFormData.top = new FormAttachment(hostGroup, 20);
		depthFormData.left = new FormAttachment(0, 20);
		depthLabel.setLayoutData(depthFormData);

		this.depthText = new Text(shell, SWT.BORDER);
		depthText.setText(MonitorThread.getInstance().getDepth()+"");
		FormData depthFormData2 = new FormData();
		depthFormData2.top = new FormAttachment(hostGroup, 17);
		depthFormData2.left = new FormAttachment(depthLabel, 5);
		depthFormData2.right = new FormAttachment(100, -20);
		depthText.setLayoutData(depthFormData2);

		Label depthLabel2 = new Label(shell, SWT.CENTER);
		depthLabel2.setText("You can change it there or from menu \"Control -> Set depth control\"");
		FormData depthFormData3 = new FormData();
		depthFormData3.top = new FormAttachment(depthLabel, 5);
		depthLabel2.setLayoutData(depthFormData3);

		this.okButton = new Button(shell, SWT.NONE);
		okButton.setText("OK");
		okButton.addSelectionListener(new MonitorNewHostListener());
		okButton.setFocus();
		FormData okFormData = new FormData();
		okFormData.top = new FormAttachment(depthLabel2, 20);
		okFormData.left = new FormAttachment(25, 20);
		okFormData.right = new FormAttachment(50, -10);
		okButton.setLayoutData(okFormData);

		this.cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new MonitorNewHostListener());
		FormData cancelFormData = new FormData();
		cancelFormData.top = new FormAttachment(depthLabel2, 20);
		cancelFormData.left = new FormAttachment(50, 10);
		cancelFormData.right = new FormAttachment(75, -20);
		cancelButton.setLayoutData(cancelFormData);

		center(display, shell);

		shell.pack();
		shell.open();


		while(!shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}

		//display.dispose(); TODO ???
	}


	//
	// -- PRIVATE METHODS -----------------------------------------------
	//


	private static void center(Display display, Shell shell) {
		Rectangle rect = display.getClientArea();
		Point size = shell.getSize();
		int x = (rect.width - size.x) / 2;
		int y = (rect.height - size.y) / 2;
		shell.setLocation(new Point(x, y));
	}

	/**
	 * Logs in the IC2D's console, and show a pop-up.
	 * @param message
	 */
	private void displayMessage(final String message) {
		// Print the message in the UI Thread in async mode
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Console.getInstance("IC2D").warn(message);
				MessageBox mb = new MessageBox(parent);
				mb.setMessage(message);
				mb.open();
			}});

	}

	//
	// -- INNER CLASS -----------------------------------------------
	//

	private class MonitorNewHostListener extends SelectionAdapter {
		String hostname;
		int port ;

		public void widgetSelected(SelectionEvent e) {
			if(e.widget == okButton) {
				hostname = hostText.getText();
				port = Integer.parseInt(portText.getText());
				MonitorThread.getInstance().setDepth(Integer.parseInt(depthText.getText()));
				new Thread(){
					public void run(){
						try {
							new HostObject(hostname, port, protocol);
						} catch (HostAlreadyExistsException e) {
							displayMessage(e.getMessage());
						}
					}
				}.start();
			}
			shell.close();
		}
	}
}
