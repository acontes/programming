
package org.objectweb.proactive.infrastructuremanager.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.objectweb.proactive.infrastructuremanager.composite.IMCompositeDescriptor;
import org.objectweb.proactive.infrastructuremanager.composite.IMCompositeVNode;

public class IMActionCollapseAll extends Action {
	
	private ExpandBar expandBar;

	public IMActionCollapseAll(ExpandBar bar) {
		super("Collapse All");
		expandBar = bar;
		setImageDescriptor(ImageDescriptor.createFromFile(this.getClass(), "collapse.gif"));
		setToolTipText("Collapse All");
	}

	@Override
	public void run() {
		for(ExpandItem item : expandBar.getItems()) {
			item.setExpanded(false);
			for(IMCompositeVNode vnode : ((IMCompositeDescriptor) item.getControl()).getVnodes().values()) {
				vnode.setCollapse(true);
			}
		}
	}
	
}
