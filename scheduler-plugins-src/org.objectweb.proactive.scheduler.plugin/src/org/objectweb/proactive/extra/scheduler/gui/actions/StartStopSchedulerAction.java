package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;

public class StartStopSchedulerAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static StartStopSchedulerAction instance = null;
	private boolean started = false;

	private StartStopSchedulerAction() {
		setStartMode();
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {
		if (started)
			SchedulerProxy.getInstance().stop();
		else
			SchedulerProxy.getInstance().start();
	}

	public void setStartMode() {
		started = false; // If I set the text to "start", so the scheduler is running ! So not started
		this.setText("Start scheduler");
		this.setToolTipText("To start the scheduler (this will finish start or restart the scheduler)");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/pause.png"));
	}

	public void setStopMode() {
		started = true; // If I set the text to "resume", so the scheduler is started !
		this.setText("Stop scheduler");
		this.setToolTipText("To stop the scheduler (this will finish all pending and running jobs)");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/resume.png"));
	}

	public static StartStopSchedulerAction newInstance() {
		instance = new StartStopSchedulerAction();
		return instance;
	}

	public static StartStopSchedulerAction getInstance() {
		if (instance == null)
			instance = new StartStopSchedulerAction();
		return instance;
	}
}
