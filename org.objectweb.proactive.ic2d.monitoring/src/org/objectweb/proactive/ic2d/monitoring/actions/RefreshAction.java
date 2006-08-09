package org.objectweb.proactive.ic2d.monitoring.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.objectweb.proactive.ic2d.monitoring.data.MonitorThread;

public class RefreshAction extends Action {
	
	public static final String REFRESH = "Refresh";
	
	public RefreshAction() {
		this.setId(REFRESH);
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "refresh.gif"));
		this.setText("Update");
		this.setToolTipText("Update");
	}
	
	public void run() {
		MonitorThread.getInstance().forceRefresh();
	}
}