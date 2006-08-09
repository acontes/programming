package org.objectweb.proactive.ic2d.monitoring.figures.listeners;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.objectweb.proactive.ic2d.monitoring.actions.RefreshAction;
import org.objectweb.proactive.ic2d.monitoring.views.MonitoringView;

public class WorldListener implements MouseListener {

	private ActionRegistry registry;

	public WorldListener() {
		this.registry = MonitoringView.getInstance().getGraphicalViewer().getActionRegistry();
	}

	public void mouseDoubleClicked(MouseEvent me) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent me) {
		if(me.button == 3) {
			System.out.println("WorldListener.mousePressed()");
			registry.getAction(RefreshAction.REFRESH).setEnabled(true);
		}
	}

	public void mouseReleased(MouseEvent me) {
		// TODO Auto-generated method stub

	}

}
