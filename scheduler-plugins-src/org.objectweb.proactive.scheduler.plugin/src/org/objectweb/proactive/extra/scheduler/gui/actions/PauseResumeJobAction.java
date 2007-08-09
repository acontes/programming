package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.TableItem;
import org.objectweb.proactive.extra.scheduler.gui.data.JobsController;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;
import org.objectweb.proactive.extra.scheduler.gui.data.TableManager;
import org.objectweb.proactive.extra.scheduler.job.JobId;

public class PauseResumeJobAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static PauseResumeJobAction instance = null;

	private PauseResumeJobAction() {
		setPauseResumeMode();
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {
		TableItem item = TableManager.getInstance().getLastSelectedItem();
		if (item != null) {
			JobId jobId = (JobId) item.getData();
			if (JobsController.getLocalView().getJobById(jobId).isPaused()) {
				SchedulerProxy.getInstance().resume(jobId);
				setPauseMode();
			} else {
				SchedulerProxy.getInstance().pause(jobId);
				setResumeMode();
			}
		}
	}

	public void setPauseMode() {
		this.setText("Pause job");
		this.setToolTipText("To pause this job (this will finish all running tasks)");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/pause.png"));
	}

	public void setResumeMode() {
		this.setText("Resume job");
		this.setToolTipText("To resume this job (this will restart all paused tasks)");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/resume.png"));
	}

	public void setPauseResumeMode() {
		this.setEnabled(false);
		this.setText("Pause/Resume job");
		this.setToolTipText("To pause or resume a job");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/resume.png"));
	}

	public static PauseResumeJobAction newInstance() {
		instance = new PauseResumeJobAction();
		return instance;
	}

	public static PauseResumeJobAction getInstance() {
		return instance;
	}
}