package org.objectweb.proactive.core.component.componentcontroller.sla;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.component.componentcontroller.AbstractPAComponentController;
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
		// TODO Auto-generated method stub

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
