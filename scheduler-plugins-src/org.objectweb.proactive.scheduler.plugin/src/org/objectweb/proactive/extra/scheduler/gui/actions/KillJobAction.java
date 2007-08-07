package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

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
	public void run() {}

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
