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
	
	public List<ComponentRequestID> getListOfIncomingRequestIDs() {
		return recordStore.getListOfRequestIDs();
	}
	
	public List<ComponentRequestID> getListOfOutgoingRequestIDs() {
		return recordStore.getListOfCallIDs();
	}

	/** 
     * Builds the Request path starting from request with ID id. 
     */
    public RequestPath getPathForID(ComponentRequestID id) {
    	RequestPath result = null;
    	OutgoingRequestRecord cr;
    	
    	rpLogger.debug("["+this.getMonitoredComponentName()+"] getPathFor("+id+")");
//    	if(!recordStore.exists(id, RecordType.OutgoingRequestRecord).booleanValue()) {
//    		rpLogger.debug("["+this.getMonitoredComponentName()+"] No outgoing request found here for ("+id+")");
//    		return null;
//    	}
    	cr = recordStore.fetchOutgoingRequestRecord(id);
    	
    	ComponentRequestID rootID = cr.getRootID();
    	Set<String> visited = new HashSet<String>();
    	visited.add(this.getMonitoredComponentName());
    	
    	String localName = this.getMonitoredComponentName();
    	String destName = cr.getCalledComponent();
    	MonitorControl child = null;
    	
    	rpLogger.debug("["+localName+"] Record ["+id+"] "+ cr.getCalledComponent() + "." + cr.getInterfaceName() + "." + cr.getMethodName() );
    	
    	// try the internal monitor controllers (only composites have internal monitor controllers)
    	for(String monitorItfName : internalMonitors.keySet()) {
    		rpLogger.debug("["+localName+"] Looking internal interface ["+monitorItfName+"]");
			if(internalMonitors.get(monitorItfName).getMonitoredComponentName().equals(destName)) {
				child = internalMonitors.get(monitorItfName);
			}
		}
		// try the external monitor controllers
		for(String monitorItfName : externalMonitors.keySet()) {
    		rpLogger.debug("["+localName+"] Looking external interface ["+monitorItfName+"]");
    		if(externalMonitors.get(monitorItfName).getMonitoredComponentName().equals(destName)) {
				child = externalMonitors.get(monitorItfName);
			}
		}
		// TODO Warning: Does not work (yet) if the first interface is multicast
		
		rpLogger.debug("-------------------------------------------------------------+");
		rpLogger.debug("["+this.getMonitoredComponentName()+"] getPathFor("+id+") calling " + (child==null?"NOBODY":child.getMonitoredComponentName()) );
    	
		result = child.getPathForID(id, rootID, visited);
    	
		//Need to add the new firstPathItem
		//result.add(new PathItem(cr.getParentID(), id, cr.getSentTime(), cr.getReplyReceptionTime(), cr.getReplyReceptionTime() - cr.getSentTime(), localName, destName, cr.getInterfaceName(), cr.getMethodName()));
    	
		result.getSize();
		
		result.getPath().setSendTime(cr.getSentTime());
		result.getPath().setReplyRecvTime(cr.getReplyReceptionTime());
//		mainPath.setSendTime(cr.getSentTime());
//		mainPath.setReplyRecvTime(cr.getReplyReceptionTime());
//		mainPath.addChildID(result.getPath().getID());
//		mainPath.addChild(result.getPath().getID(), result.getPath());
		
    	//result.getSize();
    	
    	rpLogger.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    	return result;
    	
    }
    
    /** 
     * Builds the Request path starting from request with ID id. 
     */
    public RequestPath getPathForID(ComponentRequestID incomingID, ComponentRequestID rootID, Set<String> visited) {

    	// add this component to the set of visited components
    	String localName = this.hostComponent.getComponentParameters().getName();
    	visited.add(localName);
    	rpLogger.debug("["+localName+"] Visiting, for ID ["+ incomingID + "], rootID ["+rootID+"]");
    	
    	// Create new RequestPath object to return. The requestPath is built for the incomingID we're looking for, and with the current visited list
    	RequestPath result = new RequestPath(incomingID);
    	result.addSetVisited(visited);
    	
    	// get the time when the request 'incomingID' was received
    	IncomingRequestRecord objectiveIrr = recordStore.fetchIncomingRequestRecord(incomingID);
    	final long arrivalTimeIncomingRequest = objectiveIrr.getArrivalTime();
    	final ComponentRequestID root = rootID;
    	
    	// obtains all incomingRequestRecords with arrivalTime >= arrivalTimeIncomingRequest, and the same rootID
    	List<IncomingRequestRecord> incomingRequests = recordStore.getIncomingRequestRecords(new Condition<IncomingRequestRecord>() {
    		@Override
    		public boolean evaluate(IncomingRequestRecord rr) {
    			return rr.getArrivalTime() >= arrivalTimeIncomingRequest && rr.getRootID().equals(root);
    		}
    	});
    	rpLogger.debug("["+localName+"] Found "+ incomingRequests.size() + " incoming requests.");

    	// process each incoming request found (there must be at least one)
    	for(IncomingRequestRecord irr : incomingRequests) {
        	rpLogger.debug("["+localName+"]    Processing incomingRequest ["+ irr.getRequestID()+"]");
    		// assertion
    		if(!irr.getCalledComponent().equals(localName)) System.out.println("["+localName+"]    ERROR! Different component names!!!!");
    		// create new PathItem. If the request ID is the same as the incomingID, then it is the expected pathItem. Otherwise, it is another subtree.
    		PathItem pi = new PathItem(irr.getRequestID(), localName, irr.getInterfaceName(), irr.getMethodName());
    		// add the "server view" information
    		pi.setRecvTime(irr.getArrivalTime());
    		pi.setReplySentTime(irr.getReplyTime());

    		// obtains all outgoingRequestRecords generated from the current request
    		Map<ComponentRequestID, OutgoingRequestRecord> outgoingRequests = recordStore.getOutgoingRequestRecordsFromParent(irr.getRequestID());
    		rpLogger.debug("["+localName+"]    Found "+ outgoingRequests.size() + " outgoing requests from "+ irr.getRequestID());
    		// process each outgoing request found (there may be 0)
    		for(OutgoingRequestRecord orr : outgoingRequests.values()) {
    			
				rpLogger.debug("["+localName+"]       Processing outgoing request ID ["+ orr.getRequestID() +"]");
    			// add the ID of this outgoing requests as child of the current PathItem
    			pi.addChildID(orr.getRequestID());
    			
    			String destinationComponent = orr.getCalledComponent();
		
    			// don't call an already visited component
    			if(!visited.contains(destinationComponent)) {
    				
    				rpLogger.debug("["+localName+"]       Looking for monitor of (not visited) component ["+ destinationComponent +"]");
    				// find the appropriate monitor controller to call
        			MonitorControl mc = findMonitorControl(destinationComponent);
        			// assertion
        			if(mc == null) System.out.println("["+localName+"]       ERROR! No MonitorControl found!!!");
        			
    				rpLogger.debug("["+localName+"]       Calling external monitor on Component ["+ destinationComponent +"]");
    				// find the request path from the ID of the current outgoing request (it will return a path, and 0 or more addition subtrees)
    				RequestPath rp = mc.getPathForID(orr.getRequestID(), rootID, visited);
    				// add all the visited nodes (there may be new ones)
    				visited.addAll(rp.getVisited());
    				// get the searched path (there must be one)
    				PathItem childPath = rp.getPath();
    				// assertion
    				if(!childPath.getID().equals(orr.getRequestID())) System.out.println("["+localName+"]       ERROR! Obtained path does not begin with search request ID. Obtained path ID: "+ childPath.getID() + ", while OutgoingRequestID was "+ orr.getRequestID());
    				rpLogger.debug("["+localName+"]       Found path for request ID ["+ orr.getRequestID() +"]");

    				// complete the path with "client view" information
    				childPath.setSendTime(orr.getSentTime());
    				childPath.setReplyRecvTime(orr.getReplyReceptionTime());
    				// add the obtained path as a child of the PathItem that we are creating
    				pi.addChild(orr.getRequestID(), childPath);
    				
    				// copy all the other heads found in the obtained RequestPath (maybe 0)
    				rpLogger.debug("["+localName+"]       Obtained "+ rp.getHeads().size() +" additional subtrees");
    				for(PathItem head : rp.getHeads()) {
        				rpLogger.debug("["+localName+"]          This subtree belong to request ID ["+ head.getID() +"]");
        				result.addHead(head);
    				}
    				// copy the incomplete path entries
    				rpLogger.debug("["+localName+"]       Obtained "+ rp.getHeads().size() +" incomplete path entries");
    				for(PathItem inc : rp.getIncompletes()) {
    					rpLogger.debug("["+localName+"]          Entry for ["+ inc.getID()+"] is incomplete");
    					result.addIncomplete(inc);
    				}
    			}
    			else {
    				// the destination component has already been visited !!! Don't visit it again, but add an incomplete entry,
    				// which must be later "glued" with a subtree (head)
    				rpLogger.debug("["+localName+"]       Component ["+ destinationComponent +"] already visited. Won't call it again.");
    				// add incomplete child path
    				// Warning... the interfaceName maybe different (it seems I'm losing that info)
    				rpLogger.debug("["+localName+"]          Adding incomplete entry ["+ orr.getRequestID()+"]");
    				PathItem childIncomplete = new PathItem(orr.getRequestID(), destinationComponent, orr.getInterfaceName(), orr.getMethodName());
    				childIncomplete.setSendTime(orr.getSentTime());
    				childIncomplete.setReplyRecvTime(orr.getReplyReceptionTime());
    				result.addIncomplete(childIncomplete);
    				// the incomplete entry must also be registered as a child of the current request
    				pi.addChild(orr.getRequestID(), childIncomplete);
    			}
    			
    		}
    		// add the new created PathItem as the expected child of this RequestPath
    		if(pi.getID().equals(incomingID)) {
    			result.setPath(pi);
    		}
    		// it is another incoming request, so put it in the list of heads
    		else {
    			result.addHead(pi);
    		}
    		
    	}
    	
    	rpLogger.debug("["+localName+"] Trying to reduce subtrees");
    	// Now comes the reduction part.
    	// Check all the 'incomplete' entries, and try to find a match with a 'head' entry.
    	for(PathItem incomplete : result.getIncompletes()) {
    		PathItem subtree = result.getHead(incomplete.getID());
    		if(subtree != null) {
    			// merge 'subtree' with 'incomplete' 
    			incomplete.setRecvTime(subtree.getRecvTime());
    			incomplete.setReplySentTime(subtree.getReplySentTime());
    			incomplete.setChildrenID(subtree.getChildrenID());
    			incomplete.setChildren(subtree.getChildren());
    		}
    		// now the incomplete entry must be removed from the 'incomplete' list, and from the 'subtree' list
    		result.removeHead(incomplete.getID());
    		// I'm inside an iteration over 'incomplete' ... maybe I shouldn't remove an entry now
    			
    		// TODO remove the entry from the 'incomplete' list
    	}
    	
    	rpLogger.debug("["+localName+"] Returning results with "+ result.getHeads().size() +" additional subtrees and "+ result.getIncompletes().size() + " incomplete entries.");

    	return result;    	
    }
    
    /**
     * Look on each internal/external monitor interface to find the Monitor for the indicate name.
     * Warning!!... It may still block if I try with the MonitorControl of a "working component" (in a cycle). 
     *              I should avoid consulting the internal/external monitor for the name, and rely on a "cached" name.
     *              The name of the component does not change (normally) anyway. 
     *              The only problem is the multicast interfaces, because the names may of the bound components may change.
     *                 In that case, I'd need to do something upon each multicast binding to keep the consistency.
     * @param destName
     * @return
     */
    private MonitorControl findMonitorControl(String destName) {
    	MonitorControl child = null;
    	// select the client interface (can be external or internal) where this component is connected
		// try the internal monitor controllers
		for(String monitorItfName : internalMonitors.keySet()) {
			rpLogger.debug("["+this.getMonitoredComponentName()+"]          Trying internal interface ["+monitorItfName+"]");
			if(internalMonitors.get(monitorItfName).getMonitoredComponentName().equals(destName)) {
				rpLogger.debug("["+this.getMonitoredComponentName()+"]          Found!!");
				child = internalMonitors.get(monitorItfName);
			}
		}
		// try the external monitor controllers
		for(String monitorItfName : externalMonitors.keySet()) {
			rpLogger.debug("["+this.getMonitoredComponentName()+"]          Trying external interface ["+monitorItfName+"]");
			if(externalMonitors.get(monitorItfName).getMonitoredComponentName().equals(destName)) {
				rpLogger.debug("["+this.getMonitoredComponentName()+"]          Found!!");
				child = externalMonitors.get(monitorItfName);
			}
		}
		// try the external monitor controllers connected through multicast
		for(String monitorItfName : externalMonitorsMulticast.keySet()) {
			rpLogger.debug("["+this.getMonitoredComponentName()+"]          Trying external multicast interface ["+monitorItfName+"]");
//			rpLogger.debug("Current OutgoingRequestRecord:"+ cr.toString());
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
						rpLogger.debug("["+this.getMonitoredComponentName()+"]          Found! (in multicast "+ monitorItfName+")");
						try {
							child = (MonitorControl) destinationComponent.getFcInterface(Constants.MONITOR_CONTROLLER);
						} catch (NoSuchInterfaceException e) {
							e.printStackTrace();
						}
					}
				}
				
			}
			
		}
		return child;
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
