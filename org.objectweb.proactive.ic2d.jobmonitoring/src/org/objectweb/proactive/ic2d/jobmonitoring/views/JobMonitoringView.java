package org.objectweb.proactive.ic2d.jobmonitoring.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.ic2d.jobmonitoring.gui.JobMonitoringContentProvider;
import org.objectweb.proactive.ic2d.jobmonitoring.gui.JobMonitoringLabelProvider;
import org.objectweb.proactive.ic2d.monitoring.data.WorldObject;

public class JobMonitoringView extends ViewPart {

	public JobMonitoringView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		TreeViewer treeViewer = new TreeViewer(parent);
		
		treeViewer.setContentProvider(new JobMonitoringContentProvider());
		treeViewer.setLabelProvider(new JobMonitoringLabelProvider());
		treeViewer.setInput(WorldObject.getInstance());
		
		/*
		Tree tree = new Tree(parent, SWT.SINGLE);
		
		TreeItem treeItem = new TreeItem(tree, SWT.NONE);
		treeItem.setText("My first TreeItem");
		Image icon = new Image(Display.getCurrent(), "icon/alt_window_16.gif");
		treeItem.setImage(icon);
		*/
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
