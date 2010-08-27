package org.objectweb.proactive.core.component.componentcontroller.sla;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.component.componentcontroller.AbstractPAComponentController;

public class SLAServiceImpl extends AbstractPAComponentController implements BindingController {

	@Override
	public void bindFc(String arg0, Object arg1)
			throws NoSuchInterfaceException, IllegalBindingException,
			IllegalLifeCycleException {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] listFc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookupFc(String arg0) throws NoSuchInterfaceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unbindFc(String arg0) throws NoSuchInterfaceException,
			IllegalBindingException, IllegalLifeCycleException {
		// TODO Auto-generated method stub

	}

}
