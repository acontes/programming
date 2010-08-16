package org.objectweb.proactive.core.component.componentcontroller.monitoring;

public interface Condition<P> {
	
	boolean evaluate(P object);

}
