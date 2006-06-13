/**
 *
 */
package org.objectweb.proactive.core.component.controller.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.body.future.FutureResult;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.core.component.exceptions.GathercastTimeoutException;
import org.objectweb.proactive.core.component.identity.ProActiveComponent;
import org.objectweb.proactive.core.component.representative.ItfID;
import org.objectweb.proactive.core.component.request.ComponentRequest;
import org.objectweb.proactive.core.component.type.annotations.gathercast.MethodSynchro;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.SerializableMethod;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * This class manages a queue of requests on a gather interface: a list of reified invocations from connected client interfaces.
 *
 * Asynchronization is provided with a third-party active object and automatic continuations.
 *
 * A timeout exception is thrown if some of the connected client interfaces fail to send a request before the timeout; the countdown
 * is triggered right after serving the first invocation on the gathercast interface.
 *
 * @author Matthieu Morel
 *
 */
public class GatherRequestsQueue implements Serializable {
    private ProActiveComponent owner;
    private GatherFuturesHandler futuresHandler; // need a pool!
    private List<ItfID> connectedClientItfs; // consistency?
    private Map<ItfID, ComponentRequest> requests;
    private String serverItfName;
    private SerializableMethod itfTypeInvokedMethod;
    transient long creationTime = System.currentTimeMillis(); // todo do not reinitialize after deserialization
    public static final long DEFAULT_TIMEOUT = 1000000; // TODO use a proactive default property
    private Timer timeoutTimer = null;
    boolean timedout = false;
    boolean thrownTimeoutException = false;
    long timeout = DEFAULT_TIMEOUT;
    private static Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_GATHERCAST);

    public GatherRequestsQueue(ProActiveComponent owner, String serverItfName,
        Method itfTypeMethod, List<ItfID> connectedClientItfs) {
        this.owner = owner;
        this.serverItfName = serverItfName;
        //        this.conditionChecker = gatherConditionChecker;
        //        this.invokedMethodSignature = methodSignature;
        itfTypeInvokedMethod = new SerializableMethod(itfTypeMethod);
        this.connectedClientItfs = connectedClientItfs;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("adding futures handler for requests on " +
                    serverItfName + "." + itfTypeMethod.getName());
            }
            futuresHandler = (GatherFuturesHandler) ProActive.newActive(GatherFuturesHandler.class.getName(),
                    new Object[] { connectedClientItfs, itfTypeMethod.getName() });
        } catch (ActiveObjectCreationException e) {
            throw new ProActiveRuntimeException("cannot create futures handler for gather interface",
                e);
        } catch (NodeException e) {
            throw new ProActiveRuntimeException("cannot create futures handler for gather interface",
                e);
        }
        requests = new HashMap<ItfID, ComponentRequest>();

        // add first request
        //        requests.put(r.getMethodCall().getComponentMetadata().getSenderItfID(), r);
    }

    public boolean containsRequestFrom(ItfID clientItfID) {
        return requests.containsKey(clientItfID);
    }

    public synchronized Object put(ItfID clientItfID, ComponentRequest request) {
        if (isFull()) {
            throw new ProActiveRuntimeException("gather requests queue is full");
        }
        requests.put(clientItfID, request);

        // evaluate timeout
        if (timeoutTimer == null) {
            timeoutTimer = new Timer();
            MethodSynchro sc = itfTypeInvokedMethod.getMethod().getAnnotation(MethodSynchro.class);
            if (sc != null) {
                timeout = sc.timeout();
            } else {
                timeout = DEFAULT_TIMEOUT;
            }
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "gather request queue timer starting with timeout = " +
                    timeout);
            }
            timeoutTimer.schedule(new TimeoutTask(this), timeout);
        }

        if (isFull()) {
            timeoutTimer.cancel();
        }

        if (((System.currentTimeMillis() - creationTime) / 1000) >= timeout) {
            // we need to check this for small timeouts because timer runs concurrently
            timedout = true;
            addFutureForGatheredRequest(null);
        }
        Object reply = futuresHandler.distribute(clientItfID);

        // return future result (will be computed when gather request is processed)
        	try {
//        		System.out.println("RETURNING REPLY FOR REQUEST " + itfTypeInvokedMethod.getMethod());
        		return reply;
        	} finally {
                if (isFull() ) {
                	try {
						finalize();
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
        }
        
    }

    public ComponentRequest get(ItfID id) {
        return requests.get(id);
    }

    public boolean isFull() {
        return (requests.size() == connectedClientItfs.size());
    }

    public int size() {
        return requests.size();
    }

    public Method getInvokedMethod() {
        // return the first one
        if (requests.isEmpty()) {
            return null;
        }
        return requests.get(requests.keySet().iterator().next()).getMethodCall()
                       .getReifiedMethod();
    }

    public boolean oneWayMethods() {
        if (requests.isEmpty()) {
            return false;
        }
        return requests.get(requests.keySet().iterator().next()).isOneWay();
    }

    public void addFutureForGatheredRequest(FutureResult futureResult) {
    	
        if (timedout) {
            // avoids race condition with small timeouts (result is replaced with a timeout exception)
            if (!thrownTimeoutException) {
                if (logger.isDebugEnabled()) {
                    logger.debug("timeout reached at " + timeout +
                        "for gather request on [" +
                        itfTypeInvokedMethod.getMethod().getName() + "]");
                }
                thrownTimeoutException = true;
                futuresHandler.setFutureOfGatheredInvocation(new FutureResult(
                        null,
                        new GathercastTimeoutException("timeout of " + timeout +
                            " reached before invocations from all clients were received for gather invocation (method " +
                            itfTypeInvokedMethod.getMethod().toGenericString() +
                            " on gather interface " + serverItfName), null));
            }

            // else ignore
        } else {
            // this will trigger automatically the distribution of result for clients of the gather itf
            futuresHandler.setFutureOfGatheredInvocation(futureResult);
        }
        timeoutTimer.cancel();
    }

    /**
     * @return Returns the creationTime.
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * @return Returns the requests.
     */
    public Map<ItfID, ComponentRequest> getRequests() {
        return requests;
    }

    /**
     * @return Returns the connectedClientItfs.
     */
    public List<ItfID> getConnectedClientItfs() {
        return connectedClientItfs;
    }

    private class TimeoutTask extends TimerTask {
        GatherRequestsQueue requestsQueue;

        public TimeoutTask(GatherRequestsQueue requestsQueue) {
            this.requestsQueue = requestsQueue;
        }

        public void run() {
            timedout = true;
            if (!thrownTimeoutException) {
                requestsQueue.addFutureForGatheredRequest(new FutureResult(
                        null,
                        new GathercastTimeoutException("timeout of " + timeout +
                            " reached before invocations from all clients were received for gather invocation (method " +
                            itfTypeInvokedMethod.getMethod().toGenericString() +
                            " on gather interface " + serverItfName), null));
            }
            try {
                //				requestsQueue.finalize();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
    
    public void migrateFuturesHandlerTo(Node node) throws MigrationException {
    	futuresHandler.migrateTo(node);
    }
    
    private void writeObject(java.io.ObjectOutputStream out)
    throws java.io.IOException {
//    System.out.println("writing gather requests queue");
    out.defaultWriteObject();
}
    private void readObject(java.io.ObjectInputStream in)
    throws java.io.IOException, ClassNotFoundException {
//    System.out.println("reading gather requests queue");
    in.defaultReadObject();
}
}
