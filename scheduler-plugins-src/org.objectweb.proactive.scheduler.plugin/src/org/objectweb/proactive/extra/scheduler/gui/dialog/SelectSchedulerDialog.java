/*
 * ################################################################
 * 
 * ProActive: The Java(TM) library for Parallel, Distributed, Concurrent
 * computing with Security and Mobility
 * 
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis Contact:
 * proactive@objectweb.org
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Initial developer(s): The ProActive Team
 * http://www.inria.fr/oasis/ProActive/contacts.html Contributor(s):
 * 
 * ################################################################
 */
package org.objectweb.proactive.extra.scheduler.gui.dialog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.extra.scheduler.core.SchedulerFrontend;
import org.objectweb.proactive.extra.scheduler.core.UserScheduler;

/**
 * This class allow to pop up a dialogue to connect a scheduler.
 *
 * @author ProActive Team
 * @version 1.0, Jul 12, 2007
 * @since ProActive 3.2
 */
public class SelectSchedulerDialog extends Dialog {

	/** Name of the file which store the good url */
	public static final String file = ".ProActive_Scheduler_url";

	private static List<String> urls = null;
	private static boolean validate = false;
	private static String url = null;
	private static Combo urlCombo = null;
	private Shell shell = null;
	private Button okButton = null;
	private Button cancelButton = null;

	// -------------------------------------------------------------------- //
	// --------------------------- constructor ---------------------------- //
	// -------------------------------------------------------------------- //
	private SelectSchedulerDialog(Shell parent) {
		// Pass the default styles here
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

		// Load the proactive default configuration
		ProActiveConfiguration.load();

		validate = false;

		// Init the display
		Display display = parent.getDisplay();

		// Init the shell
		shell = new Shell(parent, SWT.BORDER | SWT.CLOSE);
		shell.setText("Connect to scheduler");
		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		shell.setLayout(layout);

		// label "URL"
		Label urlLabel = new Label(shell, SWT.NONE);
		urlLabel.setText("Url :");

		// text url
		urlCombo = new Combo(shell, SWT.BORDER);
		FormData urlFormData = new FormData();
		urlFormData.top = new FormAttachment(0, -1);
		urlFormData.left = new FormAttachment(urlLabel, 5);
		urlFormData.right = new FormAttachment(100, -5);
		urlCombo.setLayoutData(urlFormData);
		loadUrls();

		// button "OK"
		this.okButton = new Button(shell, SWT.NONE);
		okButton.setText("OK");
		okButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				validate = true;
				url = urlCombo.getText();
				shell.close();
			}
		});
		FormData okFormData = new FormData();
		okFormData.top = new FormAttachment(urlCombo, 5);
		okFormData.left = new FormAttachment(25, 20);
		okFormData.right = new FormAttachment(50, -10);
		okButton.setLayoutData(okFormData);
		shell.setDefaultButton(okButton);

		// button "CANCEL"
		this.cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText("Cancel");
		cancelButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				validate = false;
				shell.close();
			}
		});
		FormData cancelFormData = new FormData();
		cancelFormData.top = new FormAttachment(urlCombo, 5);
		cancelFormData.left = new FormAttachment(50, 10);
		cancelFormData.right = new FormAttachment(75, -20);
		cancelButton.setLayoutData(cancelFormData);

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	// -------------------------------------------------------------------- //
	// ----------------------------- private ------------------------------ //
	// -------------------------------------------------------------------- //
	private static void setInitialHostName() {
		String initialHostValue = "";
		String port = "";
		try {
			/* Get the machine's name */
			initialHostValue = UrlBuilder.getHostNameorIP(java.net.InetAddress.getLocalHost());
			/* Get the machine's port */
			port = System.getProperty("proactive.rmi.port");
		} catch (UnknownHostException e) {
			initialHostValue = "localhost";
			port = "1099";
		}
		urlCombo.add("rmi://" + initialHostValue + ":" + port + "/" + SchedulerFrontend.SCHEDULER_DEFAULT_NAME);
		urlCombo.setText("rmi://" + initialHostValue + ":" + port + "/" + SchedulerFrontend.SCHEDULER_DEFAULT_NAME);
	}

	/**
	 * Load Urls
	 */
	private static void loadUrls() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				urls = new ArrayList<String>();
				String url = null;
				String lastUrl = null;
				while ((url = reader.readLine()) != null) {
					urls.add(url);
					lastUrl = url;
				}
				int size = urls.size();
				if (size > 0) {
					String[] hosts = new String[size];
					urls.toArray(hosts);
					Arrays.sort(hosts);
					urlCombo.setItems(hosts);
					urlCombo.setText(lastUrl);
				} else {
					setInitialHostName();
				}
			} catch (IOException e) {
				/* Do-nothing */
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					/* Do-Nothing */
				}
			}
		} catch (FileNotFoundException e) {
			setInitialHostName();
		}
	}

	/**
	 * Record an url
	 * 
	 * @param lastUrl
	 */
	private static void recordUrl(String lastUrl) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file, false));
			PrintWriter pw = new PrintWriter(bw, true);
			// Record urls
			for (String s : urls) {
				if(!s.equals(lastUrl))
					pw.println(s);
			}
			// Record the last URL used at the end of the file
			// in order to find it easily for the next time
			pw.println(lastUrl);
		} catch (IOException e) {
			/* Do-Nothing */
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				/* Do-Nothing */
			}
		}
	}
	
	// -------------------------------------------------------------------- //
	// ------------------------------ public ------------------------------ //
	// -------------------------------------------------------------------- //
	/**
	 * This method pop up a dialog for trying to connect a scheduler.
	 * 
	 * @param parent the parent
	 * @return a UserScheduler if the connection is established, null otherwise.
	 */
	public static UserScheduler showDialog(Shell parent) {
		new SelectSchedulerDialog(parent);
		UserScheduler userScheduler = null;
		if (validate) {
			try {
				userScheduler = UserScheduler.connectTo(url);
				recordUrl(url);
			} catch (Exception e) {
				e.printStackTrace();
				// Couldn't Connect to the scheduler => do nothing
				MessageDialog.openError(parent, "Couldn't connect",
						"Couldn't Connect to the scheduler based on : \n" + url);
			}
		}
		return userScheduler;
	}
}