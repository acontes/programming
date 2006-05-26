package org.objectweb.proactive.core.component.controller.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.ServeException;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.ProActiveInterface;
import org.objectweb.proactive.core.component.body.ComponentBodyImpl;
import org.objectweb.proactive.core.component.identity.ProActiveComponent;
import org.objectweb.proactive.core.component.representative.ItfID;
import org.objectweb.proactive.core.component.request.ComponentRequest;
import org.objectweb.proactive.core.component.request.ComponentRequestImpl;
import org.objectweb.proactive.core.component.type.ProActiveInterfaceType;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactory;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;

/**
 * This class orders requests arriving to gathercast interfaces into queues.
 * 
 * When a request on a gathercast interface arrives, it is put into a dedicated queue.
 * 
 * There is one list of queues (lazily created) for each method of each gathercast interface.
 * 
 *  Two requests originating from the same interface and addressed to the same method on the same gathercast interface
 *  are put into separate queues.
 *  
 *  Once all clients of a gathercast interface have sent a request, and if the timeout is not reached, a new request is created, which
 *  gathers the invocation parameters from the individual requests, and it is served on the 
 *   
 * @author Matthieu Morel
 *
 */
public class GatherRequestsQueues {
    private static Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_GATHERCAST);
    // Map <serverItfName, map<signatureOfInvokedMethod, list<queuedRequests>>>
    Map<String, Map<Method, List<GatherRequestsQueue>>> queues = new HashMap<String, Map<Method, List<GatherRequestsQueue>>>();
    ProActiveComponent owner;
    List<GatherFuturesHandler> futuresHandlers = new ArrayList<GatherFuturesHandler>();
    List<ItfID> gatherItfs = new ArrayList<ItfID>();
    ProActiveInterfaceType[] itfTypes;

    public GatherRequestsQueues(ProActiveComponent owner) {
        this.owner = owner;
        Object[] untypedItfs = owner.getFcInterfaces();
        itfTypes = new ProActiveInterfaceType[untypedItfs.length];
        for (int i = 0; i < itfTypes.length; i++) {
            itfTypes[i] = (ProActiveInterfaceType) ((ProActiveInterface) untypedItfs[i]).getFcItfType();
        }

        for (int i = 0; i < itfTypes.length; i++) {
            if (ProActiveTypeFactory.GATHER_CARDINALITY.equals(
                        itfTypes[i].getFcCardinality())) {
                // add a queue for each gather itf
                Map<Method, List<GatherRequestsQueue>> map = new HashMap<Method, List<GatherRequestsQueue>>();
                queues.put(itfTypes[i].getFcItfName(), map);
                gatherItfs.add(new ItfID(itfTypes[i].getFcItfName(),
                        owner.getID()));
            }
        }
    }

    /**
     * Adds a request into the corresponding queue 
     */
    public Object addRequest(ComponentRequest r) throws ServeException {
        Object result = null;
        String serverItfName = r.getMethodCall().getComponentMetadata()
                                .getComponentInterfaceName();
        ItfID senderItfID = r.getMethodCall().getComponentMetadata()
                             .getSenderItfID();

        Method reifiedMethod = r.getMethodCall().getReifiedMethod();
        Method itfTypeMethod;
        try {
            itfTypeMethod = GatherBindingChecker.searchMatchingMethod(reifiedMethod,
                    Class.forName(getItfType(serverItfName).getFcItfSignature())
                         .getMethods());
        } catch (Exception e1) {
            e1.printStackTrace();
            throw new ServeException("problem when analysing gather request", e1);
        }

        List<ItfID> connectedClientItfs;
        try {
            connectedClientItfs = Fractive.getGathercastController(owner)
                                          .getConnectedClientItfs(serverItfName);
        } catch (NoSuchInterfaceException e) {
            throw new ServeException("this component has no binding controller");
        }
        if (!connectedClientItfs.contains(senderItfID)) {
            throw new ServeException(
                "cannot handle gather invocation : this invocation orginates from a client interface which is not bound ");
        }

        if (!queues.containsKey(serverItfName)) {
            throw new ProActiveRuntimeException(
                "there is no gathercast interface named " + serverItfName);
        }

        Map<Method, List<GatherRequestsQueue>> map = queues.get(serverItfName);

        List<GatherRequestsQueue> list = map.get(itfTypeMethod);

        if (list == null) {
            list = new ArrayList<GatherRequestsQueue>();
            // new queue, and add current request
            GatherRequestsQueue queue = new GatherRequestsQueue(owner,
                    serverItfName, itfTypeMethod, connectedClientItfs);
            list.add(queue);
            map.put(itfTypeMethod, list);
        }

        if (list.isEmpty()) {
            GatherRequestsQueue queue = new GatherRequestsQueue(owner,
                    serverItfName, itfTypeMethod, connectedClientItfs);
            map.get(itfTypeMethod).add(queue);
        }

        for (Iterator iter = list.iterator(); iter.hasNext();) {
            GatherRequestsQueue queue = (GatherRequestsQueue) iter.next();
            if (queue.containsRequestFrom(senderItfID)) {
                // there is already a request from this comp/itf
                if (!iter.hasNext()) {
                    // no other queue to receive this request. create one
                    // concurrent access exception?
                    queue = new GatherRequestsQueue(owner, serverItfName,
                            itfTypeMethod, connectedClientItfs);
                    // add the request
                    result = queue.put(senderItfID, r);
                    list.add(queue);
                    break;
                }
                continue;
            }
            // TODO if request is synchronous : put in threaded queue, notify, then put the thread on sleep and serve next request 

            // add this request
            result = queue.put(senderItfID, r);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("added request [" + r.getMethodName() +
                "] in gather queue");
        }
        // check if needs to do something!
        notifyUpdate(serverItfName, list);

        return result;
    }

    private void notifyUpdate(String serverItfName,
        List<GatherRequestsQueue> requestQueues) throws ServeException {

        // default: if all connected itfs have sent a request, then process it
        try {
            List<ItfID> connectedClientItfs = Fractive.getGathercastController(owner)
                                                      .getConnectedClientItfs(serverItfName);
            GatherRequestsQueue firstRequestsInLine = requestQueues.get(0); // need to ensure this
            if (firstRequestsInLine.isFull()) {
                // ok, condition met, proceed with request

                // create a new gather request by gathering parameters
                Method clientMethod = firstRequestsInLine.getInvokedMethod();
                String methodName = clientMethod.getName();
                if (logger.isDebugEnabled()) {
                    logger.debug(
                        "conditions reached, processing gather request [" +
                        methodName + "]");
                }

                Class[] clientMethodParamTypes = clientMethod.getParameterTypes();
                Class[] gatherMethodParamTypes = new Class[clientMethodParamTypes.length];

                for (int i = 0; i < clientMethodParamTypes.length; i++) {
                    gatherMethodParamTypes[i] = List.class;
                }

                Class gatherItfClass = Class.forName(((ProActiveInterfaceType) ((ProActiveInterface) owner.getFcInterface(
                            serverItfName)).getFcItfType()).getFcItfSignature());

                Method gatherMethod = gatherItfClass.getMethod(clientMethod.getName(),
                        gatherMethodParamTypes);
                Object[] gatherEffectiveArguments = new Object[gatherMethodParamTypes.length];

                // build the list of parameters
                for (int i = 0; i < gatherEffectiveArguments.length; i++) {
                    List<Object> l = new ArrayList<Object>(connectedClientItfs.size());
                    for (Iterator iter = connectedClientItfs.iterator();
                            iter.hasNext();) {
                        ItfID id = (ItfID) iter.next();
                        // keep same ordering as connected client itfs
                        l.add(firstRequestsInLine.get(id).getMethodCall()
                                                 .getEffectiveArguments()[i]);
                    }
                    // parameters from a given client have the same order than this client in the list of connected clients 
                    gatherEffectiveArguments[i] = l;
                }

                // create the request
                MethodCall gatherMC = MethodCall.getComponentMethodCall(gatherMethod,
                        gatherEffectiveArguments, serverItfName,
                        new ItfID(serverItfName, owner.getID()));

                ComponentRequest gatherRequest = new ComponentRequestImpl(gatherMC,
                        ProActive.getBodyOnThis(),
                        firstRequestsInLine.oneWayMethods(),
                        ((ComponentBodyImpl) ProActive.getBodyOnThis()).getNextSequenceID());

                // serve the request (do not reenqueue it)
                if (logger.isDebugEnabled()) {
					logger.debug("gather request queues .serving request [" + gatherRequest.getMethodName()+ "]");
				}
                Reply reply = gatherRequest.serve(ProActive.getBodyOnThis());

                // handle the future for async invocations
                if (reply != null) {
                    firstRequestsInLine.addFutureForGatheredRequest(reply.getResult());
                }

                // remove the list that was just used
                requestQueues.remove(0);
            }
        } catch (NoSuchInterfaceException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private ProActiveInterfaceType getItfType(String name) {
        for (int i = 0; i < itfTypes.length; i++) {
            if (name.equals(itfTypes[i].getFcItfName())) {
                return itfTypes[i];
            }
        }
        return null;
    }

 
}
