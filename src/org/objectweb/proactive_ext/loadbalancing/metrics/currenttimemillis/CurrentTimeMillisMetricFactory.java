package org.objectweb.proactive_ext.loadbalancing.metrics.currenttimemillis;

import org.objectweb.proactive_ext.loadbalancing.metrics.Metric;
import org.objectweb.proactive_ext.loadbalancing.metrics.MetricFactory;

public class CurrentTimeMillisMetricFactory implements MetricFactory {
	private double creationTime;
	
	public CurrentTimeMillisMetricFactory(){
		this.creationTime = System.currentTimeMillis();
	}
	public Metric getNewMetric() {

		return new CurrentTimeMillisMetric(creationTime);
		
	}

}
