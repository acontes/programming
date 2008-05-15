package org.objectweb.proactive.core.component.group;

import java.util.List;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.component.body.ComponentBody;
import org.objectweb.proactive.core.group.DispatchBehavior;
import org.objectweb.proactive.core.group.ProxyForGroup;
import org.objectweb.proactive.core.mop.MethodCall;


public class ComponentDispatchControllerProxy implements DispatchBehavior {

    DispatchBehavior dispatchController;

    // @Override
    // public int getExpectedNumberOfTasks(MethodCall originalMethodCall,
    // List<MethodCall> generatedMethodCalls, int nbWorkers) {
    // return dispatchController.getExpectedNumberOfTasks(originalMethodCall,
    // generatedMethodCalls, nbWorkers);
    // }

    public List<Integer> getTaskIndexes(MethodCall originalMethodCall, List<MethodCall> generatedMethodCalls,
            int nbWorkers) {
        return dispatchController.getTaskIndexes(originalMethodCall, generatedMethodCalls, nbWorkers);
    }

    //	public void setContext(String context) {
    ////		try {
    //		    throw new RuntimeException("set context ");
    ////			Object o = ((ComponentBody) PAActiveObject
    ////					.getBodyOnThis()).getProActiveComponentImpl()
    ////					.getFcInterface(context);
    ////			dispatchController = (AllocationBehavior)o;
    //////			dispatchController = (AllocationBehavior) ((ComponentBody) ProActiveObject
    //////					.getBodyOnThis()).getProActiveComponentImpl()
    //////					.getFcInterface(context);
    ////		} catch (NoSuchInterfaceException e) {
    ////			// TODO Auto-generated catch block
    ////			e.printStackTrace();
    ////		}
    //	}

}
