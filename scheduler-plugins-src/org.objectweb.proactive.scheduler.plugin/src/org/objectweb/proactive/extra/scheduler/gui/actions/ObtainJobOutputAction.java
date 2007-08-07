package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.TableItem;
import org.objectweb.proactive.extra.scheduler.gui.data.JobsOutputController;
import org.objectweb.proactive.extra.scheduler.gui.data.TableManager;
import org.objectweb.proactive.extra.scheduler.job.JobId;

public class ObtainJobOutputAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;

	private static ObtainJobOutputAction instance = null;

	private ObtainJobOutputAction() {
		this.setText("Get job output");
		this.setToolTipText("To get the job output");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/output.png"));
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}

	@Override
	public void run() {
		TableItem item = TableManager.getInstance().getLastSelectedItem();
		if (item != null) {
			JobId jobId = (JobId) item.getData();
			JobsOutputController.getInstance().createJobOutput(jobId);
		}
	}

	public static ObtainJobOutputAction newInstance() {
		instance = new ObtainJobOutputAction();
		return instance;
	}

	public static ObtainJobOutputAction getInstance() {
		return instance;
	}
}
