package org.objectweb.proactive.core.group;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.body.LocalBodyStore;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.exceptions.ParameterDispatchException;
import org.objectweb.proactive.core.component.type.ProActiveInterfaceType;
import org.objectweb.proactive.core.component.type.ProActiveInterfaceTypeImpl;
import org.objectweb.proactive.core.exceptions.manager.NFEManager;
import org.objectweb.proactive.core.exceptions.proxy.FailedGroupRendezVousException;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.mop.ConstructionOfReifiedObjectFailedException;
import org.objectweb.proactive.core.mop.ConstructorCall;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.Proxy;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.util.profiling.Profiling;


public class ProxyForComponentInterfaceGroup extends ProxyForGroup {
    protected ProActiveInterfaceType interfaceType;
    protected Component owner;
    protected ProxyForComponentInterfaceGroup delegatee = null;

    
    /**
     * @return Returns the interfaceType.
     */
    public ProActiveInterfaceType getInterfaceType() {
    
        return interfaceType;
    }

    public ProxyForComponentInterfaceGroup() throws ConstructionOfReifiedObjectFailedException {
        super();
        className = Interface.class.getName();
    }

    public ProxyForComponentInterfaceGroup(ConstructorCall c, Object[] p)
        throws ConstructionOfReifiedObjectFailedException {
        super(c, p);
        className = Interface.class.getName();
    }

    public ProxyForComponentInterfaceGroup(String nameOfClass)
        throws ConstructionOfReifiedObjectFailedException {
        this();
        className = Interface.class.getName();
    }

