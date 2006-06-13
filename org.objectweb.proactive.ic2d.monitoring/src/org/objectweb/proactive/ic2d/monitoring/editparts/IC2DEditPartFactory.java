package org.objectweb.proactive.ic2d.monitoring.editparts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.objectweb.proactive.ic2d.monitoring.data.HostObject;
import org.objectweb.proactive.ic2d.monitoring.data.VMObject;

public class IC2DEditPartFactory implements EditPartFactory{
	
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		if (model instanceof HostObject)
			part = new HostEditPart();
		else if(model instanceof VMObject)
			part = new VMEditPart();
		else
			return null;
		part.setModel(model);
		return part;
	}
}
