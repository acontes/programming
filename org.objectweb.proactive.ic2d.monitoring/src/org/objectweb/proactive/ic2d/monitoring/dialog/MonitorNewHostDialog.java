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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MonitorNewHostDialog {

	
	private Shell shell = null;

	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	public MonitorNewHostDialog(Display display) {
		shell = new Shell(display, SWT.BORDER | SWT.CLOSE);
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
		
		Text hostText = new Text(hostGroup, SWT.BORDER);
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
		
		Text portText = new Text(hostGroup, SWT.BORDER);
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
		
		Text depthText = new Text(shell, SWT.BORDER);
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
		
		Button okButton = new Button(shell, SWT.NONE);
		okButton.setText("OK");
		FormData okFormData = new FormData();
		okFormData.top = new FormAttachment(depthLabel2, 20);
		okFormData.left = new FormAttachment(25, 20);
		okFormData.right = new FormAttachment(50, -10);
		okButton.setLayoutData(okFormData);

		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText("Cancel");
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
}
