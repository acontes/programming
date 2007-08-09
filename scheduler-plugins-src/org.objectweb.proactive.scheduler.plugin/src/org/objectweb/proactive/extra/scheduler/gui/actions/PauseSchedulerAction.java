package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;

public class PauseSchedulerAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static PauseSchedulerAction instance = null;

	private PauseSchedulerAction() {
		this.setText("Pause scheduler");
		this.setToolTipText("To pause the scheduler (All running Jobs will be terminated)");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/output.png"));
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {
		SchedulerProxy.getInstance().pause();
	}

	public static PauseSchedulerAction newInstance() {
		instance = new PauseSchedulerAction();
		return instance;
	}

	public static PauseSchedulerAction getInstance() {
		return instance;
	}
}
