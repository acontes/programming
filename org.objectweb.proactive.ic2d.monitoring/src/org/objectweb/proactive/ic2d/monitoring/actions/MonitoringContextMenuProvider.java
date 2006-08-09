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

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Display;
import org.objectweb.proactive.ic2d.monitoring.views.MonitoringView.MonitoringViewer;

public class MonitoringContextMenuProvider extends ContextMenuProvider {

	private Display display;
	
	public MonitoringContextMenuProvider(EditPartViewer viewer, Display display) {
		super(viewer);
		this.display = display;
	}
	

	@Override
	public void buildContextMenu(IMenuManager manager) {
		GEFActionConstants.addStandardActionGroups(manager); // ???
		
		IAction action;
		ActionRegistry registry = ((MonitoringViewer)this.getViewer()).getActionRegistry();
		
		// Refresh
		action = registry.getAction(RefreshAction.REFRESH);
		if(action == null) {
			action = new RefreshAction();
			registry.registerAction(action);
		}
		if (action.isEnabled())
			manager.appendToGroup(GEFActionConstants.GROUP_REST, action);
		
		// Monitor a new host
		action = registry.getAction(NewHostAction.NEW_HOST);
		if (action == null) {
			action = new NewHostAction(display);
			registry.registerAction(action);
		}
		if (action.isEnabled())
			manager.appendToGroup(GEFActionConstants.GROUP_REST, action);
		
		// Set depth control
		action = registry.getAction(SetDepthAction.SET_DEPTH);
		if (action == null) {
			action = new SetDepthAction(display);
			registry.registerAction(action);
		}
		if (action.isEnabled())
			manager.appendToGroup(GEFActionConstants.GROUP_REST, action);
		
		// Set time to refresh
		action = registry.getAction(SetTTRAction.SET_TTR);
		if (action == null) {
			action = new SetTTRAction(display);
			registry.registerAction(action);
		}
		if (action.isEnabled())
			manager.appendToGroup(GEFActionConstants.GROUP_REST, action);
	}

}
