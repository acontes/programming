package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.component.componentcontroller.AbstractPAComponentController;
import org.objectweb.proactive.core.component.componentcontroller.remmos.Remmos;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;

public class MetricsStoreImpl extends AbstractPAComponentController implements MetricsStore, BindingController {

	private static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_MONITORING);
	
	Map<String, Metric<?>> metrics;
	
	RecordStore records;
	
	String[] itfList = {Remmos.RECORD_STORE_ITF};
	
	@Override
	public void init() {
		metrics = new HashMap<String, Metric<?>>();
	}
	
	@Override
	public void addMetric(String name, Metric<?> metric) {
		metric.setRecordSource(records);
		metrics.put(name, metric);
	}

	@Override
	public Object calculate(String name, Object[] params) {
		Metric metric = metrics.get(name);
		Object result = null;
		if(metric != null) {
			result = metric.calculate(params);
		}
		return result;
	}

	@Override
	public void disableMetric(String name) {
		Metric metric = metrics.get(name);
		if(metric != null) {
			metric.disable();
		}
	}

	@Override
	public void enableMetric(String name) {
		Metric metric = metrics.get(name);
		if(metric != null) {
			metric.enable();
		}
	}

	@Override
	public Object getValue(String name) {
		Metric metric = metrics.get(name);
		Object result = null;
		if(metric != null) {
			result = metric.getValue();
		}
		return result;
	}

	@Override
	public void removeMetric(String name) {
		metrics.remove(name);
	}

	@Override
	public void setValue(String name, Object v) {
		Metric metric = metrics.get(name);
		if(metric != null) {
			metric.setValue(v);
		}
	}
	
	@Override
	public Set<String> getMetricList() {
		return metrics.keySet();
	}


	@Override
	public void bindFc(String itfName, Object obj)
			throws NoSuchInterfaceException, IllegalBindingException,
			IllegalLifeCycleException {
		if(itfName.equals(Remmos.RECORD_STORE_ITF)) {
			records = (RecordStore) obj;
		}
		else {
			throw new NoSuchInterfaceException("Interface "+ itfName + " not found.");
		}
	}

	@Override
	public String[] listFc() {
		return itfList;
	}

	@Override
	public Object lookupFc(String itfName) throws NoSuchInterfaceException {
		if(itfName.equals(Remmos.RECORD_STORE_ITF)) {
			return records;
		}
		throw new NoSuchInterfaceException("Interface "+ itfName + " not found.");
	}

	@Override
	public void unbindFc(String itfName) throws NoSuchInterfaceException,
			IllegalBindingException, IllegalLifeCycleException {
		if(itfName.equals(Remmos.RECORD_STORE_ITF)) {
			records = null;
		}
		else {
			throw new NoSuchInterfaceException("Interface "+ itfName + " not found.");
		}
	}



}
