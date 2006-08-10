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
package org.objectweb.proactive.ic2d.monitoring.figures.listeners;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.objectweb.proactive.ic2d.monitoring.actions.NewHostAction;
import org.objectweb.proactive.ic2d.monitoring.actions.RefreshAction;
import org.objectweb.proactive.ic2d.monitoring.actions.RefreshHostAction;
import org.objectweb.proactive.ic2d.monitoring.actions.RefreshJVMAction;
import org.objectweb.proactive.ic2d.monitoring.actions.RefreshNodeAction;
import org.objectweb.proactive.ic2d.monitoring.actions.SetDepthAction;
import org.objectweb.proactive.ic2d.monitoring.actions.SetTTRAction;
import org.objectweb.proactive.ic2d.monitoring.actions.SetUpdateFrequenceAction;
import org.objectweb.proactive.ic2d.monitoring.actions.StopMonitoringAction;
import org.objectweb.proactive.ic2d.monitoring.data.VMObject;
import org.objectweb.proactive.ic2d.monitoring.views.MonitoringView;

public class JVMListener implements MouseListener {

	private ActionRegistry registry;
	
	private VMObject jvm;

	public JVMListener(VMObject jvm) {
		this.registry = MonitoringView.getInstance().getGraphicalViewer().getActionRegistry();
		this.jvm = jvm;
	}
	
	
	public void mouseDoubleClicked(MouseEvent me) { /* Do nothing */ }

	public void mousePressed(MouseEvent me) {
		if(me.button == 3) {
			// Monitor a new host
			registry.getAction(NewHostAction.NEW_HOST).setEnabled(false);
			
			// Set depth control
			registry.getAction(SetDepthAction.SET_DEPTH).setEnabled(false);
				
			// Refresh
			registry.getAction(RefreshAction.REFRESH).setEnabled(false);
			
			// Set time to refresh
			registry.getAction(SetTTRAction.SET_TTR).setEnabled(false);
			
			// Look for new JVM
			registry.getAction(RefreshHostAction.REFRESH_HOST).setEnabled(false);
			
			// Look for new Nodes
			RefreshJVMAction refreshJVMAction = (RefreshJVMAction)registry.getAction(RefreshJVMAction.REFRESH_JVM);
			refreshJVMAction.setJVM(jvm);
			refreshJVMAction.setEnabled(true);
			
			// Look for new Active Objects
			registry.getAction(RefreshNodeAction.REFRESH_NODE).setEnabled(false);
			
			// Stop monitoring this JVM
			StopMonitoringAction stopMonitoringAction = (StopMonitoringAction)registry.getAction(StopMonitoringAction.STOP_MONITORING);
			stopMonitoringAction.setObject(jvm);
			stopMonitoringAction.setEnabled(true);
			
			// Set update frequence...
			registry.getAction(SetUpdateFrequenceAction.SET_UPDATE_FREQUENCE).setEnabled(false);
		}
	}

	public void mouseReleased(MouseEvent me) { /* Do nothing */ }

}
