package org.objectweb.proactive.ic2d.monitoring.actions;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Display;

public class MonitoringContextMenuProvider extends ContextMenuProvider {

	private Display display;
	
	public MonitoringContextMenuProvider(EditPartViewer viewer, Display display) {
		super(viewer);
		this.display = display;
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		
		menu.add(new RefreshAction());
		menu.add(new NewHostAction(display));
	}

}
