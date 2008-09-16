package org.objectweb.proactive.core.component.reconfiguration.tagrequest;

import java.util.Random;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.body.request.RequestFilter;
import org.objectweb.proactive.core.mop.MethodCall;

public class ComponentRequestTagUtilitiesDefault implements ComponentRequestTagUtilities {

	public boolean acceptRequest(Object tag) {
		// TODO Auto-generated method stub
		return true;
	}

	public void postService(Object tag, Component owner) {
		// TODO Auto-generated method stub
	}

	public void preService(Object tag, Component owner) {
		// TODO Auto-generated method stub

	}

	public Object tagForOutputMethod() {
		// TODO Auto-generated method stub
		return null; 
	}

	public void whenReceiveRequest(Object tag) {
		// TODO Auto-generated method stub
	}

}
