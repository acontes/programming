package org.objectweb.proactive.core.component.reconfiguration.tagrequest;

import org.objectweb.fractal.api.Component;

public class PropagateComponentRequestTagUtilities implements ComponentRequestTagUtilities{

	private Object tagRequest = null;
	
	public boolean acceptRequest(Object tag) {
		// TODO Auto-generated method stub
		return false;
	}

	public void postService(Object tag, Component owner) {
		// TODO Auto-generated method stub
		tagRequest = null;
	}

	public void preService(Object tag, Component owner) {
		// TODO Auto-generated method stub
		tagRequest = tag;
	}

	public Object tagForOutputMethod() {
		// TODO Auto-generated method stub
		return tagRequest;
	}

	public void whenReceiveRequest(Object tag) {
		// TODO Auto-generated method stub
		
	}

}
