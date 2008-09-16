package org.objectweb.proactive.core.component.reconfiguration.tagrequest;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.body.request.RequestFilter;

public class ComponentService {

	private ComponentServiceImpl service;

	public ComponentService(Body body) {
		service = new ComponentServiceImpl(body);
	}

	public void fifoServing() {
		service.fifoServing();
	}

	public void blockingServeOldest(RequestFilter requestFilter, long timeout) {
		service.blockingServeOldest(requestFilter, timeout);
	}

	public void blockingServeOldest(RequestFilter requestFilter) {
		service.blockingServeOldest(requestFilter);
	}

	public void blockingServeOldest() {
		service.blockingServeOldest();
	}
}
