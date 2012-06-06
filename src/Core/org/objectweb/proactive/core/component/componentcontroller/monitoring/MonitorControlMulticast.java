/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.control.MethodStatistics;
import org.objectweb.proactive.core.component.type.annotations.multicast.ClassDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMode;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;

/**
 * Multicast version of MonitorControl interface.
 * The interface just aggregates the results obtained from the existent call in the MonitorControl interface.
 * 
 * @author cruz
 *
 */

@ClassDispatchMetadata(mode = @ParamDispatchMetadata(mode = ParamDispatchMode.BROADCAST))
public interface MonitorControlMulticast {

	//-------------------------------------------------------------------------------------------
	// Methods from the MonitorController interface, extended for use with Multicast
	void startGCMMonitoring();
	void stopGCMMonitoring();
	void resetGCMMonitoring();
	List<Boolean> isGCMMonitoringStarted();
	List<Map<String, Object>> getAllGCMStatistics();
	//List<MethodStatistics> getGCMStatistics(String itfName, String methodName, Class<?>[] parametersTypes) throws ProActiveRuntimeException;
	
	//-------------------------------------------------------------------------------------------
	// Adaptation for the GCM Monitoring API
	
	void startMonitoring();
	void stopMonitoring();
	void resetMonitoring();
	List<BooleanWrapper> isMonitoringStarted();
	List<Map<String, MethodStatistics>> getAllStatistics();
	List<MethodStatistics> getStatistics(String itfName, String methodName) throws ProActiveRuntimeException;
	//List<MethodStatistics> getStatistics(String itfName, String methodName, Class<?>[] parametersTypes) throws ProActiveRuntimeException;
	
    //--------------------------------------------------------------------------------------------
    // Extensions for the Monitoring Framework
    //
    
    /**
     * Get the list of all requests that have been entered/sent by this component
     * 
     */
    List<List<ComponentRequestID>> getListOfIncomingRequestIDs();
    List<List<ComponentRequestID>> getListOfOutgoingRequestIDs();
    
    /** 
     * Get the path followed by an specific request
     * 
     * @param id
     * @return
     */
    List<RequestPath> getPathForID(ComponentRequestID id);
    List<RequestPath> getPathForID(ComponentRequestID id, ComponentRequestID rootID, Set<String> visited);
    
    /**
     * Same from above, but with statistical information attached
     * 
     * @param id
     * @return
     */
    List<RequestPath> getPathStatisticsForId(ComponentRequestID id);
    
    /**
     * Get the list of entries in the Incoming Request Log
     * @return
     */
    List<Map<ComponentRequestID, IncomingRequestRecord>> getIncomingRequestLog();
    
    /**
     * Get the list of entries in the Outgoing Request Log
     * @return
     */
    List<Map<ComponentRequestID, OutgoingRequestRecord>> getOutgoingRequestLog();
    
    List<List<String>> getNotificationsReceived(); 
    
    List<String> getMonitoredComponentName();
    
    void addMetric(String name, Metric<?> metric);
    List<Object> runMetric(String name);
    //List<Object> runMetric(String name, Object[] params);
    List<Object> getMetricValue(String name);
    
    List<List<String>> getMetricList();
	
	
	
}
