package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;

public class ResumeSchedulerAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static ResumeSchedulerAction instance = null;

	private ResumeSchedulerAction() {
		this.setText("Resume scheduler");
		this.setToolTipText("To resume the scheduler");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/output.png"));
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {
		SchedulerProxy.getInstance().resume();
	}

	public static ResumeSchedulerAction newInstance() {
		instance = new ResumeSchedulerAction();
		return instance;
	}

	public static ResumeSchedulerAction getInstance() {
		return instance;
	}
}
