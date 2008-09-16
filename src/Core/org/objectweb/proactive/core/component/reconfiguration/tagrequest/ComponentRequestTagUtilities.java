package org.objectweb.proactive.core.component.reconfiguration.tagrequest;

import java.io.Serializable;

import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.body.request.RequestFilter;
import org.objectweb.proactive.core.mop.MethodCall;

public interface ComponentRequestTagUtilities extends Serializable {

	void whenReceiveRequest(Object tag);
    boolean acceptRequest(Object tag);
    void preService(Object tag, Component owner);
    void postService(Object tag, Component owner);
    Object tagForOutputMethod();
	
}
