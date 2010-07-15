package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.etsi.uri.gcm.api.control.MonitorController;

public interface MonitorControl extends MonitorController {

    //--------------------------------------------------------------------------------------------
    // Extensions for the Monitoring Framework
    //
    
    /**
     * Get the list of all requests that have been entered this component
     * 
     */
    List<org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID> getListOfRequestIDs();
    List<org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID> getListOfCallIDs();
    
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
     * Get the list of entries in the Request Log
     * @return
     */
    Map<org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID, RequestRecord> getRequestLog();
    
    /**
     * Get the list of entries in the Call Log
     * @return
     */
    Map<org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID, CallRecord> getCallLog();
    
    List<String> getNotificationsReceived(); 
    
    String getMonitoredComponentName();
	
	
	
}
