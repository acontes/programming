package org.objectweb.proactive.core.component.componentcontroller.monitoring;

public interface Transformation<S,T> {
	T execute(S target);
}
