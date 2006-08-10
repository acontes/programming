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
package org.objectweb.proactive.ic2d.monitoring.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.objectweb.proactive.ic2d.monitoring.dialog.MonitorNewHostDialog;

public class NewHostAction extends Action implements IWorkbenchWindowActionDelegate {

	private Display display;
	
	public static final String NEW_HOST = "New host";
	
	public NewHostAction() {
		super();
	}
	
	public NewHostAction(Display display) {
		this.display = display;
		this.setId(NEW_HOST);
		this.setText("Monitor a new host...");
	}
	
	//
	// -- PUBLICS METHODS -----------------------------------------------
	//
	
	public void dispose() {
		// TODO Auto-generated method stub
	}

	public void init(IWorkbenchWindow window) {
		this.display = window.getShell().getDisplay();
	}

	public void run(IAction action) {
		new MonitorNewHostDialog(display.getActiveShell());
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}
	
	
	
	@Override
	public void run() {
		new MonitorNewHostDialog(display.getActiveShell());
	}
}
