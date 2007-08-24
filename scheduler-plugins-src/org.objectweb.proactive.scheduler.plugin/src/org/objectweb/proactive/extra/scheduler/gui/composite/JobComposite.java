package org.objectweb.proactive.extra.scheduler.gui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class JobComposite extends Composite {

	public JobComposite(Composite parent) {
		super(parent, SWT.NONE);
		// It must be a FillLayout !
		FillLayout layout = new FillLayout(SWT.HORIZONTAL);
		layout.spacing = 2;
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		this.setLayout(layout);
	}
}
