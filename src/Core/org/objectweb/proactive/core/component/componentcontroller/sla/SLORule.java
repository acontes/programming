package org.objectweb.proactive.core.component.componentcontroller.sla;

import java.io.Serializable;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControl;

/**
 * This class represents a very simple rule.
 * metricValue <condition> threshold value.
 * 
 * If the condition fails, an alarm is sent.
 *  * Optionally, it contains a preventive value that triggers a preventive alarm.
 * 
 * 
 * @author cruz
 *
 */
public class SLORule<T> implements Serializable {

	/** The name of the monitored metric */
	String metricName;
	
	T metricValue;
	
	Condition<T> condition;
	
	T threshold;

	T preventiveThreshold;
	
	private MonitorControl monitor; 
	
	boolean enabled = true;
	/** 
	 * Checks the condition and determines if an alarm must be raised
	 * 
	 * @return
	 */
	public AlarmLevel check() {
		
		metricValue = (T) monitor.runMetric(metricName);
		
		if(condition.evaluate(metricValue, threshold)) {
			return AlarmLevel.OK;
		}
		return null;
	}
	
	public boolean isEnabled() {
		return enabled; 
	}
	
	public void setEnabled(boolean e) {
		enabled = e;
	}
	
	
}
