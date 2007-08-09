package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;

public class ShutdownSchedulerAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static ShutdownSchedulerAction instance = null;
	private Shell shell = null;

	private ShutdownSchedulerAction(Shell shell) {
		this.shell = shell;
		this.setText("Shutdown scheduler");
		this
				.setToolTipText("To shutdown the scheduler (This will finish all running and pending jobs before shutdown)");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/kill.png"));
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {
		if (MessageDialog.openConfirm(shell, "Confirm please",
				"Are you sure you want to shutting down the scheduler ?")) {
			SchedulerProxy.getInstance().shutdown();
		}
	}

	public static ShutdownSchedulerAction newInstance(Shell shell) {
		instance = new ShutdownSchedulerAction(shell);
		return instance;
	}

	public static ShutdownSchedulerAction getInstance() {
		return instance;
	}
}
