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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.objectweb.proactive.ic2d.monitoring.data.MonitorThread;

public class ChangeTTRDialog extends Dialog {

	
	private Shell shell = null;
	
	private Text text;
	private Button okButton;
	private Button cancelButton;
	
	public ChangeTTRDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		/* Init the display */
		Display display = getParent().getDisplay();
		
		/* Init the shell */
		shell = new Shell(getParent(), SWT.BORDER | SWT.CLOSE);
		shell.setText("Change the Time To Refresh");
		//shell.setSize(new Point(300, 400));
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);
		
		Label label = new Label(shell, SWT.NONE);
		label.setText("Enter the new Time To Refresh");
		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(15, 0);
		label.setLayoutData(labelFormData);
		
		this.text = new Text(shell, SWT.BORDER);
		text.setText(MonitorThread.getInstance().getTTR()+"");
		FormData textFormData = new FormData();
		textFormData.top = new FormAttachment(0, -3);
		textFormData.left = new FormAttachment(label, 5);
		textFormData.right = new FormAttachment(85, 0);
		text.setLayoutData(textFormData);
		
		// button "OK"
		this.okButton = new Button(shell, SWT.NONE);
		okButton.setText("OK");
		okButton.addSelectionListener(new ChangeTTRListener());
		FormData okFormData = new FormData();
		okFormData.top = new FormAttachment(label, 20);
		okFormData.left = new FormAttachment(25, 20);
		okFormData.right = new FormAttachment(50, -10);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);
		
		// button "CANCEL"
		this.cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new ChangeTTRListener());
		FormData cancelFormData = new FormData();
		cancelFormData.top = new FormAttachment(label, 20);
		cancelFormData.left = new FormAttachment(50, 10);
		cancelFormData.right = new FormAttachment(75, -20);
		cancelButton.setLayoutData(cancelFormData);
		
		shell.pack();
		shell.open();


		while(!shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}
	}
	
	
	//
	// -- INNER CLASS -----------------------------------------------
	//

	private class ChangeTTRListener extends SelectionAdapter {
		
		public void widgetSelected(SelectionEvent e) {
			if(e.widget == okButton) {
				int ttr = Integer.parseInt(text.getText());
				MonitorThread thread = MonitorThread.getInstance();
				thread.setTTR(ttr);
				thread.forceRefresh();
			}
			shell.close();
		}
	}

}
