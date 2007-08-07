package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;

public class ShutdownSchedulerAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static ShutdownSchedulerAction instance = null;

	private ShutdownSchedulerAction() {
		this.setText("Shutdown scheduler");
		this.setToolTipText("To shutdown the scheduler (This will finish all running and pending jobs before shutdwon)");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/kill.png"));
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {
		//TODO demander si c'est sur ou non ...
		SchedulerProxy.getInstance().shutdown();
	}

	public static ShutdownSchedulerAction newInstance() {
		instance = new ShutdownSchedulerAction();
		return instance;
	}

	public static ShutdownSchedulerAction getInstance() {
		if (instance == null)
			instance = new ShutdownSchedulerAction();
		return instance;
	}
}
