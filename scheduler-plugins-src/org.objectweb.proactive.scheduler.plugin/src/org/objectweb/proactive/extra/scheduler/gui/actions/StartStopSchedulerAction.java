package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;

/**
 * 
 * 
 * @author ProActive Team
 * @version 1.0, Aug 8, 2007
 * @since ProActive 3.2
 */
public class StartStopSchedulerAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static StartStopSchedulerAction instance = null;
	private boolean started = false;

	private StartStopSchedulerAction() {
		setStartStopMode();
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
		started = false; // If I set the text to "start", so the scheduler is
							// stopped !
		this.setText("Start scheduler");
		this.setToolTipText("To start the scheduler (this will finish start or restart the scheduler)");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/pause.png"));
	}

	public void setStopMode() {
		started = true; // If I set the text to "stop", so the scheduler is
						// started/running !
		this.setText("Stop scheduler");
		this.setToolTipText("To stop the scheduler (this will finish all pending and running jobs)");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/resume.png"));
	}

	public void setStartStopMode() {
		this.setText("Start/Stop scheduler");
		this.setToolTipText("To start or stop the scheduler");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/resume.png"));
		this.setEnabled(false);
	}

	public static StartStopSchedulerAction newInstance() {
		instance = new StartStopSchedulerAction();
		return instance;
	}

	public static StartStopSchedulerAction getInstance() {
		return instance;
	}
}
