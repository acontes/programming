package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.Serializable;

public abstract class Metric implements Serializable {

	/** The value hold by the Metric */
	Object value = null;
	
	/** Metric must be updated or not */
	boolean enabled = true;
	
	/**
	 * Calculates the value of the metric, using the parameters provided
	 * @param params
	 * @return
	 */
	Object calculate(Object[] params) {
		return null;
	}
	
	/**
	 * Returns the current value of the metric, without any recalculating
	 * @return
	 */
	Object getValue() {
		return null;
	}
	
	/**
	 * Sets arbitrarily the value of the metric
	 * @param v
	 */
	void setValue(Object v) {
		value = v;
	}

	/**
	 * Enable/disable the metric. Default is enabled.
	 */
	void enable() {
		enabled = true;
	}
	void disable() {
		enabled = false;
	}
	
}
