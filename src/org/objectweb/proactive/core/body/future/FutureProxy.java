/*
* ################################################################
*
* ProActive: The Java(TM) library for Parallel, Distributed,
*            Concurrent computing with Security and Mobility
*
* Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
* Contact: proactive-support@inria.fr
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
* USA
*
*  Initial developer(s):               The ProActive Team
*                        http://www.inria.fr/oasis/ProActive/contacts.html
*  Contributor(s):
*
* ################################################################
*/
package org.objectweb.proactive.core.body.future;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.Constants;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.LocalBodyStore;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.event.FutureEvent;
import org.objectweb.proactive.core.exceptions.NonFunctionalException;
import org.objectweb.proactive.core.exceptions.handler.Handler;
import org.objectweb.proactive.core.mop.ConstructionOfReifiedObjectFailedException;
import org.objectweb.proactive.core.mop.ConstructorCall;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.MethodCallExecutionFailedException;
import org.objectweb.proactive.core.mop.Proxy;
import org.objectweb.proactive.core.mop.StubObject;


/**
 * This proxy class manages the semantic of future objects
 *
 * @author Julien Vayssi?re - INRIA
 * @see org.objectweb.proactive.core.mop.Proxy
 *
 */
public class FutureProxy implements Future, Proxy, java.io.Serializable {
    //
    // -- STATIC MEMBERS -----------------------------------------------
    //

    /**
     *  The size of the pool we use for recycling FutureProxy objects.
     */
    public static final int RECYCLE_POOL_SIZE = 1000;
    private static FutureProxy[] recyclePool;

    /**
     *  Indicates if the recycling of FutureProxy objects is on.
     */
    private static boolean shouldPoolFutureProxyObjects;
    private static int index;

    /** Static point for management of events related to futures.
     * This FutureEventProducer is responsible for all FutureProxys of this VM */
    private static FutureEventProducerImpl futureEventProducer;

    //
    // -- PROTECTED MEMBERS -----------------------------------------------
    //

    /**
     *        The object the proxy sends calls to
     */
    protected Object target;

    /**
     * To mark the Proxy before migration
     * Usually, the Proxy cannot be serialized if the result is not available (no automatic continuation)
     * but if we migrate, we don't want to wait for the result
     */
    protected boolean migration;

    /**
     * To mark the proxy before sending this future by parameter or by result
     */
    protected boolean continuation;

    /**
     * UniqueID of the body which create this future
     */
    protected UniqueID creatorID;

    /**
     * ID of the future
     * In fact, the sequence number of the request that generate this future
     */
    protected long ID;

    /**
     * Unique ID of the sender (in case of automatic continuation).
     */
    protected UniqueID senderID;

    /**
     * This flag indicates the status of the future object
     */
    protected boolean isAvailable;

    /**
     * Indicates if the returned object is an exception
     */
    protected boolean isException;

    /**
    * Indicates if the returned object is a Non Functional Exception (thus the communication failed)
    */
    protected boolean isNFE;

    /**
     * This table is needed for the NFE mechanism
     */
    protected HashMap futureLevel = null;

    /**
     * Get NFE logger
     */
    protected static Logger logger = Logger.getLogger("NFE");

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
     * More precisely, this permits proxy instanciation via the Meta.newMeta
     * method.
     */
    public FutureProxy(ConstructorCall c, Object[] p)
        throws ConstructionOfReifiedObjectFailedException {
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
        // If the object is not reified, it cannot be a future
        if ((MOP.isReifiedObject(obj)) == false) {
            return false;
        }
        Proxy theProxy = ((StubObject) obj).getProxy();

        // If it is reified but its proxy is not of type future, we cannot wait
        if (!(theProxy instanceof Future)) {
            return false;
        }
        return ((Future) theProxy).isAwaited();
    }

    public synchronized static FutureProxy getFutureProxy() {
        FutureProxy result;
        if (shouldPoolFutureProxyObjects && (index > 0)) {
            // gets the object from the pool
            index--;
            result = recyclePool[index];
            recyclePool[index] = null;
        } else {
            try {
                result = new FutureProxy();
            } catch (ConstructionOfReifiedObjectFailedException e) {
                result = null;
            }
        }
        return result;
    }

