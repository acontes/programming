/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
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
