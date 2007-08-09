package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;
import org.objectweb.proactive.extra.scheduler.gui.data.TableManager;
import org.objectweb.proactive.extra.scheduler.job.JobId;

public class KillJobAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static KillJobAction instance = null;
	private Shell shell = null;

	private KillJobAction(Shell shell) {
		this.shell = shell;
		this.setText("Kill job");
		this.setToolTipText("To kill a job (this will remove this job from the scheduler)");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/kill.png"));
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {
		if (MessageDialog.openConfirm(shell, "Confirm please",
				"Are you sure you want to Kill the scheduler ?")) {
			TableItem item = TableManager.getInstance().getLastSelectedItem();
			if (item != null) {
				JobId jobId = (JobId) item.getData();
				SchedulerProxy.getInstance().kill(jobId);
			}
		}
	}

	public static KillJobAction newInstance(Shell shell) {
		instance = new KillJobAction(shell);
		return instance;
	}

	public static KillJobAction getInstance() {
		return instance;
	}
}
