package org.objectweb.proactive.core.component.reconfiguration.tagrequest;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.component.body.NFRequestFilterImpl;
import org.objectweb.proactive.core.component.controller.AbstractProActiveController;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactoryImpl;

public class ProActiveAOPLikeControllerImpl extends AbstractProActiveController
		implements ProActiveAOPLikeController {

	private ComponentRequestTagUtilities utilities = new ComponentRequestTagUtilitiesDefault();


	public ProActiveAOPLikeControllerImpl(Component owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setControllerItfType() {
		// TODO Auto-generated method stub

		try {
			setItfType(ProActiveTypeFactoryImpl.instance().createFcItfType(
					ProActiveAOPLikeController.AOP_LIKE_CONTROLLER_NAME,
					ProActiveAOPLikeController.class.getName(),
					TypeFactory.SERVER, TypeFactory.MANDATORY,
					TypeFactory.SINGLE));
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ComponentRequestTagUtilities getRequestTagUtilities() {
		// TODO Auto-generated method stub
		return this.utilities;
	}

	public void setRequestTagUtilities(ComponentRequestTagUtilities utilities) {
		// TODO Auto-generated method stub
		this.utilities = utilities;
	}


}
