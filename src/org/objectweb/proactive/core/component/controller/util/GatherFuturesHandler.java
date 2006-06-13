package org.objectweb.proactive.core.component.controller.util;

import java.io.Serializable;
import java.util.List;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.core.body.future.FutureResult;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestFilter;
import org.objectweb.proactive.core.component.representative.ItfID;
import org.objectweb.proactive.core.exceptions.manager.ExceptionThrower;
import org.objectweb.proactive.core.node.Node;


public class GatherFuturesHandler implements RunActive, Serializable {
    List<ItfID> senders;
    List<?> resultOfGatheredInvocation;
    Throwable exceptionToRaise;
    String methodName = null;
    int step = 0;

    public GatherFuturesHandler() {
    }

    public GatherFuturesHandler(List<ItfID> senders) {
        this.senders = senders;
    }

    public GatherFuturesHandler(List<ItfID> senders, String methodName) {
        this.senders = senders;
        this.methodName = methodName;
    }

    public void setFutureOfGatheredInvocation(FutureResult future) {
//    	System.out.println("[gather futures handler] setFutureOfGatheredInvocation");
        if (future.getExceptionToRaise() != null) {
            exceptionToRaise = future.getExceptionToRaise();
        } else {
            // no cast for futures ==> need to get the result before casting
            resultOfGatheredInvocation = (List<?>) future.getResult();
            ProActive.waitFor(resultOfGatheredInvocation);
        }
    }

    // returns
    public Object distribute(ItfID sender) {
//    	System.out.println(" distribute to " + sender.getComponentBodyID());
        if (exceptionToRaise != null) {
//                		System.out.println("[gather futures handler] distribute: throwing exception for [" + methodName + "]");
            ExceptionThrower.throwException(exceptionToRaise); // guillaume's exception thrower
        }

        // APPLY REDISTRIBUTION POLICY HERE !
        return resultOfGatheredInvocation.get(senders.indexOf(sender));
    }

    public void migrateTo(Node node) throws MigrationException {
//        System.out.println("gather futures handler migrating to " + node);
        ProActive.migrateTo(node);
    }

    // TODO a migration-compatible activity (see below)
public void runActivity(Body body) {
        
//	System.out.println("\nFUTURES HANDLER ID IS " + ProActive.getBodyOnThis().getID().getUID());
        Service service = new Service(body);
        service.blockingServeOldest("setFutureOfGatheredInvocation");
        int i=1;
        for (ItfID senderID: senders) {
//        	System.out.println("[gather futures handler] distributing sender " + i);
            service.blockingServeOldest("distribute");
            i++;
        }
        
        
        if (resultOfGatheredInvocation == null) {
            // results after a timeout: ignore
        	service.blockingServeOldest("setFutureOfGatheredInvocation");
        }
        // keep the object alive so that it keeps managing automatic continuations
        service.blockingRemoveOldest();
        
        try {
        	body.disableAC();
            finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /*
     * @see org.objectweb.proactive.RunActive#runActivity(org.objectweb.proactive.Body)
     */
//    public void runActivity(Body body) {
//        Service service = new Service(body);
//        int i = 1;
//        while (step <= senders.size()) {
//            if (step == 0) {
//                Request r = service.blockingRemoveOldest(new MigrateOrSetFuturesRequestFilterImpl());
//                if ("setFutureOfGatheredInvocation".equals(r.getMethodName())) {
//                    step++;
//                }
//                ProActive.getBodyOnThis().serve(r);
//                continue;
//            }
//
//            if ((step >= 1) && (step <= senders.size())) {
//                Request r = service.blockingRemoveOldest(new MigrateOrDistributeRequestFilterImpl());
//                if ("distribute".equals(r.getMethodName())) {
//                    step++;
//                }
//                ProActive.getBodyOnThis().serve(r);
//                i++;
//                continue;
//            }
//        }
//
////        while (resultOfGatheredInvocation == null) {
////            Request r = service.blockingRemoveOldest(new MigrateOrSetFuturesRequestFilterImpl());
////            if ("setFutureOfGatheredInvocation".equals(r.getMethodName())) {
////                //		            // results after a timeout: ignore
////                //                	System.out.println("serving set future after a timeout");
////                ProActive.getBodyOnThis().serve(r);
////                break;
////            } else {
////                ProActive.getBodyOnThis().serve(r);
////            }
////        }
//        while (resultOfGatheredInvocation == null) {
//            // results after a timeout: ignore
//        	service.blockingServeOldest("setFutureOfGatheredInvocation");
//        }
//        
//        service.blockingRemoveOldest();
//
//        try {
//            body.disableAC();
//            finalize();
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//    }

    public static class MigrateOrSetFuturesRequestFilterImpl implements RequestFilter,
        java.io.Serializable {
        public MigrateOrSetFuturesRequestFilterImpl() {
        }

        public boolean acceptRequest(Request request) {
            return ("migrateTo".equals(request.getMethodName()) ||
            "setFutureOfGatheredInvocation".equals(request.getMethodName()));
        }
    }

    public static class MigrateOrDistributeRequestFilterImpl implements RequestFilter,
        java.io.Serializable {
        public MigrateOrDistributeRequestFilterImpl() {
        }

        public boolean acceptRequest(Request request) {
            return ("distribute".equals(request.getMethodName()) ||
            "setFutureOfGatheredInvocation".equals(request.getMethodName()));
        }
    }
}
