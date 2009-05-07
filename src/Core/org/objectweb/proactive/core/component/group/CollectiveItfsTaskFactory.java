/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
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
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.component.group;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.component.collectiveitfs.MulticastHelper;
import org.objectweb.proactive.core.component.exceptions.ParameterDispatchException;
import org.objectweb.proactive.core.component.identity.ProActiveComponent;
import org.objectweb.proactive.core.component.type.ProActiveInterfaceTypeImpl;
import org.objectweb.proactive.core.group.AbstractProcessForGroup;
import org.objectweb.proactive.core.group.BasicTaskFactory;
import org.objectweb.proactive.core.group.Dispatch;
import org.objectweb.proactive.core.group.DispatchMode;
import org.objectweb.proactive.core.group.ExceptionListException;
import org.objectweb.proactive.core.group.ProxyForGroup;
import org.objectweb.proactive.core.mop.MethodCall;


public class CollectiveItfsTaskFactory extends BasicTaskFactory {

    public CollectiveItfsTaskFactory(ProxyForGroup groupProxy) {
        super(groupProxy);
    }

    @Override
    public List<MethodCall> generateMethodCalls(MethodCall mc) throws InvocationTargetException {
        ProxyForComponentInterfaceGroup parent = ((ProxyForComponentInterfaceGroup) groupProxy).getParent();
        if (parent != null && (((ProActiveInterfaceTypeImpl) parent.getInterfaceType()).isFcCollective())) {
            // ok we are dealing with a delegation proxy for a collective
            // interface
            // use helper class
            try {
                List<MethodCall> methodCalls = MulticastHelper.generateMethodCallsForMulticastDelegatee(
                        (ProActiveComponent) parent.getOwner(), mc,
                        (ProxyForComponentInterfaceGroup) groupProxy);
                return methodCalls;
            } catch (ParameterDispatchException e) {
                e.printStackTrace();
                throw new InvocationTargetException(e);
            }

        } else {
            return super.generateMethodCalls(mc);
        }
    }

    public Queue<AbstractProcessForGroup> generateTasks(MethodCall originalMethodCall,
            List<MethodCall> methodCalls, Object result, ExceptionListException exceptionList,
            CountDownLatch doneSignal, ProxyForGroup<?> groupProxy) {

        Queue<AbstractProcessForGroup> taskList = new ConcurrentLinkedQueue<AbstractProcessForGroup>();

        // not a broadcast: use generated method calls
        Vector<Object> memberListOfResultGroup = null;

        // if dynamic dispatch or random distribution, randomly distribute
        // tasks
        // reorder(methodCalls, originalMethodCall.getReifiedMethod());

        List<Integer> taskIndexes = getTaskIndexes(originalMethodCall, methodCalls, groupProxy
                .getMemberList().size());
        if (!(result == null)) {
            memberListOfResultGroup = initializeResultsGroup(result, methodCalls.size());
        }

        for (int i = 0; i < methodCalls.size(); i++) {
            MethodCall mc = methodCalls.get(i);
            AbstractProcessForGroup task = useOneWayProcess(mc) ? new ComponentProcessForOneWayCall(
                groupProxy, groupProxy.getMemberList(),
                getTaskIndex(mc, i, groupProxy.getMemberList().size()), mc, PAActiveObject.getBodyOnThis(),
                exceptionList, doneSignal)

            : new ComponentProcessForAsyncCall(groupProxy, groupProxy.getMemberList(),
                memberListOfResultGroup, taskIndexes.get(i), mc, i, PAActiveObject.getBodyOnThis(),
                doneSignal);

            setDynamicDispatchTag(task, originalMethodCall);
            taskList.offer(task);
            //          System.out.println("*** worker index = [" + i
            //                  % groupProxy.getMemberList().size() + "]");
        }
        return taskList;
    }

    @Override
    public int getTaskIndex(MethodCall mc, int partitioningIndex, int groupSize) {
        return super.getTaskIndex(mc, partitioningIndex, groupSize);
    }

    public void setDynamicDispatchTag(AbstractProcessForGroup task, MethodCall originalMethodCall) {
        // knowledge based means dynamic dispatch
        // info specified through proxy API has priority

        Dispatch balancingModeAnnotation = originalMethodCall.getReifiedMethod()
                .getAnnotation(Dispatch.class);
        if (balancingModeAnnotation != null) {
            task.setDynamicallyDispatchable(balancingModeAnnotation.mode().equals(DispatchMode.DYNAMIC));
        }
    }

    //	@Override
    //	public void setDynamicDispatchTag(AbstractProcessForGroup task,
    //			MethodCall mc) {
    //		// defined by a specific annotation
    //		task.setDynamicallyDispatchable(MulticastHelper.dynamicDispatch(mc));
    //	}
}
