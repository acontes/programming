package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.component.componentcontroller.AbstractProActiveComponentController;
import org.objectweb.proactive.core.component.controller.MethodStatistics;
import org.objectweb.proactive.core.component.controller.MonitorController;
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
	
	private EventControl eventControl = null;
	private LogHandler logHandler = null;
	private MonitorController externalMonitor = null;
	private String itfs[] = {"events-control-nf", "log-handler-nf", "external-monitoring-api-nf"};

	
	/** Monitoring status */
    private boolean started = false;
	
    public MonitorControl() {
    	super();
    }
    
	@Override
	public Map<String, MethodStatistics> getAllStatistics() {
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
		this.logHandler.reset();
		this.eventControl.reset();
	}

	@Override
	public void startMonitoring() {
		started = true;
		logger.debug("[Monitor Control] Start ... ");
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
	
	// TODO
	public List<ComponentRequestID> getListOfRequestIDs() {
		return logHandler.getListOfRequestIDs();
	}

	// TODO
    public RequestPath getPathForID(ComponentRequestID id) {
    	RequestPath result;
    	// end of the path
    	if(externalMonitor == null) {
    		result = new RequestPath();    		
    	}
    	else {
    		result = externalMonitor.getPathForID(id);
    	}
    	String name = this.hostComponent.getComponentParameters().getName();
		result.add(new PathItem(name,id.toString(),name));
		return result;
    }
    
	// TODO
    public RequestPath getPathStatisticsForId(ComponentRequestID id) {
    	return null;
    }

    public List<String> getNotificationsReceived() {
    	return eventControl.getNotifications();
    }
	
	
	//----------------------------------------------------------------------------------------
	// BindingController interface
	//
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
		if(cItf.equals("external-monitoring-api-nf")) {
			externalMonitor = (MonitorController) sItf;
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
		if(cItf.equals("log-handler-nf")) {
			return logHandler;
		}
		if(cItf.equals("external-monitoring-api-nf")) {
			return externalMonitor;
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
		if(cItf.equals("external-monitoring-api-nf")) {
			externalMonitor = null;
		}
		throw new NoSuchInterfaceException("Interface "+ cItf +" non existent");		
	}

	@Override
	public Map<ComponentRequestID, CallRecord> getCallLog() {
		return logHandler.getCallLog();
	}

	@Override
	public Map<ComponentRequestID, RequestRecord> getRequestLog() {
		return logHandler.getRequestLog();
	}
	

}
