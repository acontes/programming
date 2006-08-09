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
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

public class LauncherPerspective implements IPerspectiveFactory {

	public static final String ID = "org.objectweb.proactive.ic2d.launcher.perspectives.LauncherPerspective";

	/** Bottom folder's id. */
	public static final String FI_BOTTOM = ID + ".bottomFolder";

	//
	// -- PUBLIC METHODS ----------------------------------------------
	//

	public void createInitialLayout(IPageLayout layout) {
		String editorAreaId=layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);
		IFolderLayout bottomFolder = layout.createFolder(FI_BOTTOM, IPageLayout.BOTTOM, 0.75f, editorAreaId);
		bottomFolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
	}

}
