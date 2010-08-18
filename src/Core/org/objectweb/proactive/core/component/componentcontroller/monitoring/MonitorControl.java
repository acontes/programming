package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.etsi.uri.gcm.api.control.MonitorController;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.control.MethodStatistics;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;

public interface MonitorControl extends MonitorController {

	//-------------------------------------------------------------------------------------------
	// Adaptation for the GCM Monitoring API
	
	void startMonitoring();
	void stopMonitoring();
	void resetMonitoring();
	BooleanWrapper isMonitoringStarted();
	public Map<String, MethodStatistics> getAllStatistics();
	public MethodStatistics getStatistics(String itfName, String methodName) throws ProActiveRuntimeException;
	public MethodStatistics getStatistics(String itfName, String methodName, Class<?>[] parametersTypes) throws ProActiveRuntimeException;
	
    //--------------------------------------------------------------------------------------------
    // Extensions for the Monitoring Framework
    //
    
    /**
     * Get the list of all requests that have been entered this component
     * 
     */
    List<ComponentRequestID> getListOfIncomingRequestIDs();
    List<ComponentRequestID> getListOfOutgoingRequestIDs();
    
    /** 
     * Get the path followed by an specific request
     * 
     * @param id
     * @return
     */
    RequestPath getPathForID(org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID id);
    RequestPath getPathForID(org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID id, org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID rootID, Set<String> visited);
    
    /**
     * Same from above, but with statistical information attached
     * 
     * @param id
     * @return
     */
    RequestPath getPathStatisticsForId(org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID id);
    
    /**
     * Get the list of entries in the Incoming Request Log
     * @return
     */
    Map<org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID, IncomingRequestRecord> getIncomingRequestLog();
    
    /**
     * Get the list of entries in the Outgoing Request Log
     * @return
     */
    Map<org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID, OutgoingRequestRecord> getOutgoingRequestLog();
    
    List<String> getNotificationsReceived(); 
    
    String getMonitoredComponentName();
    
    void addMetric(String name, Metric metric);
    Object runMetric(String name, Object[] params);
    Object getMetricValue();
    
    Set<String> getMetricList();
	
	
	
}
