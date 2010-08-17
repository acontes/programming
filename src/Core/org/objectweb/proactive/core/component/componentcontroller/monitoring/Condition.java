package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;

public interface Condition<P> extends Serializable {
	
	boolean evaluate(P object);

}
