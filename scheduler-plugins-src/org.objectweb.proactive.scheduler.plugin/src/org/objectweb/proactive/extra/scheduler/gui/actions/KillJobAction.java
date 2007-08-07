package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.TableItem;
import org.objectweb.proactive.extra.scheduler.gui.data.SchedulerProxy;
import org.objectweb.proactive.extra.scheduler.gui.data.TableManager;
import org.objectweb.proactive.extra.scheduler.job.JobId;

public class KillJobAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static KillJobAction instance = null;

	private KillJobAction() {
		this.setText("Kill job");
		this.setToolTipText("To kill a job");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/kill.png"));
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {
		TableItem item = TableManager.getInstance().getLastSelectedItem();
		if (item != null) {
			JobId jobId = (JobId) item.getData();
			SchedulerProxy.getInstance().kill(jobId);
		}
	}

	public static KillJobAction newInstance() {
		instance = new KillJobAction();
		return instance;
	}

	public static KillJobAction getInstance() {
		if (instance == null)
			instance = new KillJobAction();
		return instance;
	}
}
