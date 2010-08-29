package org.objectweb.proactive.core.component.componentcontroller.sla;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.component.componentcontroller.AbstractPAComponentController;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.metrics.MetricsLibrary;
import org.objectweb.proactive.core.component.componentcontroller.remmos.Remmos;

public class SLAServiceImpl extends AbstractPAComponentController implements SLAService, BindingController {

	SLOStore sloStore;
	
	String[] itfList = {Remmos.SLO_STORE_ITF};
	
	public SLAServiceImpl() {
		super();
	}
	
	@Override
	public void addSLO(String name, Object rule) {

		SLORule<?> sloRule = null;
		
		if(rule instanceof String[]) {
			String [] args = (String[]) rule;
			int nArgs = args.length;
			if(nArgs < 3) {
				System.out.println("Must provide at least 3 arguments: <metricType> <condition> <threshold>");
				return;
			}
			String sloName = name;
			String metricType = args[0];
			String conditionName = args[nArgs-2];
			String threshold = args[nArgs-1];
			String[] metricArgs;
			if(nArgs == 3) {
				metricArgs = null;
			}
			else {
				metricArgs = new String[nArgs-3];
				for(int i=0; i<nArgs-3; i++) {
					metricArgs[i] = args[i+1];
				}
			}
			// TODO It should check if the combination metricType + args is already monitored in the MetricStore, or not.
			Metric<?> metric = MetricsLibrary.getInstance().getMetric(metricType);
			//Class<?> metricReturnType = metric.getClass().getSuperclass().getTypeParameters()[0].getClass();
			System.out.println("sloName         : "+ sloName);
			System.out.println("metricType      : "+ metricType);
			//System.out.println("metricReturnType: "+ metricReturnType.getName());
			System.out.println("conditionName   : "+ conditionName);
			System.out.println("threshold       : "+ threshold);
			
			
			// parse the string, create the rule and store it
			//sloStore.addSLO("newRule", sloRule);
			return;
		}
		System.out.println("Don't know how to handle rules contained in "+ rule.getClass().getName());
	}

	@Override
	public void disableSLO(String name) {
		sloStore.disableSLO(name);		
	}

	@Override
	public void enableSLO(String name) {
		sloStore.enableSLO(name);		
	}

	@Override
	public void removeSLO(String name) {
		sloStore.removeSLO(name);
	}

	@Override
	public void bindFc(String itfName, Object itf)
			throws NoSuchInterfaceException, IllegalBindingException,
			IllegalLifeCycleException {
		if(itfName.equals(Remmos.SLO_STORE_ITF)) {
			sloStore = (SLOStore) itf;
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
		if(itfName.equals(Remmos.SLO_STORE_ITF)) {
			return sloStore;
		}
		throw new NoSuchInterfaceException("Interface "+ itfName +" not found!");
	}

	@Override
	public void unbindFc(String itfName) throws NoSuchInterfaceException,
			IllegalBindingException, IllegalLifeCycleException {
		if(itfName.equals(Remmos.SLO_STORE_ITF)) {
			sloStore = null;
		}
		throw new NoSuchInterfaceException("Interface "+ itfName +" not found!");
	}
	
}
