package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;

public class FreezeSchedulerAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static FreezeSchedulerAction instance = null;

	private FreezeSchedulerAction() {
		this.setText("Freeze scheduler");
		this.setToolTipText("To freeze the scheduler (Only all running Tasks will be terminated)");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/output.png"));
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {
		SchedulerProxy.getInstance().pauseImmediate();
	}

	public static FreezeSchedulerAction newInstance() {
		instance = new FreezeSchedulerAction();
		return instance;
	}

	public static FreezeSchedulerAction getInstance() {
		return instance;
	}
}
