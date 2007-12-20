package org.objectweb.proactive.compi2.control.controller;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.compi2.control.DGConstants;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.controller.AbstractProActiveController;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactoryImpl;

public class DGAttributeControllerImpl extends AbstractProActiveController 
										implements DGAttributeController {


	public DGAttributeControllerImpl(Component owner) {
		super(owner);
	}

	@Override
	protected void setControllerItfType() {
		try {
			setItfType(ProActiveTypeFactoryImpl.instance().createFcItfType(DGConstants.DG_ATTRIBUTE_CONTROLLER, 
					DGAttributeController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, TypeFactory.SINGLE));
		} catch (InstantiationException e) {
			throw new ProActiveRuntimeException("cannot create controller " +
					this.getClass().getName());
		}
		
	}
	
}
