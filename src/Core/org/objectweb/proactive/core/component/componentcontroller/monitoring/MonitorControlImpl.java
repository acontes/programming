package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.componentcontroller.AbstractPAComponentController;
import org.objectweb.proactive.core.component.componentcontroller.remmos.Remmos;
import org.objectweb.proactive.core.component.control.MethodStatistics;
import org.objectweb.proactive.core.component.control.PAMulticastController;
import org.objectweb.proactive.core.component.identity.PAComponent;
import org.objectweb.proactive.core.component.representative.PAComponentRepresentative;
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
public class MonitorControlImpl extends AbstractPAComponentController implements MonitorControl, BindingController {

	private static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_MONITORING);
	private static final Logger rpLogger = ProActiveLogger.getLogger(Loggers.COMPONENTS_REQUEST_PATH);
	
	private EventControl eventControl = null;
	private RecordStore recordStore = null;
	private MetricsStore metricsStore = null;
	
	// interfaces for monitors of internal and external components
	private Map<String, MonitorControl> externalMonitors = new HashMap<String, MonitorControl>();
	private Map<String, MonitorControl> internalMonitors = new HashMap<String, MonitorControl>();
	private Map<String, MonitorControlMulticast> externalMonitorsMulticast = new HashMap<String, MonitorControlMulticast>();
	
	private String basicItfs[] = {
			Remmos.EVENT_CONTROL_ITF,
			Remmos.RECORD_STORE_ITF,
			Remmos.METRICS_STORE_ITF
			};
	
	/** Monitoring status */
    private boolean started = false;
	
    public MonitorControlImpl() {
    	super();
    }
    
    //--------------------------------------------------------------------------
    // Old API, kept for wrapping calls to the new one
	@Override
	public Map<String, MethodStatistics> getAllStatistics() {
		return null;
	}
	@Override
	public MethodStatistics getStatistics(String itfName, String methodName)
			throws ProActiveRuntimeException {
		return null;
	}
	/*@Override
	public MethodStatistics getStatistics(String itfName, String methodName,
			Class<?>[] parametersTypes) throws ProActiveRuntimeException {
		return null;
	}*/
	@Override
	public BooleanWrapper isMonitoringStarted() {
		return new BooleanWrapper(isGCMMonitoringStarted());
	}
	@Override
	public void resetMonitoring() {
		resetGCMMonitoring();
	}
	@Override
	public void startMonitoring() {
		startGCMMonitoring();
	}
	@Override
	public void stopMonitoring() {
		stopGCMMonitoring();
	}
    
	//-----------------------------------------------------------------------
	// GCM Monitoring API
	@Override
	public Map<String, Object> getAllGCMStatistics() {
		return null;
	}

	/*@Override
	public MethodStatistics getGCMStatistics(String itfName, String methodName,
			Class<?>[] parametersTypes) throws ProActiveRuntimeException {
		return null;
	}*/

	@Override
	public Boolean isGCMMonitoringStarted() {
		return started;
	}

	@Override
	public void resetGCMMonitoring() {
		this.recordStore.reset();
		this.eventControl.reset();
	}

	@Override
	public void startGCMMonitoring() {
		started = true;
		logger.debug("[Monitor Control] Start ... ");
		String hostComponentName = null;
		if(hostComponent != null) {
			hostComponentName = hostComponent.getComponentParameters().getControllerDescription().getName();
		}
		logger.debug("[Monitor Control] My Host component is "+ hostComponentName + "[ID: "+ hostComponent.getID()+"]");
		// configure the event listener
		UniqueID aoID = hostComponent.getID();
		String runtimeURL = ProActiveRuntimeImpl.getProActiveRuntime().getURL();
		logger.debug("[Monitor Control] RuntimeURL = "+ runtimeURL);
		this.eventControl.setBodyToMonitor(aoID, runtimeURL, hostComponentName);
		
		
		// start the other components of the framework
		this.eventControl.start();
		this.recordStore.init();
		this.metricsStore.init();
	}

	@Override
	public void stopGCMMonitoring() {
		started = false;
		//TODO: stop

	}
	
	// TODO
	public List<ComponentRequestID> getListOfIncomingRequestIDs() {
		return recordStore.getListOfRequestIDs();
	}
	
	// TODO
	public List<ComponentRequestID> getListOfOutgoingRequestIDs() {
		return recordStore.getListOfCallIDs();
	}

	/** 
     * Builds the Request path starting from request with ID id. 
     */
    public RequestPath getPathForID(ComponentRequestID id) {
    	RequestPath result;
    	OutgoingRequestRecord cr;
    	
    	rpLogger.debug("["+this.getMonitoredComponentName()+"] getPathFor("+id+")");
    	cr = recordStore.fetchOutgoingRequestRecord(id);
    	
    	ComponentRequestID rootID = cr.getRootID();
    	Set<String> visited = new HashSet<String>();
    	visited.add(this.getMonitoredComponentName());
    	
    	String localName = this.getMonitoredComponentName();
    	String destName = cr.getCalledComponent();
    	MonitorControl child = null;
    	
    	rpLogger.debug("["+this.getMonitoredComponentName()+"] Record ["+id+"] "+ cr.getCalledComponent() + "." + cr.getInterfaceName() + "." + cr.getMethodName() );
    	
    	// try the internal monitor controllers (only composites have internal monitor controllers)
    	for(String monitorItfName : internalMonitors.keySet()) {
    		rpLogger.debug("["+this.getMonitoredComponentName()+"] Looking internal interface ["+monitorItfName+"]");
			if(internalMonitors.get(monitorItfName).getMonitoredComponentName().equals(destName)) {
				child = internalMonitors.get(monitorItfName);
			}
		}
		// try the external monitor controllers
		for(String monitorItfName : externalMonitors.keySet()) {
    		rpLogger.debug("["+this.getMonitoredComponentName()+"] Looking external interface ["+monitorItfName+"]");
    		if(externalMonitors.get(monitorItfName).getMonitoredComponentName().equals(destName)) {
				child = externalMonitors.get(monitorItfName);
			}
		}
		rpLogger.debug("-------------------------------------------------------------");
		rpLogger.debug("["+this.getMonitoredComponentName()+"] getPathFor("+id+") calling " + (child==null?"NOBODY":child.getMonitoredComponentName()) );
    	result = child.getPathForID(id, rootID, visited);
    	result.add(new PathItem(cr.getParentID(), id, cr.getSentTime(), cr.getReplyReceptionTime(), cr.getReplyReceptionTime() - cr.getSentTime(), localName, destName, cr.getInterfaceName(), cr.getMethodName()));
    	
    	// sort the results according to the order of the calls
    	result.getSize();
    	
    	rpLogger.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
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
    	
 
    	Map<ComponentRequestID, IncomingRequestRecord> branches = null; //logHandler.getCallRecordsFromParent(rootID);
    	Map<ComponentRequestID, OutgoingRequestRecord> children = null; 
    	IncomingRequestRecord rr;
    	OutgoingRequestRecord cr;
    	PathItem pi;
    	MonitorControl child = null;


    	// find, in the call log, the list of all calls that were made here, related to the same rootID
    	branches = recordStore.getIncomingRequestRecordsFromRoot(rootID);

    	rpLogger.debug("["+localName+"] Branches: "+ branches.size());



    	// call each branch that starts here and has the same rootID
    	for(ComponentRequestID crid : branches.keySet()) {

    		rpLogger.debug("["+this.getMonitoredComponentName()+"] Trying branch ["+crid+"]");
    		// get the record of the request
    		rr = recordStore.fetchIncomingRequestRecord(crid);
    		// get all the calls that were sent while serving this request
    		children = recordStore.getOutgoingRequestRecordsFromParent(crid);


    		// this path item represents the fact that the call arrived here
    		pi = new PathItem(crid, rr.getRequestID(), rr.getArrivalTime(), rr.getReplyTime(), rr.getReplyTime()-rr.getArrivalTime(), rr.getCallerComponent(), rr.getCalledComponent(), rr.getInterfaceName(), rr.getMethodName());
    		// add this PathItem
    		rpLogger.debug("["+localName+"] Adding pathItem ["+ pi.toString() +"]");
    		result.add(pi);


    		// call each component to which a request was sent
    		for(ComponentRequestID childID : children.keySet()) {
    			cr = recordStore.fetchOutgoingRequestRecord(childID);
    			// get the name of the component to call
    			destName = cr.getCalledComponent();
    			rpLogger.debug("["+this.getMonitoredComponentName()+"] Found call to ["+childID+"], component ["+destName+"]");
    			
    			// add the item saying that a call was sent. However, while doing the search we won't necessarily call that component now.
    			result.add(new PathItem(cr.getParentID(), cr.getRequestID(), cr.getSentTime(), cr.getReplyReceptionTime(), cr.getReplyReceptionTime()-cr.getSentTime(), localName, destName, cr.getInterfaceName(), cr.getMethodName()));
    			
    			// if it is in the visited list, don't call it
    			if(!visited.contains(destName)) {
    				// select the client interface (can be external or internal) where this component is connected
    				// try the internal monitor controllers
    				for(String monitorItfName : internalMonitors.keySet()) {
    					rpLogger.debug("["+this.getMonitoredComponentName()+"] Trying internal interface ["+monitorItfName+"]");
    					if(internalMonitors.get(monitorItfName).getMonitoredComponentName().equals(destName)) {
    						rpLogger.debug("Found!");
    						child = internalMonitors.get(monitorItfName);
    					}
    				}
    				// try the external monitor controllers
    				for(String monitorItfName : externalMonitors.keySet()) {
    					rpLogger.debug("["+this.getMonitoredComponentName()+"] Trying external interface ["+monitorItfName+"]");
    					if(externalMonitors.get(monitorItfName).getMonitoredComponentName().equals(destName)) {
    						rpLogger.debug("Found!");
    						child = externalMonitors.get(monitorItfName);
    					}
    				}
    				// try the external monitor controllers connected through multicast
    				for(String monitorItfName : externalMonitorsMulticast.keySet()) {
    					rpLogger.debug("["+this.getMonitoredComponentName()+"] Trying external multicast interface ["+monitorItfName+"]");
    					rpLogger.debug("Current OutgoingRequestRecord:"+ cr.toString());
    					// Two options:
    					// (1) need to get all the destinations of the multicast interface, and call each component (assuming multicast are always broadcast)
    					// OR
    					// (2) call only the destination used (if we consider selective multicast, but I should have to copy it from Elton's work)
    					// so, for now it's (1)
    					// Select the bound component which has the same name as "destName"
    					// I need to check the bound component using the PAMulticastController, because the actual set of bound components can change at runtime,
    					//    whereas in the case of the singleton monitor controllers, it is only one (or zero).
    					PAMulticastController pamc = null;
    					Object[] destinationObjects = null;
    					PAComponentRepresentative destinationPAComponent = null;
    					String destinationComponentName = null;
    					try {
    						// gets all destination components (as objects) bound to this multicast itf
							pamc = Utils.getPAMulticastController(this.hostComponent);
							MonitorControlMulticast mcm = externalMonitorsMulticast.get(monitorItfName);
							rpLogger.debug("mcm is "+ mcm.getClass().getName());
							rpLogger.debug("PAInterface?"+ (mcm instanceof PAInterface));
							String externalMulticastItfName = ((PAInterface)mcm).getFcItfName();
							rpLogger.debug("mcm name: "+ externalMulticastItfName);
							destinationObjects = pamc.lookupGCMMulticast(externalMulticastItfName);
						} catch (NoSuchInterfaceException e) {
							e.printStackTrace();
						}
						// WARNING: I'm not sure it works ok with the aliasClientBinding ... but it should ...
						for(Object destinationObject : destinationObjects) {
							Component destinationComponent = ((PAInterface)destinationObject).getFcItfOwner();
							// ignore WSComponents
							if(destinationComponent instanceof PAComponentRepresentative) {
								destinationPAComponent = (PAComponentRepresentative) destinationComponent;
								destinationComponentName = destinationPAComponent.getComponentParameters().getName();
								if(destinationComponentName.equals(destName)) {
									rpLogger.debug("Found! (in multicast "+ monitorItfName+")");
									try {
										child = (MonitorControl) destinationComponent.getFcInterface(Constants.MONITOR_CONTROLLER);
									} catch (NoSuchInterfaceException e) {
										e.printStackTrace();
									}
								}
							}
							
						}
						
    				}

    				// and call it
    				rpLogger.debug("["+this.getMonitoredComponentName()+"] calling " + (child==null?"NOBODY":child.getMonitoredComponentName()) );
    				branch = child.getPathForID(childID, rootID, visited);

    				// add the result to the current RequestPath
    				result.add(branch);	
    			}  
    			else {
    				rpLogger.debug("["+this.getMonitoredComponentName()+"] Component ["+destName+"] already visited.");
    			}
    		}
    	}
    	

    	rpLogger.debug("["+localName+"] Returning results with "+ result.getPath().size() +" pathItems");

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
		if(cItf.equals(Remmos.EVENT_CONTROL_ITF)) {
			eventControl = (EventControl) sItf;
			return;
		}
		if(cItf.equals(Remmos.RECORD_STORE_ITF)) {
			recordStore = (RecordStore) sItf;
			return;
		}
		if(cItf.equals(Remmos.METRICS_STORE_ITF)) {
			metricsStore = (MetricsStore) sItf;
			return;
		}
		// it refers to the monitoring interface of an external component (bound from an external client interface)
		if(cItf.endsWith("-external-"+Remmos.MONITOR_SERVICE_ITF)) {
			// WARN: does not check if the corresponding external client interface exists in the host component
			// The server interface maybe a Multicast. In that case, it must be cast appropriately.!!!
			if(sItf instanceof MonitorControl) { 
				externalMonitors.put(cItf, (MonitorControl)sItf);
			}
			if(sItf instanceof MonitorControlMulticast) {
				//System.out.println("   bindFc. Binding ["+cItf+"] to Multicast interface");
				externalMonitorsMulticast.put(cItf, (MonitorControlMulticast) sItf);
			}
			return;
		}
		// it refers to the monitoring interface of an internal component (external server interface bound to an internal server interface)
		if(cItf.endsWith("-internal-"+Remmos.MONITOR_SERVICE_ITF)) {
			// WARN: does not check if the corresponding internal server interface exists in the host component
			internalMonitors.put(cItf, (MonitorControl) sItf);
			return;
		}
		throw new NoSuchInterfaceException("Interface ["+ cItf +"] not found ... Type received: "+ sItf.getClass().getName());
	}

	@Override
	public String[] listFc() {
		int nExternalMonitors = externalMonitors.size();
		int nInternalMonitors = internalMonitors.size();
		int nExternalMonitorsMulticast = externalMonitorsMulticast.size();
		int nBasicItfs = basicItfs.length;
		
		ArrayList<String> itfsList = new ArrayList<String>(nExternalMonitors+nInternalMonitors+nExternalMonitorsMulticast+nBasicItfs);
		for(int i=0;i<nBasicItfs;i++) {
			itfsList.add(basicItfs[i]);
		}
		itfsList.addAll(externalMonitors.keySet());
		itfsList.addAll(internalMonitors.keySet());
		
		return itfsList.toArray(new String[itfsList.size()]);
		
	}

	@Override
	public Object lookupFc(String cItf) throws NoSuchInterfaceException {
		if(cItf.equals(Remmos.EVENT_CONTROL_ITF)) {
			return eventControl;
		}
		if(cItf.equals(Remmos.RECORD_STORE_ITF)) {
			return recordStore;
		}
		if(cItf.equals(Remmos.METRICS_STORE_ITF)) {
			return metricsStore;
		}
		if(cItf.endsWith("-external-"+Remmos.MONITOR_SERVICE_ITF)) {
			//System.out.println("   Looking up ... "+ cItf);
			//the interface maybe a singleton or a multicast
			if(externalMonitors.containsKey(cItf)) {
				return externalMonitors.get(cItf);
			}
			return externalMonitorsMulticast.get(cItf);
		}
		if(cItf.endsWith("-internal-"+Remmos.MONITOR_SERVICE_ITF)) {
			return internalMonitors.get(cItf);
		}
		throw new NoSuchInterfaceException("Interface "+ cItf +" non existent");
	}

	@Override
	public void unbindFc(String cItf) throws NoSuchInterfaceException,
			IllegalBindingException, IllegalLifeCycleException {
		if(cItf.equals(Remmos.EVENT_CONTROL_ITF)) {
			eventControl = null;
		}
		if(cItf.equals(Remmos.RECORD_STORE_ITF)) {
			recordStore = null;
		}
		if(cItf.equals(Remmos.METRICS_STORE_ITF)) {
			metricsStore = null;
		}
		if(cItf.endsWith("-external-"+Remmos.MONITOR_SERVICE_ITF)) {
			if(externalMonitors.containsKey(cItf)) {
				externalMonitors.put(cItf,null);
			}
			if(externalMonitorsMulticast.containsKey(cItf)) {
				externalMonitorsMulticast.put(cItf,null);
			}
		}
		if(cItf.endsWith("-internal-"+Remmos.MONITOR_SERVICE_ITF)) {
			internalMonitors.put(cItf,null);
		}
		throw new NoSuchInterfaceException("Interface "+ cItf +" non existent");		
	}

	@Override
	public Map<ComponentRequestID, IncomingRequestRecord> getIncomingRequestLog() {
		return recordStore.getIncomingRequestRecords();
	}
	
	@Override
	public Map<ComponentRequestID, OutgoingRequestRecord> getOutgoingRequestLog() {
		return recordStore.getOutgoingRequestRecords();
	}

	@Override
	public String getMonitoredComponentName() {
		return hostComponent.getComponentParameters().getName();
	}

	@Override
	public void addMetric(String name, Metric<?> metric) {
		metricsStore.addMetric(name, metric);		
	}

	@Override
	public Object runMetric(String name) {
		return metricsStore.calculate(name);
	}
	
	/*@Override
	public Object runMetric(String name, Object[] params) {
		return metricsStore.calculate(name, params);
	}*/

	@Override
	public List<String> getMetricList() {
		return metricsStore.getMetricList();
	}

	@Override
	public Object getMetricValue(String name) {
		return metricsStore.getValue(name);
	}



}
