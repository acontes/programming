package org.objectweb.proactive.extra.scheduler.gui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class ChangeViewModeAction extends Action {

	public static final boolean ENABLED_AT_CONSTRUCTION = false;
	
	private static ChangeViewModeAction instance = null;
	private Composite parent = null;
	
	private ChangeViewModeAction(Composite parent) {
		this.parent = parent;
		this.setText("Switch view mode");
		this.setToolTipText("Switch view to horizontal mode");
		this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "icons/horizontal.png"));
		this.setEnabled(ENABLED_AT_CONSTRUCTION);
	}	

	@Override
	public void run() {
		FillLayout layout = (FillLayout) (parent.getLayout());
		boolean isVertical = layout.type == SWT.VERTICAL;
		layout.type = isVertical ? SWT.HORIZONTAL : SWT.VERTICAL;
		if (isVertical) {
			this.setToolTipText("Switch view to horizontal mode");
			this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(),
					"icons/horizontal.png"));
		} else {
			this.setToolTipText("Switch view to vertical mode");
			this.setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(),
					"icons/vertical.png"));
		}
		parent.layout();
	}
	
	public static ChangeViewModeAction newInstance(Composite parent) {
		instance = new ChangeViewModeAction(parent);
		return instance;
	}
	
	public static ChangeViewModeAction getInstance() {
		return instance;
	}
}
