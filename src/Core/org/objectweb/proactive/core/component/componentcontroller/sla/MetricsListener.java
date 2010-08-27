package org.objectweb.proactive.core.component.componentcontroller.sla;

import java.util.Set;

public interface MetricsListener {

	void onMetric(Set<String> updatedMetrics);
	
}
