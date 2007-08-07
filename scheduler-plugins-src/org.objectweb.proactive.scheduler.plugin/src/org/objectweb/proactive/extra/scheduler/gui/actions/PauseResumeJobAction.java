package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public class PauseResumeJobAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static PauseResumeJobAction instance = null;

	private PauseResumeJobAction() {
		setPauseResumeMode();
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {}
	
	public void setPauseMode() {
		this.setText("Pause");
		this.setToolTipText("To pause this job");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/pause.png"));
	}
	
	public void setResumeMode() {
		this.setText("Resume");
		this.setToolTipText("To resume this job");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/resume.png"));
	}
	
	public void setPauseResumeMode() {
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
