package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.List;
import java.util.Set;

/**
 * Interface for a component that stores and updates Metrics.
 * @author cruz
 *
 */

public interface MetricsStore {

	void init();
	
	void addMetric(String name, Metric<?> metric);
	
	void removeMetric(String name);
	
	void disableMetric(String name);
	
	void enableMetric(String name);
	
	Object calculate(String name, Object[] params);
	
	Object getValue(String name);
	
	void setValue(String name, Object v);
	
	List<String> getMetricList();
	
}
