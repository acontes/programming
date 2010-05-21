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
package org.objectweb.proactive.core.body.future;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.Constants;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.ProActiveTimeoutException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.LocalBodyStore;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.proxy.AbstractProxy;
import org.objectweb.proactive.core.body.tags.MessageTags;
import org.objectweb.proactive.core.body.tags.Tag;
import org.objectweb.proactive.core.exceptions.ExceptionHandler;
import org.objectweb.proactive.core.exceptions.ExceptionMaskLevel;
import org.objectweb.proactive.core.group.DispatchMonitor;
import org.objectweb.proactive.core.jmx.mbean.BodyWrapperMBean;
import org.objectweb.proactive.core.jmx.notification.FutureNotificationData;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.jmx.notification.RequestNotificationData;
import org.objectweb.proactive.core.mop.ConstructionOfReifiedObjectFailedException;
import org.objectweb.proactive.core.mop.ConstructorCall;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.MethodCallExecutionFailedException;
import org.objectweb.proactive.core.mop.Proxy;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.util.TimeoutAccounter;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.profiling.Profiling;
import org.objectweb.proactive.core.util.profiling.TimerWarehouse;


/**
 * This proxy class manages the semantic of future objects
 *
 * @author The ProActive Team
 * @see org.objectweb.proactive.core.mop.Proxy
 *
 */
public class FutureProxy implements Future, Proxy, java.io.Serializable {
    //
    // -- STATIC MEMBERS -----------------------------------------------
    //
    final static protected Logger logger = ProActiveLogger.getLogger(Loggers.FUTURE);
    final static protected Logger CMlogger = ProActiveLogger.getLogger(Loggers.COMPONENTS_MONITORING);

    //
    // -- PROTECTED MEMBERS -----------------------------------------------
    //

    /**
     *        The object the proxy sends calls to
     */
    protected MethodCallResult target;

    /**
     * True if this proxy has to be copied for migration or local copy.
     * If true, the serialization of this future does not register an automatic continuation.
     */
    protected transient boolean copyMode;

    /**
     * Unique ID (not a UniqueID) of the future
     */
    private FutureID id;

    /**
     * Unique ID of the sender (in case of automatic continuation).
     */
    protected UniqueID senderID;

    /**
     * To monitor this future, this body will be pinged.
     * transient to explicitly document that the serialization of
     * this attribute is custom: in case of automatic continuation,
     * it references the previous element in the chain
     */
    private transient UniversalBody updater;

    /**
     * The exception level in the stack in which this future is
     * registered
     */
    private transient ExceptionMaskLevel exceptionLevel;

    /**
     * The proxy that created this future. Set as transient to avoid
     * adding remote references when sending the future. Migration is
     * thus not supported.
     */
    private transient AbstractProxy originatingProxy;

    /**
     * The methods to call when this future is updated
     */
    private transient LocalFutureUpdateCallbacks callbacks;

    // returns future update info used during dynamic dispatch for groups
    private transient DispatchMonitor dispatchMonitor;

    //cruz
    private String methodName = null;
    private String parentMethodName = null;
    private MessageTags tags;
    private MessageTags parentTags;
    private transient boolean ignoreNotification = false;
    //--cruz
    
    //
    // -- CONSTRUCTORS -----------------------------------------------
    //

    /**
     * As this proxy does not create a reified object (as opposed to
     * BodyProxy for example), it is the noargs constructor that
     * is usually called.
     */
    public FutureProxy() throws ConstructionOfReifiedObjectFailedException {
    }

    /**
     * This constructor is provided for compatibility with other proxies.
     * More precisely, this permits proxy instantiation via the Meta.newMeta
     * method.
     */
    public FutureProxy(ConstructorCall c, Object[] p) throws ConstructionOfReifiedObjectFailedException {
        // we don't care what the arguments are
        this();
    }

    //
    // -- PUBLIC STATIC METHODS -----------------------------------------------
    //

    /**
     * Tests if the object <code>obj</code> is awaited or not. Always returns
     * <code>false</code> if <code>obj</code> is not a future object.
     */
    public static boolean isAwaited(Object obj) {
        return PAFuture.isAwaited(obj);
    }

