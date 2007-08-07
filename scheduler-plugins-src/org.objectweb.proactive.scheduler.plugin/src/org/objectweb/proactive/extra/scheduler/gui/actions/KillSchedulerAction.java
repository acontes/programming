package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;

public class KillSchedulerAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static KillSchedulerAction instance = null;

	private KillSchedulerAction() {
		this.setText("Kill scheduler");
		this.setToolTipText("To kill the scheduler");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/kill.png"));
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {
		//TODO demander si c'est sur ou non ...
		SchedulerProxy.getInstance().kill();
	}

	public static KillSchedulerAction newInstance() {
		instance = new KillSchedulerAction();
		return instance;
	}

	public static KillSchedulerAction getInstance() {
		if (instance == null)
			instance = new KillSchedulerAction();
		return instance;
	}
}
