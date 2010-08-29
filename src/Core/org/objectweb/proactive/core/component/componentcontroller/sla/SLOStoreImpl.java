package org.objectweb.proactive.core.component.componentcontroller.sla;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.component.componentcontroller.AbstractPAComponentController;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControl;
import org.objectweb.proactive.core.component.componentcontroller.remmos.Remmos;

public class SLOStoreImpl extends AbstractPAComponentController implements
		SLOStore, MetricsListener, BindingController {

	private MonitorControl monitor;
	private SLANotifier slaNotifier;
	
	private Map<String, SLORule<?>> rules;
	
	String[] itfList = {Remmos.MONITOR_SERVICE_ITF, Remmos.SLA_ALARM_ITF};
	
	@Override
	public void addSLO(String name, SLORule<?> rule) {
		rules.put(name, rule);
		// create the metric, add it to the monitor, and subscribe to their updates
		String metricName = rule.getMetricName();
		Metric<?> metric = rule.getMetric();
		monitor.addMetric(metricName, metric);

	}

	@Override
	public void disableSLO(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enableSLO(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		rules = new HashMap<String, SLORule<?>>();
	}

	@Override
	public void removeSLO(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMetric(Set<String> updatedMetrics) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindFc(String itfName, Object itf)
			throws NoSuchInterfaceException, IllegalBindingException,
			IllegalLifeCycleException {
		if(itfName.equals(Remmos.MONITOR_SERVICE_ITF)) {
			monitor = (MonitorControl) itf;
			return;
		}
		if(itfName.equals(Remmos.SLA_ALARM_ITF)) {
			slaNotifier = (SLANotifier) itf;
			return;
		}
		throw new NoSuchInterfaceException("Interface "+ itfName +" not found!");
		
	}

	@Override
	public String[] listFc() {
		return itfList;
	}

	@Override
	public Object lookupFc(String itfName) throws NoSuchInterfaceException {
		if(itfName.equals(Remmos.MONITOR_SERVICE_ITF)) {
			return monitor;
		}
		if(itfName.equals(Remmos.SLA_ALARM_ITF)) {
			return slaNotifier;
		}
		throw new NoSuchInterfaceException("Interface "+ itfName +" not found!");
	}

	@Override
	public void unbindFc(String itfName) throws NoSuchInterfaceException,
			IllegalBindingException, IllegalLifeCycleException {
		if(itfName.equals(Remmos.MONITOR_SERVICE_ITF)) {
			monitor = null;
		}
		if(itfName.equals(Remmos.SLA_ALARM_ITF)) {
			slaNotifier = null;
		}
		throw new NoSuchInterfaceException("Interface "+ itfName +" not found!");
	}
}
