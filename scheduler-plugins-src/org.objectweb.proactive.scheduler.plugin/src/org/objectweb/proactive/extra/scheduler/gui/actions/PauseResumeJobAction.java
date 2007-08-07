package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.TableItem;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;
import org.objectweb.proactive.extra.scheduler.gui.data.TableManager;
import org.objectweb.proactive.extra.scheduler.job.JobId;

public class PauseResumeJobAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static PauseResumeJobAction instance = null;
	private boolean paused = false;

	private PauseResumeJobAction() {
		setPauseResumeMode();
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {
		TableItem item = TableManager.getInstance().getLastSelectedItem();
		if (item != null) {
			JobId jobId = (JobId) item.getData();
			if(paused)
				SchedulerProxy.getInstance().resume(jobId);
			else
				SchedulerProxy.getInstance().pause(jobId);
		}
	}
	
	public void setPauseMode() {
		paused = false; // If I set the text to "pause", so the job is running ! So not paused
		this.setText("Pause");
		this.setToolTipText("To pause this job");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/pause.png"));
	}
	
	public void setResumeMode() {
		paused = true; // If I set the text to "resume", so the job is paused !
		this.setText("Resume");
		this.setToolTipText("To resume this job");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/resume.png"));
	}
	
	public void setPauseResumeMode() {
		this.setEnabled(false);
		this.setText("Pause / Resume");
		this.setToolTipText("To pause or resume a job");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/resume.png"));
	}

	public static PauseResumeJobAction newInstance() {
		instance = new PauseResumeJobAction();
		return instance;
	}

	public static PauseResumeJobAction getInstance() {
		if (instance == null)
			instance = new PauseResumeJobAction();
		return instance;
	}
}