    /*
     * @see org.objectweb.proactive.core.group.Group#getGroupByType()
     */
    public Object getGroupByType() {

        try {
            Interface result = ProActiveComponentGroup.newComponentInterfaceGroup(
                        interfaceType,
                        owner);

            ProxyForComponentInterfaceGroup proxy = (ProxyForComponentInterfaceGroup) ((StubObject) result)
                .getProxy();
            proxy.memberList = this.memberList;
            proxy.className = this.className;
            proxy.interfaceType = this.interfaceType;
            proxy.owner = this.owner;
            proxy.proxyForGroupID = this.proxyForGroupID;
            proxy.waited = this.waited;
            return result;
        } catch (ClassNotReifiableException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    //    public synchronized Object reify(MethodCall mc) throws InvocationTargetException {
    //        if (((ProActiveInterfaceTypeImpl) interfaceType).isFcCollective() && (delegatee != null)) {
    //            try {
    //                Object result = delegateInvocation(mc);
    //                result = ProActiveGroup.getGroup(result);
    //                return result;
    //                //                return delegateInvocation(mc);
    //            } catch (Throwable e) {
    //                throw new InvocationTargetException(e);
    //            }
    //        } else {
    //            return super.reify(mc);
    //        }
    //    }

    /*
     * @see org.objectweb.proactive.core.group.ProxyForGroup#asynchronousCallOnGroup(org.objectweb.proactive.core.mop.MethodCall)
     */
    @Override
    protected Object asynchronousCallOnGroup(MethodCall mc)
        throws InvocationTargetException {

        if (((ProActiveInterfaceTypeImpl) interfaceType).isFcCollective()) {
            if (delegatee != null) {
            Object result;
            Body body = ProActive.getBodyOnThis();

            // Creates a stub + ProxyForGroup for representing the result
            try {
                Object[] paramProxy = new Object[0];

                // create a result group of the type of the adapted mc
                if (!(mc.getReifiedMethod().getGenericReturnType() instanceof ParameterizedType)) {
                    throw new ProActiveRuntimeException(
                            "all methods in multicast interfaces must return parameterized lists, "
                            + "which is not the case for method "
                            + mc.getReifiedMethod().toString());
                }

                Class returnTypeForGroup = (Class) ((ParameterizedType) mc.getReifiedMethod()
                                                                          .getGenericReturnType())
                    .getActualTypeArguments()[0];
                result = MOP.newInstance(
                            returnTypeForGroup.getName(),
                            null,
                            ProxyForGroup.class.getName(),
                            paramProxy);
                ((ProxyForGroup) ((StubObject) result).getProxy()).className = returnTypeForGroup
                    .getName();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            Map<MethodCall, Integer> generatedMethodCalls;

            try {
                generatedMethodCalls = Fractive.getCollectiveInterfacesController(owner)
                                               .generateMethodCallsForDelegatee(mc, delegatee);
            } catch (NoSuchInterfaceException e) {
                e.printStackTrace();
                throw new InvocationTargetException(
                        e,
                        "missing collective interfaces controller for collective interface "
                        + interfaceType.getFcItfName());
            } catch (ParameterDispatchException e) {
                throw new InvocationTargetException(
                        e,
                        "cannot dispatch invocation parameters for method "
                        + mc.getReifiedMethod().toString() + " from collective interface "
                        + interfaceType.getFcItfName());
            }

            // Init the lists of result with null value to permit the "set(index)" operation
            Vector memberListOfResultGroup = ((ProxyForGroup) ((StubObject) result).getProxy()).memberList;

            // there are as many results expected as there are method invocations
            for (int i = 0; i < generatedMethodCalls.size(); i++) {
                memberListOfResultGroup.add(null);
            }

            for (MethodCall currentMc : generatedMethodCalls.keySet()) {
                // delegate invocations
                this.threadpool.addAJob(
                        new ProcessForAsyncCall(
                                delegatee,
                                delegatee.memberList,
                                memberListOfResultGroup,
                                generatedMethodCalls.get(currentMc),
                                currentMc,
                                body));
            }

            LocalBodyStore.getInstance().setCurrentThreadBody(body);
            return result;
            } else {
                Thread.dumpStack();
                return null;
            }
        } else {
            return super.asynchronousCallOnGroup(mc);
        }
    }

    /*
     * @see org.objectweb.proactive.core.group.ProxyForGroup#oneWayCallOnGroup(org.objectweb.proactive.core.mop.MethodCall, org.objectweb.proactive.core.group.ExceptionListException)
     */
    @Override
    protected void oneWayCallOnGroup(MethodCall mc, ExceptionListException exceptionList)
        throws InvocationTargetException {

        if (((ProActiveInterfaceTypeImpl) interfaceType).isFcCollective() && (delegatee != null)) {
            // 2. generate adapted method calls depending on nb members and parameters distribution
            // each method call is assigned a given member index
            Body body = ProActive.getBodyOnThis();

            Map<MethodCall, Integer> generatedMethodCalls;

            try {
                generatedMethodCalls = Fractive.getCollectiveInterfacesController(owner)
                                               .generateMethodCallsForDelegatee(mc, delegatee);
            } catch (NoSuchInterfaceException e) {
                e.printStackTrace();
                throw new InvocationTargetException(
                        e,
                        "missing collective interfaces controller for collective interface "
                        + interfaceType.getFcItfName());
            } catch (ParameterDispatchException e) {
                throw new InvocationTargetException(
                        e,
                        "cannot dispatch invocation parameters for method "
                        + mc.getReifiedMethod().toString() + " from collective interface "
                        + interfaceType.getFcItfName());
            }

            for (MethodCall currentMc : generatedMethodCalls.keySet()) {
                // delegate invocations
                this.threadpool.addAJob(
                        new ProcessForOneWayCall(
                                delegatee,
                                delegatee.memberList,
                                generatedMethodCalls.get(currentMc),
                                currentMc,
                                body,
                                exceptionList));
            }

            LocalBodyStore.getInstance().setCurrentThreadBody(body);
        }

        // TODO Auto-generated method stub
        super.oneWayCallOnGroup(mc, exceptionList);
    }

    public void setDelegatee(ProxyForComponentInterfaceGroup delegatee) {
        this.delegatee = delegatee;
    }

    public ProxyForComponentInterfaceGroup getDelegatee() {
        return delegatee;
    }

    /*
     * @see org.objectweb.proactive.core.group.ProxyForGroup#size()
     */
    @Override
    public int size() {

        if (getDelegatee()!=null) {
            return getDelegatee().size();
        } 
        return super.size();
    }
    
    
}
