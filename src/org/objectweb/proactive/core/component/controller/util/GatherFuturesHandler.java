package org.objectweb.proactive.core.component.controller.util;

import java.util.List;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.core.body.future.FutureResult;
import org.objectweb.proactive.core.component.representative.ItfID;
import org.objectweb.proactive.core.exceptions.manager.ExceptionThrower;

public class GatherFuturesHandler implements RunActive {
    
    List<ItfID> senders;
    List<?> resultOfGatheredInvocation;
    Throwable exceptionToRaise;
    String methodName = null;
    
    public GatherFuturesHandler() {}
    
    public GatherFuturesHandler(List<ItfID> senders) {
        this.senders = senders;
    }
    
    public GatherFuturesHandler(List<ItfID> senders, String methodName) {
        this.senders = senders;
        this.methodName = methodName;
    }

    
    public void setFutureOfGatheredInvocation(FutureResult future) {
    	if (future.getExceptionToRaise() != null) {
    		exceptionToRaise = future.getExceptionToRaise();
    	} else {
    		// no cast for futures ==> need to get the result before casting
    		resultOfGatheredInvocation = (List<?>)future.getResult();
   			ProActive.waitFor(resultOfGatheredInvocation);
    	}
    }
    
    // returns
    public Object distribute(ItfID sender) {
    	if (exceptionToRaise!=null) {
//    		System.out.println("[gather futures handler] distribute: throwing exception for [" + methodName + "]");
    		ExceptionThrower.throwException(exceptionToRaise); // guillaume's exception thrower
    	}
    	
    	// APPLY REDISTRIBUTION POLICY HERE !
        return resultOfGatheredInvocation.get(senders.indexOf(sender));
        
    }


    /*
     * @see org.objectweb.proactive.RunActive#runActivity(org.objectweb.proactive.Body)
     */
    public void runActivity(Body body) {
        
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
        
        try {
        	body.disableAC();
            finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    
    

}
