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
package org.objectweb.proactive.ic2d.launcher.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveListener4;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;
import org.objectweb.proactive.ic2d.launcher.views.InfoView;

public class LauncherPerspective implements IPerspectiveFactory ,IPerspectiveListener4 {

	public static final String ID = "org.objectweb.proactive.ic2d.launcher.perspectives.LauncherPerspective";

	/** Bottom folder's id. */
	public static final String FI_BOTTOM = ID + ".bottomFolder";
    /** Right folder's id. */
    public static final String FI_RIGHT = ID + ".rightFolder";
	//
	// -- PUBLIC METHODS ----------------------------------------------
	//

	public void createInitialLayout(IPageLayout layout) {
		String editorAreaId=layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);
		
		IFolderLayout rightFolder = layout.createFolder(FI_RIGHT, IPageLayout.RIGHT, 0.80f, editorAreaId);
		rightFolder.addView(InfoView.ID);
		
		IFolderLayout bottomFolder = layout.createFolder(FI_BOTTOM, IPageLayout.BOTTOM, 0.75f, editorAreaId);
		bottomFolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(this);
	}

	public void perspectivePreDeactivate(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		// TODO Auto-generated method stub
		//System.out.println("LauncherPerspective.perspectivePreDeactivate()");
	}

	public void perspectiveClosed(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		// TODO Auto-generated method stub
		//System.out.println("LauncherPerspective.perspectiveClosed()");
	}

	public void perspectiveDeactivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		// TODO Auto-generated method stub
		//System.out.println("LauncherPerspective.perspectiveDeactivated()");
	}

	public void perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		// TODO Auto-generated method stub
		//System.out.println("LauncherPerspective.perspectiveOpened()");
	}

	public void perspectiveSavedAs(IWorkbenchPage page, IPerspectiveDescriptor oldPerspective, IPerspectiveDescriptor newPerspective) {
		// TODO Auto-generated method stub
		//System.out.println("LauncherPerspective.perspectiveSavedAs()");
	}

	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, IWorkbenchPartReference partRef, String changeId) {
		// We remove the file of the list of the launched applications
//		if(page!=null && changeId.compareTo("editorClose")==0){
//				XMLDescriptorSet.getInstance().removeFile(partRef.getTitle());
//		}
	}

	public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		// TODO Auto-generated method stub
		//System.out.println("LauncherPerspective.perspectiveActivated()");
	}

	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
		// TODO Auto-generated method stub
		//System.out.println("LauncherPerspective.perspectiveChanged()");
	}

}