    public synchronized static FutureProxy getFutureProxy() {
        FutureProxy result;
        try {
            result = new FutureProxy();
        } catch (ConstructionOfReifiedObjectFailedException e) {
            result = null;
        }
        return result;
    }

    //
    // -- PUBLIC METHODS -----------------------------------------------
    //
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FutureProxy) {
            return this.id.equals(((FutureProxy) obj).id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    //
    // -- Implements Future -----------------------------------------------
    //

    /**
     * Invoked by a thread of the skeleton that performed the service in order
     * to tie the result object to the proxy.
     *
     * If the execution of the call raised an exception, this exception is put
     * into an object of class InvocationTargetException and returned, just like
     * for any returned object
     */
    public synchronized void receiveReply(MethodCallResult obj) {
        if (isAvailable()) {
            throw new IllegalStateException(
                "FutureProxy receives a reply and the current target field is not null. Current target is " +
                    this.target + " while reply's target is " + obj);
        }
        if (dispatchMonitor != null) {
            dispatchMonitor.updatedResult(originatingProxy);
        }
        target = obj;
        
        
        // cruz
        String resultTypeName = "";
        if(obj.getResult() != null) {
        	resultTypeName = obj.getResult().getClass().getName();
        }
        
        
        logger.debug("[FutureProxy] receiveReply. This: ID[" + this.getID()+ "], SenderID ["+ senderID +"]. IsAwaited? "+isAwaited(obj.getResult()) + " methodName:["+ methodName +"] FutureTags "+ this.getTags() + ", FutureParentTargs "+ this.getParentTags() );
        
        if(isAwaited(target.getResult())) {       	
        	// now I can give the name of method I'm still waiting for, to the new pair stub-proxy, so he will know how to generate (later) the realReplyReceived notification
        	FutureProxy futureReceived = ((FutureProxy)((StubObject)target.getResult()).getProxy());
        	logger.debug("[FutureProxy] receiveReply. ID:" + futureReceived.getID()+ ", not realReply. Had [" + futureReceived.getMethodName() +"] Tags "+ futureReceived.getTags() + ", ParentTags "+ futureReceived.getParentTags() );
        	futureReceived.setMethodName(this.methodName);

        	// propagation of the tags to the new pair stub-proxy
        	futureReceived.setTags(this.tags);
        	futureReceived.setParentTags(this.parentTags);
        	logger.debug("[FutureProxy] receiveReply. ID:" + futureReceived.getID()+ ", not realReply. Now [" + futureReceived.getMethodName() +"] Tags "+ futureReceived.getTags() + ", ParentTags "+ futureReceived.getParentTags() );
        }
        else {
        	Body body = LocalBodyStore.getInstance().getLocalBody(senderID);        	
        	
        	// Generate the JMX notification RealReplyReceived
        	logger.debug("[FutureProxy] receiveReply. ID[" + this.getID() + "], received REAL REPLY for method "+ methodName + " in body ["+ senderID +"]");

        	// if the update is because an orphan value had arrived, don't send the notification now, or we may risk duplicate notifications,
        	// and one of them (the first one) will have the wrong tags.
        	// A subsequent call to receiveReply will send the notification.
        	// This relies on the fact that the reception of orphans is done by calling FuturePool.receiveFutureValue with reply==null.
        	// If that is changed, this is not guaranteed to work properly.
        	// 
    		if(body != null && !ignoreNotification) {
    			// if it's a half body, it won't have an mbean to send notifications
    			BodyWrapperMBean mbean = body.getMBean();
    			if(mbean != null) {
    				String tagNotification = createTagNotification(this.tags);
    				// TODO correct the parameters for source and destination
    				//      the MonitorController is not reading them for the moment
    				RequestNotificationData requestNotificationData = new RequestNotificationData(
    						null, null, null, null,
    						this.methodName, -1, this.id.getID(),
    						tagNotification);
    				mbean.sendNotification(NotificationType.realReplyReceived, requestNotificationData);
            		CMlogger.debug("REAL REPLY RECEIVED (FutureProxy) ["+id.getID()+"] in ["+ body.getName() +"] tags "+ this.getTags() + " sent to mbean ["+ mbean.getName() +"]");
            		//System.out.println("REAL REPLY RECEIVED (FutureProxy) ["+id.getID()+"] in ["+ body.getName() +"] tags "+ this.getTags() + " sent to mbean ["+ mbean.getName() +"]");
    			}
    			else {
    				CMlogger.debug("NOT SENT REAL REPLY RECEIVED (FutureProxy) --no mbean-- ["+id.getID()+"] in ["+ body.getName() +" tags "+ this.getTags());
    				//System.out.println("NOT SENT REAL REPLY RECEIVED (FutureProxy) --no mbean-- ["+id.getID()+"] in ["+ body.getName() +" tags "+ this.getTags());
    			}
    		}
    		else if(body != null) {
    			if (ignoreNotification) {
    				CMlogger.debug("NOT SENT REAL REPLY RECEIVED (FutureProxy) --orphan value (ignored)-- ["+id.getID()+"] in ["+ (body==null?"---":body.getName()) +" tags "+ this.getTags());
    				//System.out.println("NOT SENT REAL REPLY RECEIVED (FutureProxy) --orphan value-- ["+id.getID()+"] in ["+ (body==null?"---":body.getName()) +" tags "+ this.getTags());
    			}
    		}
        }
        // -- cruz
        
        ExceptionHandler.addResult(this);
        FutureMonitoring.removeFuture(this);

        if (this.callbacks != null) {
            this.callbacks.run();
            this.callbacks = null;
        }

        this.notifyAll();
    }

    /**
     * Returns the result this future is for as an exception if an exception has been raised
     * or null if the result is not an exception. The method blocks until the result is available.
     * @return the exception raised once available or null if no exception.
     */
    public synchronized Throwable getRaisedException() {
        waitFor();
        return target.getException();
    }

    /**
     * @return true iff the future has arrived.
     */
    public boolean isAvailable() {
        return target != null;
    }

    /**
     * Returns a MethodCallResult containing the awaited result, or the exception that occured if any.
     * The method blocks until the future is available
     * @return the result of this future object once available.
     */
    public synchronized MethodCallResult getMethodCallResult() {
        waitFor();
        return target;
    }

    /**
     * Returns the result this future is for. The method blocks until the future is available
     * @return the result of this future object once available.
     */
    public synchronized Object getResult() {
        waitFor();
        return target.getResult();
    }

    /**
     * Returns the result this future is for. The method blocks until the future is available
     * @return the result of this future object once available.
     * @throws ProActiveException if the timeout expires
     */
    public synchronized Object getResult(long timeout) throws ProActiveTimeoutException {
        waitFor(timeout);
        return target.getResult();
    }

    /**
     * Tests the status of the returned object
     * @return <code>true</code> if the future object is NOT yet available, <code>false</code> if it is.
     */
    public synchronized boolean isAwaited() {
        return !isAvailable();
    }

    /**
     * Blocks the calling thread until the future object is available.
     */
    public synchronized void waitFor() {
        try {
            waitFor(0);
        } catch (ProActiveTimeoutException e) {
            throw new IllegalStateException("Cannot happen");
        }
    }

    /**
     * Blocks the calling thread until the future object is available or the timeout expires
     * @param timeout
     * @throws ProActiveException if the timeout expires
     */
    public synchronized void waitFor(long timeout) throws ProActiveTimeoutException {
        if (isAvailable()) {
        	// may I say here that the Real Reply has arrived?
        	// Yes, but it is not necessarily the moment when the Reply arrived. It may have arrived a long time before, because it is anothe
        	// thread who updates it (the sender of the value, or an ACThread)
        	// Moreover, we will get here on each call, for example, to "getResult" (tough this specific case could be solved with a boolean)
        	
        	// This is the place where I am using it, but no necessarily the moment when it arrived.
            return;
        }

        if (Profiling.TIMERS_COMPILED) {
            TimerWarehouse.startTimer(PAActiveObject.getBodyOnThis().getID(),
                    TimerWarehouse.WAIT_BY_NECESSITY);
        }

        FutureMonitoring.monitorFutureProxy(this);

        // JMX Notification
        BodyWrapperMBean mbean = null;
        UniqueID bodyId = PAActiveObject.getBodyOnThis().getID();
        Body body = LocalBodyStore.getInstance().getLocalBody(bodyId);

        // Send notification only if ActiveObject, not for HalfBodies
        if (body != null) {
            mbean = body.getMBean();
            if (mbean != null) {
                mbean.sendNotification(NotificationType.waitByNecessity, new FutureNotificationData(bodyId,
                    getCreatorID()));
                // send "custom" requestWbN notification, with info related to the request
                String tagNotification = createTagNotification(this.tags);
				// TODO correct the parameters for source and destination
				//      the MonitorController is not reading them for the moment
				RequestNotificationData requestNotificationData = new RequestNotificationData(
						null, null, null, null,
						this.methodName, -1, this.id.getID(),
						tagNotification);
				mbean.sendNotification(NotificationType.requestWbN, requestNotificationData);
		        logger.debug("[FutureProxy] ID:["+ id.getID() + "] WaitByNecessity, method ["+ methodName+"], Tags "+ this.getTags() + ", ParentTags "+ this.getParentTags());
		        //System.out.println("[FutureProxy] ID:["+ id.getID() + "] WaitByNecessity, method ["+ methodName+"], Tags "+ this.getTags() + ", ParentTags "+ this.getParentTags());
            }
        }

        // END JMX Notification
        TimeoutAccounter time = TimeoutAccounter.getAccounter(timeout);
        while (!isAvailable()) {
            if (time.isTimeoutElapsed()) {
                throw new ProActiveTimeoutException("Timeout expired while waiting for the future update");
            }
            try {
                this.wait(time.getRemainingTimeout());
            } catch (InterruptedException e) {
                logger.debug(e);
            }
        }

        // JMX Notification.
        // This receivedFutureResult notification is sent ONLY if WbN was incurred.
        // If the result was already available (it had been received before doing any wait on it, the execution
        // won't get to this place (returns in the first if).
        // To detect all Future updates, we should go to the "receiveReply" method.
        if (mbean != null) {
            mbean.sendNotification(NotificationType.receivedFutureResult, new FutureNotificationData(bodyId,
                getCreatorID()));
        }
        if(target.getResult() != null) {
        	logger.debug("[FutureProxy] Future updated after waiting. ID:["+this.getID() +"], target type: ["+target.getResult().getClass().getName()+"]. IsAwaited?" + isAwaited(target.getResult()) + " Method ["+ methodName + "], Tags "+ this.getTags() + ", ParentTags "+ this.getParentTags() );
        	//System.out.println("[FutureProxy] +++++++> DEFINITIVE REPLY RECEIVED (waitFor.futureUpdated) ... ID:["+ id.getID() + "] in ["+(body==null?"---":body.getName())+"] method ["+ methodName+"], Tags "+ this.getTags() + ", ParentTags "+ this.getParentTags());
        }
        // if it is the final result, I could check here too... for the REAL REPLY RECEIVED ...
        // The problem is that this method is not always called. If the result was already available, then the call never arrived to this place.
        
        // This is the place to detect WaitByNecessity, but not the place if we want to detect all Future updates,
        // because, if the value is already available, the execution will not arrive to this place.
        
    	// This is the place where I am using the value (if it is not awaited), but no necessarily the moment when it arrived.

        // END JMX Notification
        if (Profiling.TIMERS_COMPILED) {
            TimerWarehouse
                    .stopTimer(PAActiveObject.getBodyOnThis().getID(), TimerWarehouse.WAIT_BY_NECESSITY);
        }
    }

    public long getID() {
        return id.getID();
    }

    public void setID(long l) {
        if (id == null) {
            id = new FutureID();
        }
        id.setID(l);
    }

    public FutureID getFutureID() {
        return this.id;
    }

    public void setCreatorID(UniqueID creatorID) {
        if (id == null) {
            id = new FutureID();
        }
        id.setCreatorID(creatorID);
    }
    /**
     * The creatorID is the ID of the "evaluator" of the Future, not the "creator" of it.
     */
    public UniqueID getCreatorID() {
        return id.getCreatorID();
    }

    public void setUpdater(UniversalBody updater) {
        if (this.updater != null) {
            new IllegalStateException("Updater already set to: " + this.updater).printStackTrace();
        }
        this.updater = updater;
    }

    public UniversalBody getUpdater() {
        return this.updater;
    }
    
    public UniqueID getSenderID() {
    	return senderID;
    }

    public void setSenderID(UniqueID i) {
        senderID = i;
    }

    public void setOriginatingProxy(AbstractProxy p) {
        originatingProxy = p;
    }

    //
    // -- Implements Proxy -----------------------------------------------
    //

    /**
     * Blocks until the future object is available, then executes Call <code>c</code> on the now-available object.
     *
     *  As future and process behaviors are mutually exclusive, we know that
     * the invocation of a method on a future objects cannot lead to wait-by
     * necessity. Thus, we can propagate all exceptions raised by this invocation
     *
     * @exception InvocationTargetException If the invokation of the method represented by the
     * <code>Call</code> object <code>c</code> on the reified object
     * throws an exception, this exception is thrown as-is here. The stub then
     * throws this exception to the calling thread after checking that it is
     * declared in the throws clause of the reified method. Otherwise, the stub
     * does nothing except print a message on System.err (or out ?).
     */
    public Object reify(MethodCall c) throws InvocationTargetException {
        Object result = null;
        waitFor();

        // Now that the object is available, execute the call
        Object resultObject = target.getResult();
        try {
            result = c.execute(resultObject);
        } catch (MethodCallExecutionFailedException e) {
            throw new ProActiveRuntimeException("FutureProxy: Illegal arguments in call " + c.getName());
        }

        // If target of this future is another future, make a shortcut !
        if (resultObject instanceof StubObject) {
            Proxy p = ((StubObject) resultObject).getProxy();
            if (p instanceof FutureProxy) {
                target = ((FutureProxy) p).target;
            }
        }

        return result;
    }

    // -- PROTECTED METHODS -----------------------------------------------
    //
    public void setCopyMode(boolean mode) {
        copyMode = mode;
    }

    //
    // -- PRIVATE METHODS FOR SERIALIZATION -----------------------------------------------
    //
    private synchronized void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        UniversalBody writtenUpdater = this.updater;

        if (!FuturePool.isInsideABodyForwarder()) {
            // if copy mode, no need for registering AC
            if (this.isAwaited() && !this.copyMode) {
                boolean continuation = (FuturePool.getBodiesDestination() != null);

                // if continuation=false, no destination is registred:
                // - either ac are disabled,
                // - or this future is serialized in a migration forwarder.

                // identify the sender for regsitering continuation and determine if we are in a migration formwarder
                Body sender = LocalBodyStore.getInstance().getLocalBody(senderID);

                // it's a halfbody...
                if (sender == null) {
                    sender = LocalBodyStore.getInstance().getLocalHalfBody(senderID);
                }
                if (sender != null) { // else we are in a migration forwarder
                    if (continuation) {
                        /* The written future will be updated by the writing body */
                        writtenUpdater = PAActiveObject.getBodyOnThis();
                        for (BodiesAndTags dest : FuturePool.getBodiesDestination()) {
                            sender.getFuturePool().addAutomaticContinuation(id, dest);
                        }
                    } else {
                        // its not a copy and not a continuation: wait for the result
                        this.waitFor();
                        logger.debug("-------------------> [FutureProxy].writeObj, after waiting. Body["+sender.getName()+"]Real? should be TRUE" );

                    }
                }
            }
        } else {
            // Maybe this FutureProxy has been added into FuturePool by readObject
            // Remove it and restore continuation
            ArrayList<Future> futures = FuturePool.getIncomingFutures();
            if (futures != null) {
                for (int i = 0; i < futures.size(); i++) {
                    Future fp = futures.get(i);
                    if (fp.getFutureID().equals(this.getFutureID())) {
                        FuturePool.removeIncomingFutures();
                    }
                }
            }
        }

        // for future that are deepcopied then not registered in any futurepool
        out.writeObject(senderID);
        // Pass the result
        out.writeObject(target);
        // Pass the id
        out.writeObject(id);
        // Pass a reference to the updater
        out.writeObject(writtenUpdater.getRemoteAdapter());
        //cruz
        out.writeObject(methodName);
        out.writeObject(parentMethodName);
        out.writeObject(tags);
        out.writeObject(parentTags);
        //--cruz
    }

    /**
     * the use of the synchronized keyword in readObject is meant to prevent race conditions on
     * futures -- do not remove it.
     */
    private synchronized void readObject(java.io.ObjectInputStream in) throws java.io.IOException,
            ClassNotFoundException {
        senderID = (UniqueID) in.readObject();
        target = (MethodCallResult) in.readObject();
        id = (FutureID) in.readObject();
        updater = (UniversalBody) in.readObject();
        //cruz
        methodName = (String) in.readObject();
        parentMethodName = (String) in.readObject();
        tags = (MessageTags) in.readObject();
        parentTags = (MessageTags) in.readObject();
        ignoreNotification = false;
        //--cruz
        // register all incoming futures, even for migration or checkpointing
        if (this.isAwaited()) {
            FuturePool.registerIncomingFuture(this);
        }
        copyMode = false;
    }

    //
    // -- PRIVATE STATIC METHODS -----------------------------------------------
    //
    private static boolean isFutureObject(Object obj) {
        // If obj is not reified, it cannot be a future
        if (!(MOP.isReifiedObject(obj))) {
            return false;
        }

        // Being a future object is equivalent to have a stub/proxy pair
        // where the proxy object implements the interface FUTURE_PROXY_INTERFACE
        // if the proxy does not inherit from FUTURE_PROXY_ROOT_CLASS
        // it is not a future
        Class<?> proxyclass = ((StubObject) obj).getProxy().getClass();
        Class<?>[] ints = proxyclass.getInterfaces();
        for (int i = 0; i < ints.length; i++) {
            if (Constants.FUTURE_PROXY_INTERFACE.isAssignableFrom(ints[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return Returns the exceptionLevel.
     */
    public ExceptionMaskLevel getExceptionLevel() {
        return exceptionLevel;
    }

    /**
     * @param exceptionLevel The exceptionLevel to set.
     */
    public void setExceptionLevel(ExceptionMaskLevel exceptionLevel) {
        this.exceptionLevel = exceptionLevel;
    }

    /**
     * Add a method to call when the future is arrived, or call it now if the
     * future is already arrived.
     */
    public synchronized void addCallback(String methodName) {
        if (this.callbacks == null) {
            this.callbacks = new LocalFutureUpdateCallbacks(this);
        }

        this.callbacks.add(methodName);

        if (this.isAvailable()) {
            this.callbacks.run();
            this.callbacks = null;
        }
    }

    //////////////////////////
    //////////////////////////
    ////FOR DEBUG PURPOSE/////
    //////////////////////////
    //////////////////////////
    public synchronized static int futureLength(Object future) {
        int res = 0;
        if ((MOP.isReifiedObject(future)) && ((((StubObject) future).getProxy()) instanceof Future)) {
            res++;
            Future f = (Future) (((StubObject) future).getProxy());
            Object gna = f.getResult();
            while ((MOP.isReifiedObject(gna)) && ((((StubObject) gna).getProxy()) instanceof Future)) {
                f = (Future) (((StubObject) gna).getProxy());
                gna = f.getResult();
                res++;
            }
        }
        return res;
    }

    public synchronized void setDispatchMonitor(DispatchMonitor dispatchMonitor) {
        this.dispatchMonitor = dispatchMonitor;
    }

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public String getParentMethodName() {
		return parentMethodName;
	}

	public void setParentMethodName(String parentMethodName) {
		this.parentMethodName = parentMethodName;
	}
	
	public MessageTags getTags()  {
		return tags;	
	}
	
	public void setTags(MessageTags messageTags) {
		this.tags = messageTags;
	}
	
	public MessageTags getParentTags()  {
		return parentTags;	
	}
	
	public void setParentTags(MessageTags messageTags) {
		this.parentTags = messageTags;
	}
	
    private String createTagNotification(MessageTags tags) {
        String result = "";
        if (tags != null) {
            for (Tag tag : tags.getTags()) {
                result += tag.getNotificationMessage();
            }
        }
        return result;
    }
    
    public boolean isIgnoreNotification() {
    	return this.ignoreNotification;
    }
    
    public void setIgnoreNotification(boolean b) {
    	ignoreNotification = b;
    }

}
