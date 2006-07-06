package org.objectweb.proactive.ic2d.jobmonitoring.gui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.objectweb.proactive.ic2d.monitoring.data.AbstractDataObject;

public class JobMonitoringLabelProvider extends LabelProvider {

	public String getText(Object element) {
	    return element.toString();
	}
	
	public Image getImage(Object element) {
		String type = ((AbstractDataObject)element).getType();
		return new Image(Display.getCurrent(),
				this.getClass().getResourceAsStream(type+"_icon.png"));
	}
	
}
