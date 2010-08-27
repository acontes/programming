package org.objectweb.proactive.core.component.componentcontroller.sla;

import java.io.Serializable;

public interface Condition<P> extends Serializable {
	
	boolean evaluate(P object1, P object2);

}
