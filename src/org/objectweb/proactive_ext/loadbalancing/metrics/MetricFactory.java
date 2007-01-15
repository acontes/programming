package org.objectweb.proactive_ext.loadbalancing.metrics;

import java.io.Serializable;

public interface MetricFactory  extends Serializable {
	public Metric getNewMetric();
}
