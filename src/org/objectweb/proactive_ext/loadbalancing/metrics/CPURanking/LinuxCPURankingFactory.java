package org.objectweb.proactive_ext.loadbalancing.metrics.CPURanking;

import org.objectweb.proactive_ext.loadbalancing.metrics.Metric;
import org.objectweb.proactive_ext.loadbalancing.metrics.MetricFactory;

public class LinuxCPURankingFactory implements MetricFactory {

	public Metric getNewMetric() {
		return new LinuxCPURanking();
	}

}
