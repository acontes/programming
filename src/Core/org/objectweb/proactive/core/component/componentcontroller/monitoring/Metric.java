package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.event.RemmosEventType;

/**
 * Parent class for all metrics, which produce a value of type T.
 * @author cruz
 *
 * @param <T>
 */
public abstract class Metric<T> implements Serializable {

	/** The record source */
	protected RecordStore records = null;
	
	/** The listener (SLA Monitor) interested in updates of metrics */
	//protected MetricListener metricsListener = null;
	
	/** The value hold by the Metric */
	protected T value = null;
	
	/** The arguments provided to this metric */
	protected Object[] args = null;
	
	/** Metric must be updated or not */
	protected boolean enabled = true;
	
	/** Set of subscribed events */
	protected Set<RemmosEventType> subscribedEvents = new HashSet<RemmosEventType>();
	
	/**
	 * Calculates the value of the metric using the stored parameters
	 * @return
	 */
	public T calculate() {
		return calculate(args);
	}
	
	/**
	 * Calculates the value of the metric, using the parameters provided
	 * @param params
	 * @return
	 */
	public T calculate(final Object[] params) {
		return null;
	}
	
	/**
	 * Returns the current value of the metric, without any recalculation
	 * @return
	 */
	public T getValue() {
		return value;
	}
	
	/**
	 * Sets arbitrarily the value of the metric
	 * @param v
	 */
	public void setValue(T v) {
		value = v;
	}
	
	/**
	 * Sets the arguments that this Metric instance will use
	 * @param args
	 */
	public void setArgs(Object[] args) {
		this.args = args;
	}

	/**
	 * Enable/disable the metric. Default is enabled.
	 */
	public void enable() {
		enabled = true;
	}
	public void disable() {
		enabled = false;
	}
	
	public void setRecordSource(RecordStore rs) {
		records = rs;
	}
	
	public boolean isSubscribedTo(RemmosEventType ret) {
		return subscribedEvents.contains(ret);
	}
	
	public void subscribeTo(RemmosEventType ret) {
		subscribedEvents.add(ret);
	}
	
	public void unsubscribeFrom(RemmosEventType ret) {
		subscribedEvents.remove(ret);
	}
}
