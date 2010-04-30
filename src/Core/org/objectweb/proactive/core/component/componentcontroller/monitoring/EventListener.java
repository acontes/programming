package org.objectweb.proactive.core.component.componentcontroller.monitoring;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.tags.tag.CMTag;
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

	private LogHandler logHandler;
	private String[] itfs = {"log-handler-nf"};

	/** Connection to a ProActive BodyWrapperMBean 
	 *  In real terms, this connection should be to a (still inexistent) ComponentWrapperMBean. */
	private JMXNotificationManager jmxNotificationManager = null;

	// JMX data for locating the MBean
	private UniqueID monitoredBodyID;
	private String runtimeURL;
	
	/** Name of the component monitored by this listener */
	private String monitoredComponentName;
	
	private boolean started = false;
	
	public EventListener() {
		super();
		
	}
	
	public void init() {
		logger.debug("[EventListener] Init ...");
		jmxNotificationManager = JMXNotificationManager.getInstance();
	}
	
	public void start() {
		if(!started) {
			logger.debug("[EventListener] Starting Monitoring for component ["+ monitoredComponentName + "]");
			try {
            	// subscribes this class as a listener to JMX notifications from the BodyWrapperMBean
            	// from the AO implementing this Component
                if(jmxNotificationManager == null) {
                	jmxNotificationManager = JMXNotificationManager.getInstance();
                }
				jmxNotificationManager.subscribe(FactoryName.createActiveObjectName(monitoredBodyID), this, FactoryName.getCompleteUrl(runtimeURL));
            } catch (IOException e) {
                throw new ProActiveRuntimeException("JMX subscription for the MonitorController has failed", e);
            }
            started = true;
            logger.debug("[EventListener] Monitoring Started for component ["+ monitoredComponentName + "]");		
		}
	}
	
	
	public void setBodyToMonitor(UniqueID objectID, String runtimeURL, String componentName) {
		this.monitoredBodyID = objectID;
		this.runtimeURL = runtimeURL;
		this.monitoredComponentName = componentName;
	}

	// MAIN WORK of the collector. Highly dependent on the middleware.
	@Override
	public void handleNotification(Notification notification, Object handback) {
		String type = notification.getType();
		
		// Handling for REQUEST RECEIVED
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
            processRequestReceived(notification);
        }
		// Handling for SERVING STARTED
		else if (type.equals(NotificationType.servingStarted)) {
            RequestNotificationData data = (RequestNotificationData) notification.getUserData();
            processServingStarted(notification);
        } 
		// Handling for REPLY SENT
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
            processReplySent(notification);
        } 
		// TODO Handling for VOID REQUEST SERVED
        else if (type.equals(NotificationType.voidRequestServed)) {
            RequestNotificationData data = (RequestNotificationData) notification.getUserData();
//            logger.debug("["+componentName+"][voidReqServ] From:" + data.getSource() +
//            		" To:"+ data.getDestination() +
//            		" Method:" + data.getMethodName() +
//            		" SeqNumber: " + data.getSequenceNumber() +
//            		" Timestamp: " + notification.getTimeStamp() +
//            		" NotifSeqNbr: " + notification.getSequenceNumber() +
//            		" Tags: " + data.getTags());
            
        }
		// Handling for REQUEST SENT
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
            processRequestSent(notification);
        } 
		// TODO Handling for REPLY RECEIVED
		// For the moment I'm more interested in the Real Reply Received notification
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
        }
		// TODO Handling for RECEIVED FUTURE RESULT
		// This only happen when the thread is doing a WbN, and receives the Future update.
		// If there is no WbN, then this does not happen
		// see ... FutureProxy.waitfor()
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
		// Handling for REAL REPLY RECEIVED
		// This happens when the reply received does not have any more Futures (all the reply data is available)
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
            processRealReplyReceived(notification);
        }
		// TODO
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
			logger.debug(" Other: ["+ type +"]");
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
    	RequestRecord rs;
    	// checks if the request data has already been entered in the map
    	if(logHandler.exists(current, RecordType.RequestRecord).booleanValue()) {
    		// if the key was already there, it has to modify it to add the arrival time
    		logger.debug("Updating RequestRecord on LogStore, component "+ this.monitoredComponentName);
    		rs = (RequestRecord) logHandler.fetch(current, RecordType.RequestRecord);
    		rs.setArrivalTime(notification.getTimeStamp());
    	}
    	else {
    		// if there was no key, then it has to insert a new one
    		//logger.debug("Creating new RequestRecord on LogStore, component "+ this.monitoredComponentName);
    		rs = new RequestRecord(current, sourceName, destName, interfaceName, methodName, notification.getTimeStamp());
    	}
    	//logger.debug("Inserting RequestRecord on LogStore, component "+ this.monitoredComponentName);
    	logHandler.insert(rs);
    }
    
	/**
     * Process the servingStarted notification.
     * Updates the request data in the requestLog.
     * TODO It's possible that some other notifications (for example, replySent) be received before
     *      this one. In that case, when this entry is added to the requestLog, the other ones must be considered
     *      Note that, normally, requestReceived SHOULD have been received before this one (but it may also happen that this one arrives before)
     * @param notification
     */
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
    	RequestRecord rs;
    	// checks if the request data has already been entered in the map (should exist already)
    	if(logHandler.exists(current, RecordType.RequestRecord).booleanValue()) {
    		//rs = (RequestRecord) logStore.fetch(current, RecordType.RequestRecord);
    		rs = logHandler.fetchRequestRecord(current);	
    		rs.setServingStartTime(notification.getTimeStamp());
    	}
    	// else, the data should be added (without the arrival time), and be updated later,
    	// when the corresponding requestReceived notification be processed
    	else {
    		rs = new RequestRecord(current, sourceName, destName, interfaceName, methodName, 0);
    		rs.setServingStartTime(notification.getTimeStamp());
    	}
    	logHandler.insert(rs);
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
    	RequestRecord rs;
    	// checks if the request data has already been entered in the map
    	if(logHandler.exists(current, RecordType.RequestRecord).booleanValue()) {
    		//rs = (RequestRecord) logStore.fetch(current, RecordType.RequestRecord);
    		rs = logHandler.fetchRequestRecord(current);
    		rs.setReplyTime(notification.getTimeStamp());
    	}
    	// else, the data should be added (without the arrival time), and the arrival time added later,
    	// when the corresponding requestReceived notification be processed
    	else {
    		rs = new RequestRecord(current, sourceName, destName, interfaceName, methodName, 0);
    		rs.setReplyTime(notification.getTimeStamp());
    	}
    	logHandler.insert(rs);
    }
    
    /**
     * Process the requestSent notification.
     * Stores the new call in the callLog.
     * TODO It's possible that some other notifications (replyReceived, realReplyReceived...) be received before
     *      this one. In that case, when this entry is added to the requestLog, the other ones must be considered
     * @param notification
     */
    private void processRequestSent(Notification notification) {
    	// adds the request to callLog
    	// this should be the first notification regarding this request, anyway the order is not guaranteed,
    	// so care must be taken when processing the corresponding "replyReceived"
    	RequestNotificationData data = (RequestNotificationData) notification.getUserData();
    	// TODO: Move this processing to the CMTag part
    	String cmTag = extractCMTag(data);
    	if(cmTag == null) {
    		return;
    	}
    	String[] cmTagFields = cmTag.split("::");
    	ComponentRequestID parent = new ComponentRequestID(Long.parseLong(cmTagFields[0]));
    	ComponentRequestID current = new ComponentRequestID(Long.parseLong(cmTagFields[1]));
    	String destComponentName = cmTagFields[3];
    	String interfaceName = cmTagFields[4];
    	String methodName = cmTagFields[5];
    	if(interfaceName.equals("-")) {
    		return;
    	}
    	CallRecord cs;
    	// checks if the call data has already been entered in the map
    	if(logHandler.exists(current, RecordType.CallRecord).booleanValue()) {
    		//cs = (CallRecord) logStore.fetch(current, RecordType.CallRecord);
    		cs = logHandler.fetchCallRecord(current);
    		cs.setSentTime(notification.getTimeStamp());
    		//logger.debug("ReplyReceptionTime set to "+ cs.getReplyReceptionTime() +" for call ["+ destComponentName +"."+ interfaceName +"."+ methodName+"] sent: "+ cs.getSentTime());
    	}
    	else {
    		// the data should be added without the sentTime, which should be added when the notification for RequestSent arrives (later)
    		//logger.debug("Creating new CallRecord on LogStore, component "+ this.monitoredComponentName + ", ID: "+ current);
    		cs = new CallRecord(current, parent, destComponentName, interfaceName, methodName, notification.getTimeStamp(), false);
    		//cs.setReplyReceptionTime(notification.getTimeStamp());
    	}
    	logHandler.insertCallRecord(cs);
    	//logHandler.insert(cs);
    	
    	//cs = new CallRecord(current, parent, destComponentName, interfaceName, methodName, notification.getTimeStamp(), false);
    	//logStore.insert(cs);
    }
    
    /**
     * Process the realReplyReceived notification.
     * Updates the call record stored in the callLog.
     * This notification is sent when the definitive answer of a reply (i.e. a reply that does not involve futures) is received,
     * allowing to determine the time that the request took to be effectively served.
     * TODO It's possible that some other notifications (replyReceived, realReplyReceived...) be received before
     *      this one. In that case, when this entry is added to the requestLog, the other ones must be considered
     * @param notification
     */
    private void processRealReplyReceived(Notification notification) {
    	// modifies the request in the callLog, to add the time at which the final reply was received
    	RequestNotificationData data = (RequestNotificationData) notification.getUserData();
    	// TODO: Move this processing to the CMTag part
    	String cmTag = extractCMTag(data);
    	if(cmTag == null) {
    		return;
    	}
    	String[] cmTagFields = cmTag.split("::");
    	ComponentRequestID parent = new ComponentRequestID(Long.parseLong(cmTagFields[0]));
    	ComponentRequestID current = new ComponentRequestID(Long.parseLong(cmTagFields[1]));
    	String destComponentName = cmTagFields[3];
    	String interfaceName = cmTagFields[4];
    	String methodName = cmTagFields[5];
    	if(interfaceName.equals("-")) {
    		return;
    	}
    	CallRecord cs;
    	// checks if the call data has already been entered in the map
    	if(logHandler.exists(current, RecordType.CallRecord).booleanValue()) {
    		//cs = (CallRecord) logStore.fetch(current, RecordType.CallRecord);
    		cs = logHandler.fetchCallRecord(current);
    		cs.setReplyReceptionTime(notification.getTimeStamp());
    		//logger.debug("ReplyReceptionTime set to "+ cs.getReplyReceptionTime() +" for call ["+ destComponentName +"."+ interfaceName +"."+ methodName+"] sent: "+ cs.getSentTime());
    	}
    	else {
    		// the data should be added without the sentTime, which should be added when the notification for RequestSent arrives (later)
    		cs = new CallRecord(current, parent, destComponentName, interfaceName, methodName, 0, false);
    		cs.setReplyReceptionTime(notification.getTimeStamp());
    		//logger.debug("ReplyReceptionTime set to "+ cs.getReplyReceptionTime() +" for call ["+ destComponentName +"."+ interfaceName +"."+ methodName+"] NEW");
    	}
    	//logStore.insert(cs);
    	logHandler.insertCallRecord(cs);
    	//logger.debug("INSERTED "+ cs.getReplyReceptionTime() + ", ID: "+ current);
    	//logger.debug("READ     "+ logStore.fetchCallRecord(current).getReplyReceptionTime() + ", ID: "+ current);
    }
    
    /**
     * Process the waitByNecessity notification.
     * Sets the time when this request (?) became blocked doing wait by necessity, so it possible to compute the WaitByNecessity time
     * @param notification
     */
    private void processWaitByNecessity(Notification notification) {
    	// modifies the request in the callLog, to add the time at which the WbN happened
    	RequestNotificationData data = (RequestNotificationData) notification.getUserData();
    	// TODO: Move this processing to the CMTag part
    	String cmTag = extractCMTag(data);
    	if(cmTag == null) {
    		return;
    	}
    	String[] cmTagFields = cmTag.split("::");
    	ComponentRequestID parent = new ComponentRequestID(Long.parseLong(cmTagFields[0]));
    	ComponentRequestID current = new ComponentRequestID(Long.parseLong(cmTagFields[1]));
    	String destComponentName = cmTagFields[3];
    	String interfaceName = cmTagFields[4];
    	String methodName = cmTagFields[5];
    	if(interfaceName.equals("-")) {
    		return;
    	}
    	CallRecord cs;
    	// checks if the call data has already been entered in the map
    	if(logHandler.exists(current, RecordType.CallRecord).booleanValue()) {
    		//cs = (CallRecord) logStore.fetch(current, RecordType.CallRecord);
    		cs = logHandler.fetchCallRecord(current);
    		cs.setWbnStartTime(notification.getTimeStamp());
    	}
    	else {
    		// the data should be added without the sentTime, which should be added when the notification for RequestSent arrives (later)
    		cs = new CallRecord(current, parent, destComponentName, interfaceName, methodName, 0, false);
    		cs.setWbnStartTime(notification.getTimeStamp());
    	}
    	//logStore.insert(cs);
    	logHandler.insertCallRecord(cs);
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

	@Override
	public void bindFc(String cItf, Object sItf)
			throws NoSuchInterfaceException, IllegalBindingException,
			IllegalLifeCycleException {
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
		if(cItf.equals("log-handler-nf")) {
			return logHandler;
		}
		throw new NoSuchInterfaceException("Interface "+ cItf +" non existent");
	}

	@Override
	public void unbindFc(String cItf) throws NoSuchInterfaceException,
			IllegalBindingException, IllegalLifeCycleException {
		if(cItf.equals("log-handler-nf")) {
			logHandler = null;
		}
		throw new NoSuchInterfaceException("Interface "+ cItf +" non existent");
	}
	
	
}
