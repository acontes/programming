package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.component.componentcontroller.AbstractProActiveComponentController;
import org.objectweb.proactive.core.component.controller.MethodStatistics;
import org.objectweb.proactive.core.component.controller.MonitorController;
import org.objectweb.proactive.core.jmx.naming.FactoryName;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;

/**
 * Monitor Controller component for the Monitoring Framework
 * 
 * This NF Component controls the behaviour of the monitoring related activity.
 * of a Component.
 * 
 * @author cruz
 *
 */
public class MonitorControl extends AbstractProActiveComponentController implements MonitorController, BindingController {

	private static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_MONITORING);
	
	private EventControl eventControl;
	private LogHandler logHandler;
	private String itfs[] = {"events-control-nf", "log-handler-nf"};
	
	/** Monitoring status */
    private boolean started = false;
	
    public MonitorControl() {
    	super();
    }
    
    public void init() {
    	// This is not called now!!!
    	logger.debug("[Monitor Controll] Init ... !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    
	@Override
	public Map<String, MethodStatistics> getAllStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MethodStatistics getStatistics(String itfName, String methodName)
			throws ProActiveRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MethodStatistics getStatistics(String itfName, String methodName,
			Class<?>[] parametersTypes) throws ProActiveRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BooleanWrapper isMonitoringStarted() {
		return new BooleanWrapper(started);
	}

	@Override
	public void resetMonitoring() {
		// TODO reset

	}

	@Override
	public void startMonitoring() {
		started = true;
		logger.debug("[Monitor Controll] Start ... !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		String hostComponentName = null;
		if(hostComponent != null) {
			hostComponentName = hostComponent.getComponentParameters().getControllerDescription().getName();
		}
		logger.debug("[Monitor Control] My Host component is "+ hostComponentName);
		// configure the event listener
		UniqueID aoID = hostComponent.getID();
		String runtimeURL = ProActiveRuntimeImpl.getProActiveRuntime().getURL();
		this.eventControl.setBodyToMonitor(aoID, runtimeURL, hostComponentName);
		
		// start the other components of the framework
		this.eventControl.start();
		this.logHandler.init();
	}

	@Override
	public void stopMonitoring() {
		started = false;
		//TODO: stop

	}

	@Override
	public void bindFc(String cItf, Object sItf)
			throws NoSuchInterfaceException, IllegalBindingException,
			IllegalLifeCycleException {
		if(cItf.equals("events-control-nf")) {
			eventControl = (EventControl) sItf;
			return;
		}
		if(cItf.equals("log-handler-nf")) {
			logHandler = (LogHandler) sItf;
			return;
		}
		throw new NoSuchInterfaceException("Interface "+ cItf +" non existent");
	}

	@Override
	public String[] listFc() {
		return itfs;
	}

	@Override
	public Object lookupFc(String cItf) throws NoSuchInterfaceException {
		if(cItf.equals("events-control-nf")) {
			return eventControl;
		}
		if(cItf.equals("log-handler-nf")) {;
			return logHandler;
		}
		throw new NoSuchInterfaceException("Interface "+ cItf +" non existent");
	}

	@Override
	public void unbindFc(String cItf) throws NoSuchInterfaceException,
			IllegalBindingException, IllegalLifeCycleException {
		if(cItf.equals("events-control-nf")) {
			eventControl = null;
		}
		if(cItf.equals("log-handler-nf")) {;
			logHandler = null;
		}
		throw new NoSuchInterfaceException("Interface "+ cItf +" non existent");		
	}

}
