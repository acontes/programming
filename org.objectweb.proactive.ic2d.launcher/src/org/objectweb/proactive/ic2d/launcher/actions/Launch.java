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
package org.objectweb.proactive.ic2d.launcher.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.objectweb.proactive.core.descriptor.Launcher;
import org.objectweb.proactive.ic2d.console.Console;
import org.objectweb.proactive.ic2d.launcher.Activator;
import org.objectweb.proactive.ic2d.launcher.editors.PathEditorInput;
import org.objectweb.proactive.ic2d.launcher.editors.xml.XMLEditor;
import org.objectweb.proactive.ic2d.launcher.perspectives.LauncherPerspective;

public class Launch extends Action implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	//
	// -- PUBLIC METHODS ---------------------------------------------
	//

	public void dispose() {
		window = null;
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
		this.setEnabled(false);
	}

	public void run(IAction action) {
		IWorkbenchPage page = window.getActivePage();
		if(page==null)
			return;
		IEditorPart editorPart = page.getActiveEditor();
		
		boolean wasDirty = editorPart.isDirty();
		// Ask to the user, if he wants save his file (if it is not already saved).
		// If the user chooses 'cancel', we don't run the xml file
		boolean succeeded = page.saveEditor(editorPart, true);
		if(!succeeded)
			return;
		if(wasDirty && !editorPart.isDirty())
			Console.getInstance(Activator.CONSOLE_NAME).log("File saved");
		
		if (!(editorPart instanceof XMLEditor))
			return;
		XMLEditor editor = (XMLEditor) editorPart; 
		if(editor == null)
			Console.getInstance(Activator.CONSOLE_NAME).log("Editor is null");
		else{
			PathEditorInput input = (PathEditorInput) editor.getEditorInput();
			final String path = input.getPath().toString();

			Launcher launcher = null;

			// creates the launcher
			try {
				launcher = new Launcher(path);
			} catch (Exception e) {
				Console.getInstance(Activator.CONSOLE_NAME).logException(e);
			}

			// activate the launcher
			try {
				if(!launcher.isActivated()){
					final Launcher l = launcher;
					Thread thread = new Thread(new Runnable() {
						public void run () {
							try {
								l.activate();
								Console.getInstance(Activator.CONSOLE_NAME).log(path+" - activated");
							} catch (Exception e) {
								Console.getInstance(Activator.CONSOLE_NAME).logException(e);
							}
						}});
					thread.start();
				}
			} catch (Exception e) {
				Console.getInstance(Activator.CONSOLE_NAME).logException(e);
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		action.setEnabled(goodContext());
	}

	//
	// -- PRIVATE METHODS ---------------------------------------------
	//

	/**
	 * @return True if the Launcher perspective is open, and if a file is selected, false otherwise.
	 */
	private boolean goodContext(){
		boolean goodPerspective;
		IWorkbenchPage page = window.getActivePage();
		IWorkbench workbench = window.getWorkbench();
		IPerspectiveRegistry reg = workbench.getPerspectiveRegistry();
		goodPerspective =  page.getPerspective().equals(reg.findPerspectiveWithId(LauncherPerspective.ID));

		return (goodPerspective && (page.getEditorReferences().length > 0));
	}
}
