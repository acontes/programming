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
package org.objectweb.proactive.ic2d.jobmonitoring.views;

import org.eclipse.gef.editparts.RootTreeEditPart;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.ic2d.jobmonitoring.actions.CollapseAllAction;
import org.objectweb.proactive.ic2d.jobmonitoring.actions.ExpandAllAction;
import org.objectweb.proactive.ic2d.jobmonitoring.editparts.JobMonitoringTreePartFactory;
import org.objectweb.proactive.ic2d.monitoring.data.WorldObject;

/**
 * 
 * @author Jean-Michael Legait and Michèle Reynier
 *
 */
public class JobMonitoringView extends ViewPart {

	
	private TreeViewer treeViewer;

	
	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		FormLayout layout = new FormLayout();
		parent.setLayout(layout);
		
		// create graphical viewer
		treeViewer = new TreeViewer();
		treeViewer.createControl(parent);

		// configure the viewer
		Tree tree = new Tree(parent, SWT.SINGLE);
		((RootTreeEditPart)treeViewer.getRootEditPart()).setWidget(tree);

		// initialize the viewer with input
		treeViewer.setEditPartFactory(new JobMonitoringTreePartFactory());
		treeViewer.setContents(WorldObject.getInstance());

		// hide an empty part in the view
		FormData formData1 = new FormData();
		formData1.top = new FormAttachment(0, 0);
		formData1.bottom = new FormAttachment(0, 0);
		formData1.left = new FormAttachment(0, 0);
		formData1.right = new FormAttachment(0, 0);
		parent.getChildren()[0].setLayoutData(formData1);
		
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 0);
		formData.bottom = new FormAttachment(100, 0);
		formData.left = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);
		parent.getChildren()[1].setLayoutData(formData);


//		treeViewer.setContentProvider(new JobMonitoringContentProvider());
//		treeViewer.setLabelProvider(new JobMonitoringLabelProvider());
//		treeViewer.setInput(WorldObject.getInstance());

		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		
		toolBarManager.add(new ExpandAllAction(treeViewer));
		toolBarManager.add(new CollapseAllAction(treeViewer));
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
