package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;

public class MetricsStoreImpl implements MetricsStore, BindingController {

	private static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_MONITORING);
	
	Map<String, Metric> metrics;
	
	RecordHandler records;
	
	String[] itfList = {"records-nf"};
	
	@Override
	public void init() {
		metrics = new HashMap<String, Metric>();
	}
	
	@Override
	public void addMetric(String name, Metric metric) {
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
	public void bindFc(String arg0, Object arg1)
			throws NoSuchInterfaceException, IllegalBindingException,
			IllegalLifeCycleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] listFc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookupFc(String arg0) throws NoSuchInterfaceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unbindFc(String arg0) throws NoSuchInterfaceException,
			IllegalBindingException, IllegalLifeCycleException {
		// TODO Auto-generated method stub
		
	}


}
