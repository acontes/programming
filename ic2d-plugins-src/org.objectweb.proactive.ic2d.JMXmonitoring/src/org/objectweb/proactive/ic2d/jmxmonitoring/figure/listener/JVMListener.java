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
package org.objectweb.proactive.ic2d.jmxmonitoring.figure.listener;

import java.util.Iterator;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.action.RefreshJVMAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.action.StopMonitoringAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.RuntimeObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.dnd.DragAndDrop;
import org.objectweb.proactive.ic2d.jmxmonitoring.extpoint.IActionExtPoint;
import org.objectweb.proactive.ic2d.jmxmonitoring.view.MonitoringView;

public class JVMListener implements MouseListener, MouseMotionListener {

	private ActionRegistry registry;
	private RuntimeObject jvm;
	private DragAndDrop dnd;

	public JVMListener(RuntimeObject jvm, MonitoringView monitoringView) {
		this.registry = monitoringView.getGraphicalViewer().getActionRegistry();
		this.dnd = monitoringView.getDragAndDrop();
		this.jvm = jvm;
	}
	
	
	public void mouseDoubleClicked(MouseEvent me) { /* Do nothing */ }

	public void mousePressed(MouseEvent me) {
		if(me.button == 1){
			dnd.reset();
			
//			for(Iterator<IAction> action = (Iterator<IAction>) registry.getActions() ; action.hasNext() ;) {
//				IAction act = action.next();
//				if (act instanceof IActionExtPoint) {
//					IActionExtPoint extensionAction = (IActionExtPoint) act;
//					extensionAction.setActiveSelect(this.jvm);
//				}
//			}
		}
		else if(me.button == 3) {
			
			for(Iterator<IAction> action = (Iterator<IAction>) registry.getActions() ; action.hasNext() ;) {
				IAction act = action.next();
				if (act instanceof RefreshJVMAction) {
					RefreshJVMAction refreshJVMAction = (RefreshJVMAction) act;
					refreshJVMAction.setJVM(jvm);
					refreshJVMAction.setEnabled(true);
				} else if (act instanceof StopMonitoringAction) {
					StopMonitoringAction stopMonitoringAction = (StopMonitoringAction) act;
					stopMonitoringAction.setObject(jvm);
					stopMonitoringAction.setEnabled(true);
				} else if (act instanceof IActionExtPoint) {
					IActionExtPoint extensionAction = (IActionExtPoint) act;
					extensionAction.setAbstractDataObject(this.jvm);
				} else {
					act.setEnabled(false);
				}
			}
//			// Monitor a new host
//			registry.getAction(NewHostAction.NEW_HOST).setEnabled(false);
//			
//			// Set depth control
//			registry.getAction(SetDepthAction.SET_DEPTH).setEnabled(false);
//				
//			// Refresh
//			registry.getAction(RefreshAction.REFRESH).setEnabled(false);
//			
//			// Set time to refresh
//			registry.getAction(SetTTRAction.SET_TTR).setEnabled(false);
//			
//			// Look for new JVM
//			registry.getAction(RefreshHostAction.REFRESH_HOST).setEnabled(false);
//			
//			// Look for new Nodes
//			RefreshJVMAction refreshJVMAction = (RefreshJVMAction)registry.getAction(RefreshJVMAction.REFRESH_JVM);
//			refreshJVMAction.setJVM(jvm);
//			refreshJVMAction.setEnabled(true);
//			
//			// Look for new Active Objects
//			registry.getAction(RefreshNodeAction.REFRESH_NODE).setEnabled(false);
//			
//			// Stop monitoring this JVM
//			StopMonitoringAction stopMonitoringAction = (StopMonitoringAction)registry.getAction(StopMonitoringAction.STOP_MONITORING);
//			stopMonitoringAction.setObject(jvm);
//			stopMonitoringAction.setEnabled(true);
//			
//			// Kill this VM
//			KillVMAction killVMAction = (KillVMAction)registry.getAction(KillVMAction.KILLVM);
//			killVMAction.setVM(jvm);
//			killVMAction.setEnabled(true);
//			
//			// Set update frequence...
//			registry.getAction(SetUpdateFrequenceAction.SET_UPDATE_FREQUENCE).setEnabled(false);
//
//			// Vertical Layout
//			registry.getAction(VerticalLayoutAction.VERTICAL_LAYOUT).setEnabled(false);
//			
//			// Horizontal Layout
//			registry.getAction(HorizontalLayoutAction.HORIZONTAL_LAYOUT).setEnabled(false);
//			
//			// Manual handling of an action for timer snapshot ... needs improvement
//			IAction anAction = registry.getAction("Get timer snapshot");
//			if ( anAction != null ){
//				((IActionExtPoint)anAction).setAbstractDataObject(this.jvm);
//				anAction.setText("Gather Stats from JVM");
//				anAction.setEnabled(true);
//			}
		}
	}

	public void mouseReleased(MouseEvent me) {
		dnd.reset();
	}
	
	//---- MouseMotionListener 

	public void mouseEntered(MouseEvent me) {		
		if(dnd.getSource()!=null)
			dnd.refresh(null);
	}

	public void mouseExited(MouseEvent me) {
		if(dnd.getSource()!=null)
			dnd.refresh(null);
	}

	public void mouseDragged(MouseEvent me) { /* Do nothing */ }
	
	public void mouseHover(MouseEvent me) { /* Do nothing */ }

	public void mouseMoved(MouseEvent me) {	/* Do nothing */ }
}
