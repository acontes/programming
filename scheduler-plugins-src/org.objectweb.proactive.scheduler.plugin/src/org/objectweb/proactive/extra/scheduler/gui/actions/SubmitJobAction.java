package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobFactory;

public class SubmitJobAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static SubmitJobAction instance = null;
	private Composite parent = null;

	private SubmitJobAction(Composite parent) {
		this.parent = parent;
		this.setText("Submit a job");
		this.setToolTipText("Submit a job to the scheduler");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/submit.png"));
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {
		// TODO
		FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.OPEN);
		fileDialog.setFilterExtensions(new String[] { "*.xml" });
		String fileName = fileDialog.open();

		if (fileName != null) {
			try {
				// CREATE JOB
				Job job = JobFactory.getFactory().createJob(fileName);
				// SUBMIT JOB
				job.setId(SchedulerProxy.getInstance().submit(job));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static SubmitJobAction newInstance(Composite parent) {
		instance = new SubmitJobAction(parent);
		return instance;
	}

	public static SubmitJobAction getInstance() {
		return instance;
	}
}