    /** Returns the <code>FutureEventProducer</code> that is responsible for the
     * FutureProxys of this VM. Listeners can register themselves here. */
    public static FutureEventProducer getFutureEventProducer() {
        if (futureEventProducer == null) {
            futureEventProducer = new FutureEventProducerImpl();
        }
        return futureEventProducer;
    }

    //
    // -- PUBLIC METHODS -----------------------------------------------
    //
    public boolean equals(Object obj) {
        //we test if we have a future object
        if (isFutureObject(obj)) {
            return (((StubObject) obj).getProxy().hashCode() == this.hashCode());
        }
        return false;
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
    public synchronized void receiveReply(Object obj)
        throws java.io.IOException {
        if (target != null) {
            throw new java.io.IOException(
                "FutureProxy receives a reply and this target field is not null");
        }
        target = obj;
        if (target != null) {
            isException = (target instanceof Throwable);
            isNFE = (target instanceof NonFunctionalException);
        }
        isAvailable = true;
        if (this.isNFE) {
            //System.out.println("GET NFE");
            Handler handler = ProActive.searchExceptionHandler((NonFunctionalException) this.target,
                    this);
            handler.handle((NonFunctionalException) this.target, ProActive.getBodyOnThis().getNodeURL());
            //throw ((InvocationTargetException) this.target);
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
        if (isException) {
            return (Throwable) target;
        }
        return null;
    }

    /**
     * Returns the result this future is for. The method blocks until the future is available
     * @return the result of this future object once available.
     */
    public synchronized Object getResult() {
        waitFor();
        return target;
    }

    public synchronized void setResult(Object o) {
        target = o;
        isAvailable = true;
    }

    /**
     * Tests the status of the returned object
     * @return <code>true</code> if the future object is NOT yet available, <code>false</code> if it is.
     */
    public synchronized boolean isAwaited() {
        return !isAvailable;
    }

    /**
     * Blocks the calling thread until the future object is available.
     */
/*    public synchronized void waitFor() {
        if (isAvailable) {
            return;
        }

        UniqueID id = null;

        // send WAIT_BY_NECESSITY event to listeners if there are any
        if (futureEventProducer != null) {
            id = ProActive.getBodyOnThis().getID();
            if (LocalBodyStore.getInstance().getLocalBody(id) != null) {
                // send event only if ActiveObject, not for HalfBodies
                futureEventProducer.notifyListeners(id, getCreatorID(),
                    FutureEvent.WAIT_BY_NECESSITY);
            } else {
                id = null;
            }
        }
        while (!isAvailable) {
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
        }

        // send RECEIVED_FUTURE_RESULT event to listeners if there are any
        if (id != null) {
            futureEventProducer.notifyListeners(id, getCreatorID(),
                FutureEvent.RECEIVED_FUTURE_RESULT);
        }
    }
*/
    
    /**
     * Blocks the calling thread until the future object is available.
     */
    public synchronized void waitFor() {
        try {
            waitFor(0);
        } catch (ProActiveException e) {
            // Exception above should never be thrown since wait(0) means no timeout
            e.printStackTrace();
        }
    }

    /**
     * Blocks the calling thread until the future object is available or the timeout expires
     * @param timeout
     * @throws ProActiveException if the timeout expires
     */
    public synchronized void waitFor(long timeout) throws ProActiveException {
        if (isAvailable) {
            return;
        }

        UniqueID id = null;

        // send WAIT_BY_NECESSITY event to listeners if there are any
        if (futureEventProducer != null) {
            id = ProActive.getBodyOnThis().getID();
            if (LocalBodyStore.getInstance().getLocalBody(id) != null) {
                // send event only if ActiveObject, not for HalfBodies
                futureEventProducer.notifyListeners(id, getCreatorID(),
                    FutureEvent.WAIT_BY_NECESSITY);
            } else {
                id = null;
            }
        }
        int timeoutCounter = 1;
        while (!isAvailable) {
            timeoutCounter--;
            // counter < 0 means that it is the second time we enter in the loop
            // while still not available, i.e timeout has expired
            if (timeoutCounter < 0) {
                throw new ProActiveException(
                    "Timeout expired while waiting for the future update");
            }
            try {
                this.wait(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // send RECEIVED_FUTURE_RESULT event to listeners if there are any
        if (id != null) {
            futureEventProducer.notifyListeners(id, getCreatorID(),
                FutureEvent.RECEIVED_FUTURE_RESULT);
        }
    }

    public long getID() {
        return ID;
    }

    public void setID(long l) {
        ID = l;
    }

    public void setCreatorID(UniqueID i) {
        creatorID = i;
    }

    public UniqueID getCreatorID() {
        return creatorID;
    }

    public void setSenderID(UniqueID i) {
        senderID = i;
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

        //stem.out.println("FutureProxy: c.getName() = " +c.getName());
        //		if ((c.getName()).equals("equals") || (c.getName()).equals("hashCode")) {
        //			//System.out.println("FutureProxy: now executing " + c.getName());
        //			try {
        //				result = c.execute(this);
        //			} catch (MethodCallExecutionFailedException e) {
        //				throw new ProActiveRuntimeException("FutureProxy: Illegal arguments in call " + c.getName());
        //			}
        //			return result;
        //		}
        waitFor();

        // The object is available, but it may be a NFE that signal a service exception
        //System.out.println("GET FUTURE");
        /*if (this.isNFE) {
            System.out.println("GET NFE");
            Handler handler = ProActive.searchExceptionHandler((NonFunctionalException) this.target,
                    this);
            handler.handle((NonFunctionalException) this.target, c);
            //throw ((InvocationTargetException) this.target);
        }*/

        // Now that the object is available, execute the call
        if (this.isException) {
            throw ((InvocationTargetException) this.target);
        } else {
            try {
                result = c.execute(this.target);
            } catch (MethodCallExecutionFailedException e) {
                throw new ProActiveRuntimeException(
                    "FutureProxy: Illegal arguments in call " + c.getName());
            }
        }

        // If target of this future is another future, make a shortcut !
        if (target instanceof StubObject) {
            Proxy p = ((StubObject) target).getProxy();
            if (p instanceof FutureProxy) {
                target = ((FutureProxy) p).target;
            }
        }

        return result;
    }

    /**
     * Get information about the handlerizable object
     * @return information about the handlerizable object
     */
    public String getHandlerizableInfo() throws java.io.IOException {
        return "FUTURE (ID=" + this.ID + ") of CLASS [" + this.getClass() +
        "]";
    }

    /** Give a reference to a local map of handlers
    * @return A reference to a map of handlers
    */
    public HashMap getHandlersLevel() throws java.io.IOException {
        return futureLevel;
    }

    /**
         * Clear the local map of handlers
         */
    public void clearHandlersLevel() throws java.io.IOException {
        futureLevel.clear();
    }

    /** Set a new handler within the table of the Handlerizable Object
         * @param handler A handler associated with a class of non functional exception.
         * @param exception A class of non functional exception. It is a subclass of <code>NonFunctionalException</code>.
         */
    public void setExceptionHandler(Handler handler, Class exception)
        throws java.io.IOException {
        // add handler to future level
        if (futureLevel == null) {
            futureLevel = new HashMap();
        }
        futureLevel.put(exception, handler);
    }

    /** Remove a handler from the table of the Handlerizable Object
         * @param exception A class of non functional exception. It is a subclass of <code>NonFunctionalException</code>.
         * @return The removed handler or null
         */
    public Handler unsetExceptionHandler(Class exception)
        throws java.io.IOException {
        // remove handler from future level
        if (futureLevel != null) {
            Handler handler = (Handler) futureLevel.remove(exception);
            return handler;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("[NFE_WARNING] No handler for [" +
                    exception.getName() + "] can be removed from FUTURE level");
            }
            return null;
        }
    }

    //
    // -- PROTECTED METHODS -----------------------------------------------
    //
    protected void finalize() {
        returnFutureProxy(this);
    }

    protected void setMigrationTag() {
        migration = true;
    }

    protected void unsetMigrationTag() {
        migration = false;
    }

    public synchronized void setContinuationTag() {
        continuation = true;
    }

    public synchronized void unsetContinuationTag() {
        continuation = false;
    }

    //
    // -- PRIVATE METHODS FOR SERIALIZATION -----------------------------------------------
    //
    private synchronized void writeObject(java.io.ObjectOutputStream out)
        throws java.io.IOException {
        //if continuation is already set, we are in a forwarder
        //else if a destination is available in destTable, set the continuation tag
        if (!continuation) {
            continuation = (FuturePool.getBodyDestination() != null);
        }

        // We wait until the result is available
        if ((!migration) && (!continuation)) {
            waitFor();
        }

        // Registration in case of continuation
        if (continuation && isAwaited()) {
            // get the sender body
            Body sender = LocalBodyStore.getInstance().getLocalBody(senderID);

            // it's a halfbody...
            if (sender == null) {
                sender = LocalBodyStore.getInstance().getLocalHalfBody(senderID);
            }
            if (sender != null) {
                UniversalBody dest = FuturePool.getBodyDestination();
                if (dest != null) {
                    sender.getFuturePool().addAutomaticContinuation(ID,
                        creatorID, dest);
                }
            }

            // if sender is still null, it's a forwarder !!
        }

        // Pass the result
        out.writeObject(target);
        // Pass the continuation flag
        out.writeBoolean(continuation);
        // Pass the id
        out.writeLong(ID);
        //Pass the creatorID
        out.writeObject(creatorID);

        // It is impossible that a future object can be passed
        // as a parameter if it has raised a checked exception
        // For the other exceptions...
        out.writeBoolean(isException);
        out.writeBoolean(isAvailable);

        //unset the current continuation tag
        this.continuation = false;
    }

    private synchronized void readObject(java.io.ObjectInputStream in)
        throws java.io.IOException, ClassNotFoundException {
        target = (Object) in.readObject();
        continuation = (boolean) in.readBoolean();
        ID = (long) in.readLong();
        creatorID = (UniqueID) in.readObject();
        isException = (boolean) in.readBoolean();
        isAvailable = (boolean) in.readBoolean();

        if (continuation && isAwaited()) {
            continuation = false;
            FuturePool.registerIncomingFuture(this);
        }

        //now we restore migration to its normal value
        migration = false;
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
        Class proxyclass = ((StubObject) obj).getProxy().getClass();
        Class[] ints = proxyclass.getInterfaces();
        for (int i = 0; i < ints.length; i++) {
            if (Constants.FUTURE_PROXY_INTERFACE.isAssignableFrom(ints[i])) {
                return true;
            }
        }
        return false;
    }

    private static synchronized void setShouldPoolFutureProxyObjects(
        boolean value) {
        if (shouldPoolFutureProxyObjects == value) {
            return;
        }
        shouldPoolFutureProxyObjects = value;
        if (shouldPoolFutureProxyObjects) {
            // Creates the recycle poll for FutureProxy objects
            recyclePool = new FutureProxy[RECYCLE_POOL_SIZE];
            index = 0;
        } else {
            // If we do not want to recycle FutureProxy objects anymore,
            // let's free some memory by permitting the reyclePool to be
            // garbage-collecting
            recyclePool = null;
        }
    }

    private static synchronized void returnFutureProxy(FutureProxy futureProxy) {
        if (!shouldPoolFutureProxyObjects) {
            return;
        }

        // If there's still one slot left in the pool
        if (recyclePool[index] == null) {
            // Cleans up a FutureProxy object
            // It is prefereable to do it here rather than at the moment
            // the object is picked out of the pool, because it allows
            // garbage-collecting the objects referenced in here
            futureProxy.target = null;
            futureProxy.isAvailable = false;
            futureProxy.isException = false;

            // Inserts the object in the pool
            recyclePool[index] = futureProxy;
            index++;
            if (index == RECYCLE_POOL_SIZE) {
                index = RECYCLE_POOL_SIZE - 1;
            }
        }
    }

    //////////////////////////
    //////////////////////////
    ////FOR DEBUG PURPOSE/////
    //////////////////////////
    //////////////////////////
    public synchronized static int futureLength(Object future) {
        int res = 0;
        if ((MOP.isReifiedObject(future)) &&
                ((((StubObject) future).getProxy()) instanceof Future)) {
            res++;
            Future f = (Future) (((StubObject) future).getProxy());
            Object gna = f.getResult();
            while ((MOP.isReifiedObject(gna)) &&
                    ((((StubObject) gna).getProxy()) instanceof Future)) {
                f = (Future) (((StubObject) gna).getProxy());
                gna = f.getResult();
                res++;
            }
        }
        return res;
    }
}
