/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.objectweb.proactive.ic2d.console.Console;
import org.objectweb.proactive.ic2d.jmxmonitoring.Activator;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.WorldObject;


public class ComponentMonitoringAction // extends Action implements IWorkbenchWindowActionDelegate 
{
    //    private Display display;
    //    private int index= 0;
    //
    //    /** The World */
    //    private WorldObject world;
    //    public static final String COMPONENT_MONITORING_ACTION = "Component Monitor";
    //
    //    public ComponentMonitoringAction(Display display, WorldObject world) {
    ////        this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "host.gif"));
    //        this.display = display;
    //        this.world = world;
    //        this.setId(COMPONENT_MONITORING_ACTION);
    //        this.setText(COMPONENT_MONITORING_ACTION);
    //        setToolTipText(COMPONENT_MONITORING_ACTION);
    //    }
    //
    //    //
    //    // -- PUBLICS METHODS -----------------------------------------------
    //    //
    //    protected void setWorldObject(WorldObject world) {
    //        this.world = world;
    //    }
    //    
    //    //
    //    // -- PUBLICS METHODS -----------------------------------------------
    //    //
    //    public void dispose() {
    //        // TODO Auto-generated method stub
    //    }
    //
    //    public void init(IWorkbenchWindow window) {
    //        // TODO Auto-generated method stub
    //    }
    //
    //    public void run(IAction action) {
    //        this.run();
    //    }
    //
    //    public void selectionChanged(IAction action, ISelection selection) {
    //        // TODO Auto-generated method stub
    //    }
    //
    //    @Override
    //    public void run() {
    //    	try {
    //           IViewPart componentTreeView =  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ComponentTreeView.ID);
    //           if(componentTreeView instanceof ComponentTreeView)
    //           {
    //        	   ((ComponentTreeView)componentTreeView).setWorld(world);
    //           }
    //        } catch (PartInitException e) {
    //            // TODO Auto-generated catch block
    //            e.printStackTrace();
    //        }
    //        Console.getInstance(Activator.CONSOLE_NAME).debug("New Monitoring view");
    //    	
    //    }
}