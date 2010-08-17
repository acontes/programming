package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;

public abstract class Metric implements Serializable {

	/** The record source */
	protected RecordStore records = null;
	
	/** The value hold by the Metric */
	protected Object value = null;
	
	/** Metric must be updated or not */
	protected boolean enabled = true;
	
	/**
	 * Calculates the value of the metric, using the parameters provided
	 * @param params
	 * @return
	 */
	public Object calculate(Object[] params) {
		return null;
	}
	
	/**
	 * Returns the current value of the metric, without any recalculating
	 * @return
	 */
	public Object getValue() {
		return null;
	}
	
	/**
	 * Sets arbitrarily the value of the metric
	 * @param v
	 */
	public void setValue(Object v) {
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
