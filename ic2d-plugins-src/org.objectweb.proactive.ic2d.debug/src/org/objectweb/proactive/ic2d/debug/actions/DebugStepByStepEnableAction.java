/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.ic2d.debug.actions;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.objectweb.proactive.ic2d.debug.Activator;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.ActiveObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.WorldObject;


public class DebugStepByStepEnableAction extends AbstractStepByStepAction {

    public static final String DEBUGSTEPBYSTEPENABLE = "DEBUG_STEPBYSTEP01_Enable Step by Step mode";

    public DebugStepByStepEnableAction() {
        super.setId(DEBUGSTEPBYSTEPENABLE);
        super.setImageDescriptor(ImageDescriptor.createFromURL(FileLocator.find(
                org.objectweb.proactive.ic2d.debug.Activator.getDefault().getBundle(), new Path(
                    "icons/pause.gif"), null)));
        super.setText("Enable Step by Step mode");
        super.setToolTipText("Enable Step by Step mode");
        super.setEnabled(false);
    }

    /**
     * Set the action's target and set the correct display Text of the contextual menu action.
     * Enable or Disable for an active object, or only Enable for a container ( node, jvm, host,
     * world ).
     *
     * @param object - the action's target
     */
    public void setAbstractDataObject(AbstractData<?, ?> object) {
        if (object instanceof WorldObject) {
            super.setEnabled(false);
            return;
        }
        this.target = object;
        if (target instanceof ActiveObject) {
            if (((ActiveObject) target).getDebugInfo().isStepByStepMode()) {
                super.setText("Disable Step by Step in " + object.getName());
                super.setImageDescriptor(ImageDescriptor.createFromURL(FileLocator.find(Activator
                        .getDefault().getBundle(), new Path("icons/resume.gif"), null)));
            } else {
                super.setText("Enable Step by Step in " + object.getName());
                super.setImageDescriptor(ImageDescriptor.createFromURL(FileLocator.find(Activator
                        .getDefault().getBundle(), new Path("icons/pause.gif"), null)));
            }
        } else {
            super.setText("Enable Step by Step in " + object.getName());
            super.setImageDescriptor(ImageDescriptor.createFromURL(FileLocator.find(Activator.getDefault()
                    .getBundle(), new Path("icons/pause.gif"), null)));
        }
        super.setEnabled(true);
    }

    @Override
    protected void executeAction(ActiveObject obj) {
        if (obj.getDebugInfo().isStepByStepMode()) {
            obj.disableStepByStep();
        } else {
            obj.enableStepByStep();
        }
    }

}
