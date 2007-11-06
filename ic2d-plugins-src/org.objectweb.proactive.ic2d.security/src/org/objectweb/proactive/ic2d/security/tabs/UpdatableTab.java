package org.objectweb.proactive.ic2d.security.tabs;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;


public abstract class UpdatableTab extends CTabItem {
    public UpdatableTab(CTabFolder parent, int style) {
        super(parent, style);
    }

    abstract public void update();
}
