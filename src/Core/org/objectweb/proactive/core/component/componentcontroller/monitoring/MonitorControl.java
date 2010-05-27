package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private static final Logger RPlogger = ProActiveLogger.getLogger(Loggers.COMPONENTS_REQUEST_PATH);
	
	private EventControl eventControl = null;
	private LogHandler logHandler = null;
	private MonitorController externalMonitor = null;
	private MonitorController internalMonitor = null;
	private Map<String, MonitorController> externalMonitors = new HashMap<String, MonitorController>();
	private Map<String, MonitorController> internalMonitors = new HashMap<String, MonitorController>();
	
	private String itfs[] = {"events-control-nf", "log-handler-nf",
			"external-0-monitoring-api-nf", 
			"external-1-monitoring-api-nf",
			"internal-0-monitoring-api-nf",
			"internal-1-monitoring-api-nf"};
	
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
	public List<ComponentRequestID> getListOfCallIDs() {
		return logHandler.getListOfCallIDs();
	}

	/** 
     * Builds the Request path starting from request with ID id. 
     */
    public RequestPath getPathForID(ComponentRequestID id) {
    	RequestPath result;
    	CallRecord cr;
    	
    	RPlogger.debug("["+this.getMonitoredComponentName()+"] getPathFor("+id+")");
    	cr = logHandler.fetchCallRecord(id);
    	
    	ComponentRequestID rootID = cr.getRootID();
    	Set<String> visited = new HashSet<String>();
    	visited.add(this.getMonitoredComponentName());
    	
    	String destName = cr.getCalledComponent();
    	MonitorController child = null;
    	
    	RPlogger.debug("["+this.getMonitoredComponentName()+"] Record ["+id+"] "+ cr.getCalledComponent() + "." + cr.getInterfaceName() + "." + cr.getMethodName() );
    	
    	for(String monitorItfName : internalMonitors.keySet()) {
    		RPlogger.debug("["+this.getMonitoredComponentName()+"] Looking internal interface ["+monitorItfName+"]");
			if(internalMonitors.get(monitorItfName).getMonitoredComponentName().equals(destName)) {
				child = internalMonitors.get(monitorItfName);
			}
		}
		// try the external monitor controllers
		for(String monitorItfName : externalMonitors.keySet()) {
    		RPlogger.debug("["+this.getMonitoredComponentName()+"] Looking external interface ["+monitorItfName+"]");
    		if(externalMonitors.get(monitorItfName).getMonitoredComponentName().equals(destName)) {
    			RPlogger.debug("Selected!");
				child = externalMonitors.get(monitorItfName);
			}
		}
		RPlogger.debug("-------------------------------------------------------------");
		RPlogger.debug("["+this.getMonitoredComponentName()+"] getPathFor("+id+") calling " + (child==null?"NOBODY":child.getMonitoredComponentName()) );
    	result = child.getPathForID(id, rootID, visited);
    	RPlogger.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	return result;
    	
    }
    
    /** 
     * Builds the Request path starting from request with ID id. 
     */
    public RequestPath getPathForID(ComponentRequestID id, ComponentRequestID rootID, Set<String> visited) {

    	RequestPath result = new RequestPath();
    	RequestPath branch = null;
    	String localName = this.hostComponent.getComponentParameters().getName();
    	String destName;
    	// add itself to the list of visited components
    	visited.add(localName);
    	
 
    	Map<ComponentRequestID, RequestRecord> branches = null; //logHandler.getCallRecordsFromParent(rootID);
    	Map<ComponentRequestID, CallRecord> children = null; 
    	RequestRecord rr;
    	CallRecord cr;
    	PathItem pi;
    	MonitorController child = null;


    	// find, in the call log, the list of all calls that were made here, related to the same rootID
    	branches = logHandler.getRequestRecordsFromRoot(rootID);

    	RPlogger.debug("["+localName+"] Branches: "+ branches.size());



    	// call each branch that starts here and has the same rootID
    	for(ComponentRequestID crid : branches.keySet()) {
    		RPlogger.debug("["+this.getMonitoredComponentName()+"] Trying branch ["+crid+"]");
    		// get the record of the request
    		rr = logHandler.fetchRequestRecord(crid);
    		// get all the calls that were sent while serving this request
    		children = logHandler.getCallRecordsFromParent(crid);


    		// this path item represents the fact that the call arrived here
    		pi = new PathItem(rr.getCallerComponent(), rr.getCalledComponent(), rr.getInterfaceName(), rr.getMethodName());
    		// add this PathItem
    		RPlogger.debug("["+localName+"] Adding pathItem ["+ pi.toString() +"]");
    		result.add(pi);


    		// call each component to which a request was sent
    		for(ComponentRequestID childID : children.keySet()) {
    			cr = logHandler.fetchCallRecord(childID);
    			// get the name of the component to call
    			destName = cr.getCalledComponent();
    			RPlogger.debug("["+this.getMonitoredComponentName()+"] Found call to ["+childID+"], component ["+destName+"]");
    			// if it is in the visited list, don't call it
    			if(!visited.contains(destName)) {
    				// select the client interface (can be external or internal) where this component is connected
    				// try the internal monitor controllers
    				for(String monitorItfName : internalMonitors.keySet()) {
    					RPlogger.debug("["+this.getMonitoredComponentName()+"] Trying internal interface ["+monitorItfName+"]");
    					if(internalMonitors.get(monitorItfName).getMonitoredComponentName().equals(destName)) {
    						RPlogger.debug("Found!");
    						child = internalMonitors.get(monitorItfName);
    					}
    				}
    				// try the external monitor controllers
    				for(String monitorItfName : externalMonitors.keySet()) {
    					RPlogger.debug("["+this.getMonitoredComponentName()+"] Trying external interface ["+monitorItfName+"]");
    					if(externalMonitors.get(monitorItfName).getMonitoredComponentName().equals(destName)) {
    						RPlogger.debug("Found!");
    						child = externalMonitors.get(monitorItfName);
    					}
    				}

    				// and call it
    				RPlogger.debug("["+this.getMonitoredComponentName()+"] calling " + (child==null?"NOBODY":child.getMonitoredComponentName()) );
    				branch = child.getPathForID(childID, rootID, visited);

    				// add the result to the current RequestPath
    				result.add(branch);	
    			}  
    			else {
    				RPlogger.debug("["+this.getMonitoredComponentName()+"] Component ["+destName+"] already visited.");
    			}
    		}
    	}

    	RPlogger.debug("["+localName+"] Returning results with "+ result.getPath().size() +" pathItems");

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
		if(cItf.equals("external-0-monitoring-api-nf")) {
			externalMonitors.put(cItf, (MonitorController)sItf);
			return;
		}
		if(cItf.equals("external-1-monitoring-api-nf")) {
			externalMonitors.put(cItf, (MonitorController) sItf);
			return;
		}
		if(cItf.equals("internal-0-monitoring-api-nf")) {
			internalMonitors.put(cItf, (MonitorController) sItf);
			return;
		}
		if(cItf.equals("internal-1-monitoring-api-nf")) {
			internalMonitors.put(cItf, (MonitorController) sItf);
			return;
		}
		throw new NoSuchInterfaceException("Interface ["+ cItf +"] not found ... Type received: "+ sItf.getClass().getName());
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
		if(cItf.equals("external-0-monitoring-api-nf")) {
			return externalMonitors.get(cItf);
		}
		if(cItf.equals("external-1-monitoring-api-nf")) {
			return externalMonitors.get(cItf);
		}
		if(cItf.equals("internal-0-monitoring-api-nf")) {
			return internalMonitors.get(cItf);
		}
		if(cItf.equals("internal-1-monitoring-api-nf")) {
			return internalMonitors.get(cItf);
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
		if(cItf.equals("external-0-monitoring-api-nf")) {
			externalMonitors.put(cItf,null);
		}
		if(cItf.equals("external-1-monitoring-api-nf")) {
			externalMonitors.put(cItf,null);
		}
		if(cItf.equals("internal-0-monitoring-api-nf")) {
			internalMonitors.put(cItf,null);
		}
		if(cItf.equals("internal-1-monitoring-api-nf")) {
			internalMonitors.put(cItf,null);
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

	@Override
	public String getMonitoredComponentName() {
		return hostComponent.getComponentParameters().getName();
	}
	

}
