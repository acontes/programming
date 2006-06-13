package org.objectweb.proactive.ic2d.monitoring.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.objectweb.proactive.ic2d.monitoring.views.*;


public class MonitoringPerspective implements IPerspectiveFactory {
	
	public static final String ID = "org.objectweb.proactive.ic2d.monitoring.perspectives.MonitoringPerspective";
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea=layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);
		
		/*layout.addView(MonitoringView.ID, IPageLayout.LEFT,
				0.25f, editorArea);*/
		layout.addPerspectiveShortcut(ID);
		layout.addShowViewShortcut(MonitoringView.ID);
		
	}
	
}
