package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.component.componentcontroller.AbstractProActiveComponentController;
import org.objectweb.proactive.core.jmx.naming.FactoryName;
import org.objectweb.proactive.core.jmx.notification.FutureNotificationData;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.jmx.notification.RequestNotificationData;
import org.objectweb.proactive.core.jmx.util.JMXNotificationManager;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;

/**
 * Event Listener component for the Monitoring Framework
 * 
 * This NF Component subscribes to all the interesting events that are required by the monitoring activity.
 * Each time an event is received, a record is created an sent to the Log Store.
 * The specific behaviour regarding reading/updating/writing a record is dependent on the middleware.
 * This component works as a sensor of events related to the F component it belongs to.
 * 
 * This version is adapted for the GCM/ProActive case, and considers asynchronism of requests and futures.
 * 
 * @author cruz
 *
 */
public class EventListener extends AbstractProActiveComponentController implements NotificationListener, BindingController, EventControl {

	private static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_MONITORING);

	private LogHandler logStore;
	private String[] itfs = {"log-handler"};

	/** Connection to a ProActive BodyWrapperMBean (should be to a, still inexistent, ComponentWrapperMBean) */
	private JMXNotificationManager jmxNotificationManager = null;

	private UniqueID monitoredBodyID;
	private String runtimeURL;
	private String monitoredComponentName;
	
	private boolean started = false;
	
	public EventListener() {
		super();
		
	}
	
	public void init() {
		logger.debug("[EventListener] Init ...");
		jmxNotificationManager = JMXNotificationManager.getInstance();
	}
	
	// somebody must call this start (the Monitor Controller) ... it should be like an "enable"
	public void start() {
		if(!started) {
			logger.debug("[EventListener] Starting Monitoring for component ["+ monitoredComponentName + "]");
			try {
            	// subscribes this class as a listener to JMX notifications from the BodyWrapperMBean
            	// from the AO implementing this Component
                if(jmxNotificationManager == null) {
                	jmxNotificationManager = JMXNotificationManager.getInstance();
                }
				jmxNotificationManager.subscribe(
                		FactoryName.createActiveObjectName(monitoredBodyID),
                		this,
                		FactoryName.getCompleteUrl(runtimeURL));
            } catch (IOException e) {
                throw new ProActiveRuntimeException("JMX subscription for the MonitorController has failed", e);
            }
            started = true;
            logger.debug("[EventListener] Monitoring Started");
//            String componentName="";
//            try {
//            	componentName = Fractal.getNameController(owner).getFcName();
//    		} catch (NoSuchInterfaceException e) {
//    			e.printStackTrace();
//    		}
//            logger.debug("["+ componentName +"] Monitoring Started");
		
		
		
		}
	}
	
	public void setBodyToMonitor(UniqueID objectID, String runtimeURL, String componentName) {
		this.monitoredBodyID = objectID;
		this.runtimeURL = runtimeURL;
		this.monitoredComponentName = componentName;
	}

	// TODO: MAIN WORK of the collector. Highly dependent on the middleware.
	@Override
	public void handleNotification(Notification notification, Object handback) {
		String type = notification.getType();
		
		if (type.equals(NotificationType.requestReceived)) {
            RequestNotificationData data = (RequestNotificationData) notification.getUserData();
            logger.debug(
            		" Timestamp: " + notification.getTimeStamp() +
            		"["+monitoredComponentName+"][requestRecv] " + //"From:" + data.getSource() +
            		//" To:"+ data.getDestination() +
            		" Method:" + data.getMethodName() +
            		" SeqNumber: " + data.getSequenceNumber() +
            		" NotifSeqNbr: " + notification.getSequenceNumber() +
            		" Tags: " + data.getTags());
            //processRequestReceived(notification);
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
            //processServingStarted(notification);
            
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
            		"["+monitoredComponentName+"][replySent  ] " + //From:" + data.getSource() +
            		//" To:"+ data.getDestination() +
            		" Method:" + data.getMethodName() +
            		" SeqNumber: " + data.getSequenceNumber() +
            		" NotifSeqNbr: " + notification.getSequenceNumber() +
            		" Tags: " + data.getTags());
            //processReplySent(notification);
            
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
            		"["+monitoredComponentName+"][requestSent] " + //"From:" + data.getSource() +
//            		" To:"+ data.getDestination() +
            		" Method:" + data.getMethodName() +
            		" SeqNumber: " + data.getSequenceNumber() +
            		" NotifSeqNbr: " + notification.getSequenceNumber() +
            		" Tags: " + data.getTags());
            
            //processRequestSent(notification);
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
            		"["+monitoredComponentName+"][replyRecv  ] " + //From:" + data.getSource() +
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
            		"["+monitoredComponentName+"][futureUpdt ] " + //From:" + data.getSource() +
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
            		"["+monitoredComponentName+"][RRreplyRecv] " + //From:" + data.getSource() +
//            		" To:"+ data.getDestination() +
            		" Method:" + data.getMethodName() +
            		" SeqNumber: " + data.getSequenceNumber() +
            		" NotifSeqNbr: " + notification.getSequenceNumber() +
            		" Tags: " + data.getTags());
            
            //processRealReplyReceived(notification);
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
        			"["+monitoredComponentName+"][WaitByNeces] " + //From:" + data.getSource() +
        			//                		" To:"+ data.getDestination() +
        			" Method:" + data.getMethodName() +
        			" SeqNumber: " + data.getSequenceNumber() +
        			" NotifSeqNbr: " + notification.getSequenceNumber() +
        			" Tags: " + data.getTags());
        	//processWaitByNecessity(notification);
        	
        }
        else if (type.equals(NotificationType.setOfNotifications)) {
            @SuppressWarnings("unchecked")
            ConcurrentLinkedQueue<Notification> notificationsList = (ConcurrentLinkedQueue<Notification>) notification
                    .getUserData();
            for (Iterator<Notification> iterator = notificationsList.iterator(); iterator.hasNext();) {
                handleNotification(iterator.next(), handback);
            }
        }
		else {
			logger.debug("Other");
		}
		
	}

	@Override
	public void bindFc(String cItf, Object sItf)
			throws NoSuchInterfaceException, IllegalBindingException,
			IllegalLifeCycleException {
		if(cItf.equals("log-handler")) {
			logStore = (LogHandler) sItf;
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
		if(cItf.equals("log-handler")) {
			return logStore;
		}
		throw new NoSuchInterfaceException("Interface "+ cItf +" non existent");
	}

	@Override
	public void unbindFc(String cItf) throws NoSuchInterfaceException,
			IllegalBindingException, IllegalLifeCycleException {
		if(cItf.equals("log-handler")) {
			logStore = null;
		}
		throw new NoSuchInterfaceException("Interface "+ cItf +" non existent");
	}
	
	
}
