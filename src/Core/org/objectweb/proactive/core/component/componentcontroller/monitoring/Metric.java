package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;

/**
 * Parent class for all metrics, which produce a value of type T.
 * @author cruz
 *
 * @param <T>
 */
public abstract class Metric<T> implements Serializable {

	/** The record source */
	protected RecordStore records = null;
	
	/** The value hold by the Metric */
	protected T value = null;
	
	/** Metric must be updated or not */
	protected boolean enabled = true;
	
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
	
}
