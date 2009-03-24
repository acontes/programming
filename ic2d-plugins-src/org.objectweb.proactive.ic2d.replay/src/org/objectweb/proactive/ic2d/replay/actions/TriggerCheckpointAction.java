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
package org.objectweb.proactive.ic2d.replay.actions;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.objectweb.proactive.api.PAFaultTolerance;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.WorldObject;
import org.objectweb.proactive.ic2d.replay.Activator;
import org.objectweb.proactive.ic2d.replay.data.ApplicationReferencer;
import org.objectweb.proactive.ic2d.replay.views.CheckpointsView;


public class TriggerCheckpointAction extends Action {

    public static final String CHECKPOINTTRIGGER = "CHECKPOINTVIEW_01_Trigger";

    private State state;
    private int progressState;
    private int progressSleepTime = 100;

    private CheckpointsView view;

    public TriggerCheckpointAction(CheckpointsView view) {
        super.setId(CHECKPOINTTRIGGER);
        super.setImageDescriptor(ImageDescriptor.createFromURL(FileLocator.find(Activator.getDefault()
                .getBundle(), new Path("icons/trigger.gif"), null)));
        super.setText("Trigger checkpoint");
        super.setToolTipText("Trigger checkpoint");
        this.state = State.INACTIVE;
    }

    public void dispose() { /* Do nothing */
    }

    public void init(IWorkbenchWindow window) { /* Do nothing */
    }

    public void run(IAction action) {
        this.run();
    }

    public void selectionChanged(IAction action, ISelection selection) { /* Do nothing */
    }

    @Override
    public void run() {
        setActive();
        WorldObject world;
        world = ApplicationReferencer.getInstance().getWorldObject();
        if (world != null) {
            System.out.println("Replay: trigger checkpoint");
            final TriggerCheckpointAction self = this;
            PAFaultTolerance ft = PAFaultTolerance.getInstance();
            ft.triggerGlobalCheckpoint(new PAFaultTolerance.CheckpointReceiver() {
                @Override
                public void receiveCheckpoint(int line) {
                    System.out.println("Replay: received line: " + line);
                    self.setInactive();
                }
            });
        }
    }

    public void setActive() {
        state = State.ACTIVE;
        startProgressUI();
    }

    public void setInactive() {
        state = State.INACTIVE;
        stopProgressUI();
        super.setImageDescriptor(ImageDescriptor.createFromURL(FileLocator.find(Activator.getDefault()
                .getBundle(), new Path("icons/trigger.gif"), null)));
    }

    private void startProgressUI() {
        final TriggerCheckpointAction self = this;
        new Thread() {
            public void run() {
                while (state.isActive()) {
                    progressState = (progressState % 8) + 1;
                    self.setImageDescriptor(ImageDescriptor
                            .createFromURL(FileLocator.find(Activator.getDefault().getBundle(), new Path(
                                "icons/ani/" + progressState + ".png"), null)));
                    try {
                        Thread.sleep(progressSleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void stopProgressUI() {
        progressState = 0;
    }

    private enum State {
        INACTIVE(false), ACTIVE(true);

        private boolean value;

        private State(boolean value) {
            this.value = value;
        }

        public boolean isActive() {
            return value;
        }

        public boolean isInactive() {
            return !value;
        }
    }

}
