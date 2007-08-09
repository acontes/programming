package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;

public class KillSchedulerAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static KillSchedulerAction instance = null;
	private Shell shell = null;

	private KillSchedulerAction(Shell shell) {
		this.shell = shell;
		this.setText("Kill scheduler");
		this.setToolTipText("To kill the scheduler (this kill immediately the scheduler)");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/kill.png"));
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {
		if (MessageDialog.openConfirm(shell, "Confirm please",
				"Are you sure you want to Kill the scheduler ?")) {
			if (SchedulerProxy.getInstance().kill()) {
				// disable all buttons

				SubmitJobAction.getInstance().setEnabled(false);
				ObtainJobOutputAction.getInstance().setEnabled(true);
				PauseResumeJobAction.getInstance().setEnabled(false);
				KillJobAction.getInstance().setEnabled(false);

				StartStopSchedulerAction.getInstance().setEnabled(false);
				FreezeSchedulerAction.getInstance().setEnabled(false);
				PauseSchedulerAction.getInstance().setEnabled(false);
				ResumeSchedulerAction.getInstance().setEnabled(false);
				ShutdownSchedulerAction.getInstance().setEnabled(false);
				this.setEnabled(false);
			}
		}
	}

	public static KillSchedulerAction newInstance(Shell shell) {
		instance = new KillSchedulerAction(shell);
		return instance;
	}

	public static KillSchedulerAction getInstance() {
		return instance;
	}
}
