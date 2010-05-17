/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.component.controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.NameController;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.body.tags.tag.CMTag;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ProActiveInterface;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.CallRecord;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.RequestPath;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.RequestRecord;
import org.objectweb.proactive.core.component.type.ProActiveInterfaceType;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactoryImpl;
import org.objectweb.proactive.core.jmx.naming.FactoryName;
import org.objectweb.proactive.core.jmx.notification.FutureNotificationData;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.jmx.notification.RequestNotificationData;
import org.objectweb.proactive.core.jmx.util.JMXNotificationManager;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;


public class MonitorControllerImpl extends AbstractProActiveController implements MonitorController,
        NotificationListener {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_CONTROLLERS);

    /** To connect to the (Component) mbean */
    private transient JMXNotificationManager jmxNotificationManager;

    /** Monitoring status */
    private boolean started;

    /** Log for incoming requests */
    private Map<ComponentRequestID, RequestStats> requestLog;
    
    /** Log for outgoing request */
    private Map<ComponentRequestID, CallStats> callLog;
    
    
    private Map<String, MethodStatistics> statistics;

    private Map<String, String> keysList;

    
    public MonitorControllerImpl(Component owner) {
        super(owner);
        jmxNotificationManager = JMXNotificationManager.getInstance();
    }

    @Override
	public void initController() {
    	logger.debug("Monitoring Controller init");
    	// some of these two HashMap's should be synchronized? 
    	requestLog = new HashMap<ComponentRequestID, RequestStats>();
    	callLog = new HashMap<ComponentRequestID, CallStats>();
	}



	protected void setControllerItfType() {
        try {
            setItfType(ProActiveTypeFactoryImpl.instance().createFcItfType(Constants.MONITOR_CONTROLLER,
                    MonitorController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                    TypeFactory.SINGLE));
        } catch (InstantiationException e) {
            throw new ProActiveRuntimeException("cannot create controller " + this.getClass().getName(), e);
        }
    }

    private void registerMethods() {
        PAActiveObject.setImmediateService("getStatistics", new Class[] { String.class, String.class });
        PAActiveObject.setImmediateService("getStatistics", new Class[] { String.class, String.class,
                (new Class<?>[] {}).getClass() });
        PAActiveObject.setImmediateService("getAllStatistics");

        statistics = Collections.synchronizedMap(new HashMap<String, MethodStatistics>());
        keysList = new HashMap<String, String>();
        NameController nc = null;
        try {
            nc = (NameController) owner.getFcInterface(Constants.NAME_CONTROLLER);
        } catch (NoSuchInterfaceException e) {
            e.printStackTrace();
        }
        String name = nc.getFcName();
        Object[] itfs = owner.getFcInterfaces();
        for (int i = 0; i < itfs.length; i++) {
            Interface itf = (Interface) itfs[i];
            InterfaceType itfType = (InterfaceType) itf.getFcItfType();
            try {
                if (!Utils.isControllerInterfaceName(itf.getFcItfName()) && (!itfType.isFcClientItf())) {
                    List<MonitorController> subcomponentMonitors = new ArrayList<MonitorController>();
                    if (isComposite()) {
                        Iterator<Component> bindedComponentsIterator = null;
                        if (!((ProActiveInterfaceType) itfType).isFcMulticastItf()) {
                            List<Component> bindedComponent = new ArrayList<Component>();
                            bindedComponent.add(((ProActiveInterface) ((ProActiveInterface) itf)
                                    .getFcItfImpl()).getFcItfOwner());
                            bindedComponentsIterator = bindedComponent.iterator();
                        } else {
                            try {
                                MulticastControllerImpl multicastController = (MulticastControllerImpl) ((ProActiveInterface) owner
                                        .getFcInterface(Constants.MULTICAST_CONTROLLER)).getFcItfImpl();
                                Iterator<ProActiveInterface> delegatee = multicastController.getDelegatee(
                                        itf.getFcItfName()).iterator();
                                List<Component> bindedComponents = new ArrayList<Component>();
                                while (delegatee.hasNext()) {
                                    bindedComponents.add(delegatee.next().getFcItfOwner());
                                }
                                bindedComponentsIterator = bindedComponents.iterator();
                            } catch (NoSuchInterfaceException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            while (bindedComponentsIterator.hasNext()) {
                                MonitorController monitor = (MonitorController) bindedComponentsIterator
                                        .next().getFcInterface(Constants.MONITOR_CONTROLLER);
                                monitor.startMonitoring();
                                subcomponentMonitors.add(monitor);
                            }
                        } catch (NoSuchInterfaceException e) {
                            e.printStackTrace();
                        }

                    }
                    Class<?> klass = ClassLoader.getSystemClassLoader()
                            .loadClass(itfType.getFcItfSignature());
                    Method[] methods = klass.getDeclaredMethods();
                    for (Method m : methods) {
                        Class<?>[] parametersTypes = m.getParameterTypes();
                        String key = MonitorControllerHelper.generateKey(itf.getFcItfName(), m.getName(),
                                parametersTypes);
                        keysList.put(m.getName(), key);
                        if (subcomponentMonitors.isEmpty()) {
                            statistics.put(key, new MethodStatisticsPrimitiveImpl(itf.getFcItfName(), m
                                    .getName(), parametersTypes));
                        } else {
                            statistics.put(key, new MethodStatisticsCompositeImpl(itf.getFcItfName(), m
                                    .getName(), parametersTypes, subcomponentMonitors));
                        }
                        logger.debug(m.getName() + " (server) added to monitoring on component " + name +
                            "!!!");
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new ProActiveRuntimeException("The interface " + itfType + "cannot be found", e);
            }
        }
    }

    public BooleanWrapper isMonitoringStarted() {
        return new BooleanWrapper(started);
    }

    private void initMethodStatistics() {
        String[] keys = statistics.keySet().toArray(new String[] {});
        for (int i = 0; i < keys.length; i++) {
            ((MethodStatisticsAbstract) statistics.get(keys[i])).reset();
        }
    }

    public void startMonitoring() {
    	
    	boolean run = true;
    	if(!run) {
    		return;
    	}
    	
    	// experiment to try to get a reference on the connected components
    	// not necessary anymore
    	/*
    	NameController nc = null;
        try {
            nc = (NameController) owner.getFcInterface(Constants.NAME_CONTROLLER);
        } catch (NoSuchInterfaceException e) {
            e.printStackTrace();
        }
        String name = nc.getFcName();
        String itfs[] = null;
        BindingController bc = null;
        try {
        	bc = Fractal.getBindingController(owner);
		} catch (NoSuchInterfaceException e1) {
			System.out.println("Component "+ name + " doesn't have a BindingController interface");
		}
		Interface itf = null;
		ProActiveInterface paitf = null;
		ProActiveComponentRepresentative pacr = null;
		if(bc != null) {
			itfs = bc.listFc();
			for(int i=0; i<itfs.length; i++) {
				try {
					itf = (Interface) bc.lookupFc(itfs[i]);
				} catch (NoSuchInterfaceException e) {
					e.printStackTrace();
				}
				paitf = (ProActiveInterface) itf;
				pacr = (ProActiveComponentRepresentative) paitf.getFcItfOwner();
				String destName = pacr.getComponentParameters().getName();
				System.out.println("["+name+"]-->["+destName+"."+paitf.getFcItfName()+"] ");
			}
		}*/

    	if (statistics == null) {
//            registerMethods();
        }
    	
        if (!started) {
        	logger.debug("Starting Monitoring");
//            initMethodStatistics();
            try {
            	// subscribes this class as a listener to JMX notifications from the BodyWrapperMBean
            	// from the AO implementing this Component
                jmxNotificationManager.subscribe(
                		FactoryName.createActiveObjectName(PAActiveObject.getBodyOnThis().getID()),
                		this,
                		FactoryName.getCompleteUrl(ProActiveRuntimeImpl.getProActiveRuntime().getURL()));
            } catch (IOException e) {
                throw new ProActiveRuntimeException("JMX subscription for the MonitorController has failed", e);
            }
            started = true;
            String componentName="";
            try {
            	componentName = Fractal.getNameController(owner).getFcName();
    		} catch (NoSuchInterfaceException e) {
    			e.printStackTrace();
    		}
            logger.debug("["+ componentName +"] Monitoring Started");
        }
    }

    public void stopMonitoring() {
        if (started) {
            jmxNotificationManager.unsubscribe(FactoryName.createActiveObjectName(PAActiveObject
                    .getBodyOnThis().getID()), this);
            started = false;
        }
        displayCallLog();
        displayRequestLog();
    }

    public void resetMonitoring() {
        stopMonitoring();
        startMonitoring();
    }

    public MethodStatistics getStatistics(String itfName, String methodName) throws ProActiveRuntimeException {
        return getStatistics(itfName, methodName, new Class<?>[] {});
    }

    public MethodStatistics getStatistics(String itfName, String methodName, Class<?>[] parametersTypes)
            throws ProActiveRuntimeException {
        String supposedCorrespondingKey = MonitorControllerHelper.generateKey(itfName, methodName,
                parametersTypes);
        MethodStatistics methodStats = statistics.get(supposedCorrespondingKey);
        if (methodStats != null) {
            return methodStats;
        } else if (parametersTypes.length == 0) {
            String correspondingKey = null;
            String[] keys = statistics.keySet().toArray(new String[] {});
            for (int i = 0; i < keys.length; i++) {
                if (keys[i].startsWith(supposedCorrespondingKey)) {
                    if (correspondingKey == null) {
                        correspondingKey = keys[i];
                    } else {
                        throw new ProActiveRuntimeException("The method name: " + methodName +
                            " of the interface " + itfName + " is ambiguous: more than 1 method found");
                    }
                }
            }
            if (correspondingKey != null) {
                return statistics.get(correspondingKey);
            } else {
                throw new ProActiveRuntimeException("The method: " + methodName + "() of the interface " +
                    itfName + " cannot be found so no statistics are available");
            }
        } else {
            String msg = "The method: " + methodName + "(";
            for (int i = 0; i < parametersTypes.length; i++) {
                msg += parametersTypes[i].getName();
                if (i + 1 < parametersTypes.length) {
                    msg += ", ";
                }
            }
            msg += ") of the interface " + itfName + " cannot be found so no statistics are available";
            throw new ProActiveRuntimeException(msg);
        }
    }

    public Map<String, MethodStatistics> getAllStatistics() {
        return statistics;
    }

    public void handleNotification(Notification notification, Object handback) {
    	
        String type = notification.getType();
        String componentName = "";
        try {
        	componentName = Fractal.getNameController(owner).getFcName();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
        
        if (type.equals(NotificationType.requestReceived)) {
            RequestNotificationData data = (RequestNotificationData) notification.getUserData();
            logger.debug(
            		" Timestamp: " + notification.getTimeStamp() +
            		"["+componentName+"][requestRecv] " + //"From:" + data.getSource() +
            		//" To:"+ data.getDestination() +
            		" Method:" + data.getMethodName() +
            		" SeqNumber: " + data.getSequenceNumber() +
            		" NotifSeqNbr: " + notification.getSequenceNumber() +
            		" Tags: " + data.getTags());
            processRequestReceived(notification);
//            String key = keysList.get(data.getMethodName());
//            if (key != null) {
//                ((MethodStatisticsAbstract) statistics.get(key)).notifyArrivalOfRequest(notification
//                        .getTimeStamp());
//            }
        } 
        else if (type.equals(NotificationType.servingStarted)) {
            RequestNotificationData data = (RequestNotificationData) notification.getUserData();
 /*           logger.debug("["+componentName+"][servingStar] " + //From:" + data.getSource() +
            		//" To:"+ data.getDestination() +
            		" Method:" + data.getMethodName() +
            		" SeqNumber: " + data.getSequenceNumber() +
            		" Timestamp: " + notification.getTimeStamp() +
            		" NotifSeqNbr: " + notification.getSequenceNumber() +
            		" Tags: " + data.getTags());*/
            processServingStarted(notification);
            
//            String key = keysList.get(data.getMethodName());
//            if (key != null) {
//                ((MethodStatisticsAbstract) statistics.get(key)).notifyDepartureOfRequest(notification
//                        .getTimeStamp());
//            }
        } 
        else if (type.equals(NotificationType.replySent)) {
            RequestNotificationData data = (RequestNotificationData) notification.getUserData();
            logger.debug(
            		" Timestamp: " + notification.getTimeStamp() +
            		"["+componentName+"][replySent  ] " + //From:" + data.getSource() +
            		//" To:"+ data.getDestination() +
            		" Method:" + data.getMethodName() +
            		" SeqNumber: " + data.getSequenceNumber() +
            		" NotifSeqNbr: " + notification.getSequenceNumber() +
            		" Tags: " + data.getTags());
            processReplySent(notification);
            
//            String key = keysList.get(data.getMethodName());
//            if (key != null) {
//                ((MethodStatisticsAbstract) statistics.get(key)).notifyReplyOfRequestSent(notification
//                        .getTimeStamp());
//            }
        } 
        else if (type.equals(NotificationType.voidRequestServed)) {
            RequestNotificationData data = (RequestNotificationData) notification.getUserData();
//            logger.debug("["+componentName+"][voidReqServ] From:" + data.getSource() +
//            		" To:"+ data.getDestination() +
//            		" Method:" + data.getMethodName() +
//            		" SeqNumber: " + data.getSequenceNumber() +
//            		" Timestamp: " + notification.getTimeStamp() +
//            		" NotifSeqNbr: " + notification.getSequenceNumber() +
//            		" Tags: " + data.getTags());
            
//            String key = keysList.get(data.getMethodName());
//            if (key != null) {
//                ((MethodStatisticsAbstract) statistics.get(key)).notifyReplyOfRequestSent(notification
//                        .getTimeStamp());
//            }
        }
        else if (type.equals(NotificationType.requestSent)) {
            RequestNotificationData data = (RequestNotificationData) notification.getUserData();
            logger.debug(
            		" Timestamp: " + notification.getTimeStamp() +
            		"["+componentName+"][requestSent] " + //"From:" + data.getSource() +
//            		" To:"+ data.getDestination() +
            		" Method:" + data.getMethodName() +
            		" SeqNumber: " + data.getSequenceNumber() +
            		" NotifSeqNbr: " + notification.getSequenceNumber() +
            		" Tags: " + data.getTags());
            
            processRequestSent(notification);
//            String key = keysList.get(data.getMethodName());
//            if (key != null) {
//                ((MethodStatisticsAbstract) statistics.get(key)).notifyReplyOfRequestSent(notification
//                        .getTimeStamp());
//            }
        } 
        else if (type.equals(NotificationType.replyReceived)) {
            RequestNotificationData data = (RequestNotificationData) notification.getUserData();
            logger.debug(
            		" Timestamp: " + notification.getTimeStamp() +
            		"["+componentName+"][replyRecv  ] " + //From:" + data.getSource() +
//            		" To:"+ data.getDestination() +
            		" Method:" + data.getMethodName() +
            		" SeqNumber: " + data.getSequenceNumber() +
            		" NotifSeqNbr: " + notification.getSequenceNumber() +
            		" Tags: " + data.getTags());
            
//            String key = keysList.get(data.getMethodName());
//            if (key != null) {
//                ((MethodStatisticsAbstract) statistics.get(key)).notifyReplyOfRequestSent(notification
//                        .getTimeStamp());
//            }
        } 
        else if(type.equals(NotificationType.receivedFutureResult)) {
        	FutureNotificationData data = (FutureNotificationData) notification.getUserData();
        	logger.debug(
        			" Timestamp: " + notification.getTimeStamp() +
            		"["+componentName+"][futureUpdt ] " + //From:" + data.getSource() +
//            		" To:"+ data.getDestination() +
//            		" Method:" + data.getMethodName() +
//            		" SeqNumber: " + data.getSequenceNumber() +
//            		" NotifSeqNbr: " + notification.getSequenceNumber() +
//            		" Tags: " + data.getTags()
            		" Creator" + data.getCreatorID() +
            		" Waiter" + data.getBodyID()
            		);
        }
        else if(type.equals(NotificationType.realReplyReceived)) {
        	RequestNotificationData data = (RequestNotificationData) notification.getUserData();
            logger.debug(
            		" Timestamp: " + notification.getTimeStamp() +
            		"["+componentName+"][RRreplyRecv] " + //From:" + data.getSource() +
//            		" To:"+ data.getDestination() +
            		" Method:" + data.getMethodName() +
            		" SeqNumber: " + data.getSequenceNumber() +
            		" NotifSeqNbr: " + notification.getSequenceNumber() +
            		" Tags: " + data.getTags());
            
            processRealReplyReceived(notification);
//            String key = keysList.get(data.getMethodName());
//            if (key != null) {
//                ((MethodStatisticsAbstract) statistics.get(key)).notifyReplyOfRequestSent(notification
//                        .getTimeStamp());
//            }
        }
        else if(type.equals(NotificationType.requestWbN)) {
        	RequestNotificationData data = (RequestNotificationData) notification.getUserData();
        	logger.debug(
        			" Timestamp: " + notification.getTimeStamp() +
        			"["+componentName+"][WaitByNeces] " + //From:" + data.getSource() +
        			//                		" To:"+ data.getDestination() +
        			" Method:" + data.getMethodName() +
        			" SeqNumber: " + data.getSequenceNumber() +
        			" NotifSeqNbr: " + notification.getSequenceNumber() +
        			" Tags: " + data.getTags());
        	processWaitByNecessity(notification);
        	
        }
        else if (type.equals(NotificationType.setOfNotifications)) {
            @SuppressWarnings("unchecked")
            ConcurrentLinkedQueue<Notification> notificationsList = (ConcurrentLinkedQueue<Notification>) notification
                    .getUserData();
            for (Iterator<Notification> iterator = notificationsList.iterator(); iterator.hasNext();) {
                handleNotification(iterator.next(), handback);
            }
        }
    }
    
    
    private void processServingStarted(Notification notification) {
    	RequestNotificationData data = (RequestNotificationData) notification.getUserData();
    	String cmTag = extractCMTag(data);
    	// the request may not have a CMTag, when the request was invoked directly on the component
    	// (not from another component).
    	// In that case no "requestReceived" notification was generated either
    	if(cmTag == null) {
    		return;
    	}
    	String[] cmTagFields = cmTag.split("::");
    	ComponentRequestID parent = new ComponentRequestID(Long.parseLong(cmTagFields[0]));
    	ComponentRequestID current = new ComponentRequestID(Long.parseLong(cmTagFields[1]));
    	String sourceName = cmTagFields[2];
    	String destName = cmTagFields[3];
    	String interfaceName = cmTagFields[4];
    	String methodName = cmTagFields[5];
    	RequestStats rs;
    	// checks if the request data has already been entered in the map
    	if(requestLog.containsKey(current)) {
    		rs = requestLog.get(current);
    		rs.setServingStartTime(notification.getTimeStamp());
    		// updates the value
    		requestLog.put(current, rs);
    	}
    	// else, the data should be added (without the arrival time), and be added later,
    	// when the corresponding requestReceived notification be processed
    	else {
    		rs = new RequestStats(sourceName, destName, interfaceName, methodName, 0);
    		rs.setServingStartTime(notification.getTimeStamp());
    		requestLog.put(current, rs);
    	}
    }
    
    /**
     * Process the requestReceived notification.
     * Stores the request data in the requestLog.
     * TODO It's possible that some other notifications (servingStarted, replySent,...) be received before
     *      this one. In that case, when this entry is added to the requestLog, the other ones must be considered
     * @param notification
     */
    private void processRequestReceived(Notification notification) {
    	RequestNotificationData data = (RequestNotificationData) notification.getUserData();
    	String cmTag = extractCMTag(data);
    	if(cmTag == null) {
    		return;
    	}
    	String[] cmTagFields = cmTag.split("::");
    	ComponentRequestID parent = new ComponentRequestID(Long.parseLong(cmTagFields[0]));
    	ComponentRequestID current = new ComponentRequestID(Long.parseLong(cmTagFields[1]));
    	String sourceName = cmTagFields[2];
    	String destName = cmTagFields[3];
    	String interfaceName = cmTagFields[4];
    	String methodName = cmTagFields[5];
    	if(interfaceName.equals("-")) {
    		return;
    	}
    	RequestStats rs;
    	// checks if the request data has already been entered in the map
    	if(requestLog.containsKey(current)) {
    		// if the key was already there, it has to modify it to add the arrival time
    		rs = requestLog.get(current);
    		rs.setArrivalTime(notification.getTimeStamp());
    		requestLog.put(current, rs);
    	}
    	else {
    		// if there was no key, then it has to insert a new one
    		rs = new RequestStats(sourceName, destName, interfaceName, methodName, notification.getTimeStamp());
    		requestLog.put(current, rs);
    	}
    }
    
    /**
     * Process the requestSent notification.
     * Stores the new call in the callLog.
     * TODO It's possible that some other notifications (replyReceived, ...) be received before
     *      this one. In that case, when this entry is added to the requestLog, the other ones must be considered
     * @param notification
     */
    private void processRequestSent(Notification notification) {
    	// adds the request to callLog
    	// this should be the first notification regarding this request, anyway the order is not guaranteed,
    	// so care must be taken when procesing the corresponding "replyReceived"
    	RequestNotificationData data = (RequestNotificationData) notification.getUserData();
    	// TODO: Move this processing to the CMTag part
    	String cmTag = extractCMTag(data);
    	String[] cmTagFields = cmTag.split("::");
    	ComponentRequestID parent = new ComponentRequestID(Long.parseLong(cmTagFields[0]));
    	ComponentRequestID current = new ComponentRequestID(Long.parseLong(cmTagFields[1]));
    	String destComponentName = cmTagFields[3];
    	String interfaceName = cmTagFields[4];
    	String methodName = cmTagFields[5];
    	if(interfaceName.equals("-")) {
    		return;
    	}
    	CallStats cs = new CallStats(parent, destComponentName, interfaceName, methodName, notification.getTimeStamp(), false);
    	callLog.put(current, cs);
    }
    
    /**
     * Process the replySent notification.
     * This notification is generated when a reply is sent to the caller component as an answer to a non-void request 
     * @param notification
     */
    private void processReplySent(Notification notification) {
    	// modifies the request in the requestLog, to add the time at which the reply was sent
    	RequestNotificationData data = (RequestNotificationData) notification.getUserData();
    	String cmTag = extractCMTag(data);
    	// the request may not have a CMTag, when the request was invoked directly on the component
    	// (not from another component).
    	// In that case no "requestReceived" notification was generated either
    	if(cmTag == null) {
    		return;
    	}
    	String[] cmTagFields = cmTag.split("::");
    	ComponentRequestID parent = new ComponentRequestID(Long.parseLong(cmTagFields[0]));
    	ComponentRequestID current = new ComponentRequestID(Long.parseLong(cmTagFields[1]));
    	String sourceName = cmTagFields[2];
    	String destName = cmTagFields[3];
    	String interfaceName = cmTagFields[4];
    	String methodName = cmTagFields[5];
    	RequestStats rs;
    	// checks if the request data has already been entered in the map
    	if(requestLog.containsKey(current)) {
    		rs = requestLog.get(current);
    		rs.setReplyTime(notification.getTimeStamp());
    		// updates the value
    		requestLog.put(current, rs);
    	}
    	// else, the data should be added (without the arrival time), and the arrival time added later,
    	// when the corresponding requestReceived notification be processed
    	else {
    		rs = new RequestStats(sourceName, destName, interfaceName, methodName, 0);
    		rs.setReplyTime(notification.getTimeStamp());
    		requestLog.put(current, rs);
    	}
    }
    
    private void processRealReplyReceived(Notification notification) {
    	// modifies the request in the callLog, to add the time at which the final reply was received
    	RequestNotificationData data = (RequestNotificationData) notification.getUserData();
    	// TODO: Move this processing to the CMTag part
    	String cmTag = extractCMTag(data);
    	String[] cmTagFields = cmTag.split("::");
    	ComponentRequestID parent = new ComponentRequestID(Long.parseLong(cmTagFields[0]));
    	ComponentRequestID current = new ComponentRequestID(Long.parseLong(cmTagFields[1]));
    	String destComponentName = cmTagFields[3];
    	String interfaceName = cmTagFields[4];
    	String methodName = cmTagFields[5];
    	if(interfaceName.equals("-")) {
    		return;
    	}
    	CallStats cs;
    	// checks if the call data has already been entered in the map
    	if(callLog.containsKey(current)) {
    		cs = callLog.get(current);
    		cs.setReplyReceptionTime(notification.getTimeStamp());
    		// updates the value
    		callLog.put(current, cs);
    	}
    	else {
    		// the data should be added without the sentTime, which should be added when the notification for RequestSent arrives (later)
    		cs = new CallStats(parent, destComponentName, interfaceName, methodName, 0, false);
    		cs.setReplyReceptionTime(notification.getTimeStamp());
    		callLog.put(current, cs);
    	}
    }

    private void processWaitByNecessity(Notification notification) {
    	// modifies the request in the callLog, to add the time at which the WbN happened
    	RequestNotificationData data = (RequestNotificationData) notification.getUserData();
    	// TODO: Move this processing to the CMTag part
    	String cmTag = extractCMTag(data);
    	String[] cmTagFields = cmTag.split("::");
    	ComponentRequestID parent = new ComponentRequestID(Long.parseLong(cmTagFields[0]));
    	ComponentRequestID current = new ComponentRequestID(Long.parseLong(cmTagFields[1]));
    	String destComponentName = cmTagFields[3];
    	String interfaceName = cmTagFields[4];
    	String methodName = cmTagFields[5];
    	if(interfaceName.equals("-")) {
    		return;
    	}
    	CallStats cs;
    	// checks if the call data has already been entered in the map
    	if(callLog.containsKey(current)) {
    		cs = callLog.get(current);
    		cs.setWbnStartTime(notification.getTimeStamp());
    		// updates the value
    		callLog.put(current, cs);
    	}
    	else {
    		// the data should be added without the sentTime, which should be added when the notification for RequestSent arrives (later)
    		cs = new CallStats(parent, destComponentName, interfaceName, methodName, 0, false);
    		cs.setWbnStartTime(notification.getTimeStamp());
    		callLog.put(current, cs);
    	}
    }

    
    /**
     * Extracts the CMTag from the complete Tag string (which can include several tags)
     * @param data
     * @return
     */
    
    private String extractCMTag(RequestNotificationData data) {
    	String tagString = data.getTags();
    	// The ? is a "reluctant" quantifier, to make the .* to match the smallest possible string.
    	Pattern pattern = Pattern.compile("\\[TAG\\](.*?)\\[DATA\\](.*?)\\[END\\]");
    	Pattern inner = Pattern.compile("\\[(.*?)\\]");
    	Matcher match = pattern.matcher(tagString);
    	String currentTag;
    	String currentFields[];
    	while(match.find()) {
    		currentTag = match.group();
    		currentFields = inner.split(currentTag);
    		if(currentFields[1].equals(CMTag.IDENTIFIER)) {
    			// This is the CMTag. Process it
    			return currentFields[2];
    			// And this shouldn't be necessary!!!... the CMTag should have a constructor that receives a string
    			//return new CMTag(null, Long.parseLong(tagFields[0]), Long.parseLong(tagFields[1]), tagFields[2], tagFields[3], tagFields[4], tagFields[5]);
    		}
    	}
    	
    	return null;
    }
    
    public void displayCallLog() {
        String componentName = "";
        try {
        	componentName = Fractal.getNameController(owner).getFcName();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
    	Iterator<ComponentRequestID> i = callLog.keySet().iterator();
    	ComponentRequestID crID;
    	CallStats cs;
    	while(i.hasNext()) {
    		crID = i.next();
    		cs = callLog.get(crID);
    		System.out.println("[callLog:"+componentName+"] Parent: "+ cs.getParentID() + " Current: "+ crID + 
    				" Call: "+ cs.getCalledComponent() + "." + cs.getInterfaceName()+"."+cs.getMethodName()+ 
    				" SentTime: " + cs.getSentTime() + " RealReplyReceivedTime: " + cs.getReplyReceptionTime() +
    				" WbN: " + (cs.getWbnStartTime()==0 ? 0 : (cs.getReplyReceptionTime() - cs.getWbnStartTime())) + 
    				" SRV: " + (cs.getReplyReceptionTime() - cs.getSentTime()));
    		
    	}
    }

    public void displayRequestLog() {
        String componentName = "";
        try {
        	componentName = Fractal.getNameController(owner).getFcName();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
    	Iterator<ComponentRequestID> i = requestLog.keySet().iterator();
    	ComponentRequestID crID;
    	RequestStats rs;
    	while(i.hasNext()) {
    		crID = i.next();
    		rs = requestLog.get(crID);
    		System.out.println("[reqsLog:"+componentName+"] ID: "+ crID + " Sender: "+ rs.getCallerComponent() +
    				" Call: "+ rs.getCalledComponent() + "." + rs.getInterfaceName()+"."+rs.getMethodName() +
    				" Arr: " + rs.getArrivalTime() + " Serv: " + rs.getServingStartTime() + " Repl: " + rs.getReplyTime() +
    				" WQ: " + (rs.getServingStartTime()-rs.getArrivalTime()) + 
    				" SRV: " + (rs.getReplyTime()-rs.getServingStartTime()) +
    				" TOT: "+ (rs.getReplyTime() - rs.getArrivalTime()));
    		
    	}
    }
    
    /*
     * ---------- PRIVATE METHODS FOR SERIALIZATION ----------
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        jmxNotificationManager = JMXNotificationManager.getInstance();
    }

    /** Monitoring Framework Extension */
	@Override
	public List<org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID> getListOfRequestIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestPath getPathForID(org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestPath getPathStatisticsForId(org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID, CallRecord> getCallLog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID, RequestRecord> getRequestLog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getNotificationsReceived() {
		// TODO Auto-generated method stub
		return null;
	}
}
