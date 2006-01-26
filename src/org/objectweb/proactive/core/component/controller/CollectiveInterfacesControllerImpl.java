package org.objectweb.proactive.core.component.controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ProActiveInterface;
import org.objectweb.proactive.core.component.config.BroadcastCollectiveInterfacePolicy;
import org.objectweb.proactive.core.component.config.CollectiveInterfacePolicy;
import org.objectweb.proactive.core.component.exceptions.ParameterDispatchException;
import org.objectweb.proactive.core.component.type.ProActiveInterfaceType;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactoryImpl;
import org.objectweb.proactive.core.component.type.annotations.collective.ParamDispatch;
import org.objectweb.proactive.core.group.ProActiveGroup;
import org.objectweb.proactive.core.group.ProxyForComponentInterfaceGroup;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class CollectiveInterfacesControllerImpl extends AbstractProActiveController
    implements CollectiveInterfacesController {
    private static Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_CONTROLLERS);
    private static Logger multicastLogger = ProActiveLogger.getLogger(Loggers.COMPONENTS_MULTICAST);
    private Map<String, ProActiveInterface> multicastItfs = new HashMap<String, ProActiveInterface>();
    private Map<String, ProActiveInterface> gathercastItfs = new HashMap<String, ProActiveInterface>();
    
    private Map<String, Map<Method, Method>> matchingMethods = new HashMap<String, Map<Method,Method>>();

    //    private Map collectiveItfs = new HashMap();
    private Map<String, CollectiveInterfacePolicy> policies = new HashMap<String, CollectiveInterfacePolicy>();
    private MulticastInterfacesControllerHelper multicastController;

    public CollectiveInterfacesControllerImpl(Component owner) {
        super(owner);
    }

    private void init() {
        // this can only be done once the component is fully instantiated with all its interfaces created
        if (multicastController == null) {
            multicastController = new MulticastInterfacesControllerHelper(owner);
            List<Object> interfaces = Arrays.asList(owner.getFcInterfaces());
            Iterator<Object> it = interfaces.iterator();

            while (it.hasNext()) {
                addManagedInterface((ProActiveInterface) it.next());
            }
        }
    }

    protected void setControllerItfType() {
        try {
            setItfType(
                ProActiveTypeFactoryImpl.instance().createFcItfType(
                    Constants.COLLECTIVE_INTERFACES_CONTROLLER,
                    CollectiveInterfacesController.class.getName(),
                    TypeFactory.SERVER,
                    TypeFactory.MANDATORY,
                    TypeFactory.SINGLE));
        } catch (InstantiationException e) {
            throw new ProActiveRuntimeException("cannot create controller type for controller " +
                this.getClass().getName());
        }
    }

    /*
     * @see org.objectweb.proactive.core.component.controller.CollectiveInterfacesController#checkCompatibility(java.lang.String, org.objectweb.proactive.core.component.ProActiveInterface)
     */
    public void checkCompatibility(String itfName, ProActiveInterface itf)
        throws IllegalBindingException {
        init();

        checkCompatibility((ProActiveInterfaceType) (getCollectiveItf(itfName)).getFcItfType(),
            (ProActiveInterfaceType) itf.getFcItfType());
    }

    /**
     * client and server interfaces must have the same methods, except that
     * the client methods always returns a java.util.List, whereas
     * the server methods may return any type.
     * <br>
     * For java 1.5 this could be enhanced with generics
     *
     * @see org.objectweb.proactive.core.component.config.CollectiveInterfacePolicy#checkCompatibility(org.objectweb.proactive.core.component.type.ProActiveInterfaceType, org.objectweb.proactive.core.component.type.ProActiveInterfaceType)
     */
    private void checkCompatibility(ProActiveInterfaceType clientSideItfType,
        ProActiveInterfaceType serverSideItfType) throws IllegalBindingException {
        try {
            Class clientSideItfClass;
            clientSideItfClass = Class.forName(clientSideItfType.getFcItfSignature());
            Class serverSideItfClass = Class.forName(serverSideItfType.getFcItfSignature());


            Method[] clientSideItfMethods = clientSideItfClass.getMethods();
            Method[] serverSideItfMethods = serverSideItfClass.getMethods();

            if (clientSideItfMethods.length != serverSideItfMethods.length) {
                throw new IllegalBindingException("incompatible binding between multicast client interface " + clientSideItfType.getFcItfName() + " (" + clientSideItfType.getFcItfSignature() + ")  and server interface " + serverSideItfType.getFcItfName() + " ("+serverSideItfType.getFcItfSignature()+") : there is not the same number of methods (including those inherited) in both interfaces !");
            }

            Map<Method, Method> matchingMethodsForThisItf = new HashMap<Method, Method>(clientSideItfMethods.length);

            for (Method method : clientSideItfMethods) {
                try {
                    matchingMethodsForThisItf.put(method, MulticastBindingChecker.searchMatchingMethod(method, serverSideItfMethods));
                } catch (ParameterDispatchException e) {
                    throw new IllegalBindingException("binding incompatibility between " + clientSideItfType.getFcItfName() + " and " + serverSideItfType.getFcItfName() + " : " + e.getMessage());
                } catch (NoSuchMethodException e) {
                    throw new IllegalBindingException("binding incompatibility between " + clientSideItfType.getFcItfName() + " and " + serverSideItfType.getFcItfName() + " : " + e.getMessage());
                }
            }

            matchingMethods.put(clientSideItfType.getFcItfName(), matchingMethodsForThisItf);
        } catch (ClassNotFoundException e) {
            throw new IllegalBindingException("cannot find class corresponding to given signature " +
                e.getMessage());
        }
    }


    private boolean addManagedInterface(ProActiveInterface itf) {
        if (multicastItfs.containsKey(itf.getFcItfName()) ||
                gathercastItfs.containsKey(itf.getFcItfName())) {
            logger.error("the interface named " + itf.getFcItfName() +
                " is already managed by the collective interfaces controller");
            return false;
        }

        ProActiveInterfaceType itfType = (ProActiveInterfaceType) itf.getFcItfType();

        if (itfType.isFcMulticastItf()) {
            multicastItfs.put(
                itf.getFcItfName(),
                itf);
            specifyPolicy(
                itf.getFcItfName(),
                new BroadcastCollectiveInterfacePolicy());
        } else if (itfType.isFcGatherCastItf()) {
            gathercastItfs.put(
                itf.getFcItfName(),
                itf);
        } else {
            logger.error("the interface named " + itf.getFcItfName() +
                " cannot be managed by this collective interfaces controller");
            return false;
        }

        return true;
    }

    public Map<MethodCall, Integer> generateMethodCallsForDelegatee(MethodCall mc,
        ProxyForComponentInterfaceGroup delegatee) throws ParameterDispatchException {
        // read from annotations
        Object[] clientSideEffectiveArguments = mc.getEffectiveArguments();

        ProActiveInterfaceType itfType = (ProActiveInterfaceType) getCollectiveItf(
                mc.getComponentInterfaceName()).getFcItfType();

        Method matchingMethodInClientInterface;

        try {
            matchingMethodInClientInterface = Class.forName(itfType.getFcItfSignature()).getDeclaredMethod(
                    mc.getReifiedMethod().getName(),
                    mc.getReifiedMethod().getParameterTypes());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParameterDispatchException(e.fillInStackTrace());
        }

        Class[] clientSideParamTypes = matchingMethodInClientInterface.getParameterTypes();
        ParamDispatch[] clientSideParamDispatchModes = MulticastBindingChecker.getDispatchModes(matchingMethodInClientInterface);

        List<Map<Integer, Object>> dispatchedParameters = new ArrayList<Map<Integer, Object>>();

        int expectedMethodCallsNb = 0;

        // compute dispatch sizes for annotated parameters
        Vector<Integer> dispatchSizes = new Vector<Integer>();

        for (int i = 0; i < clientSideParamTypes.length; i++) {
                    dispatchSizes.addElement(
                        clientSideParamDispatchModes[i].expectedDispatchSize(
                            clientSideEffectiveArguments[i],
                            delegatee.size()));
                }
            
        

        if (dispatchSizes.size() > 0) {
            // ok, found some annotated elements
            expectedMethodCallsNb = dispatchSizes.get(0);

            for (int i = 1; i < dispatchSizes.size(); i++) {
                if (dispatchSizes.get(i).intValue() != expectedMethodCallsNb) {
                    throw new ParameterDispatchException(
                        "cannot generate invocation for multicast interface " +
                        itfType.getFcItfName() +
                        "because the specified distribution of parameters is incorrect in method " +
                        matchingMethodInClientInterface.getName());
                }
            }
        }

        // get distributed parameters
        for (int i = 0; i < clientSideParamTypes.length; i++) {
            
            Map<Integer, Object> dispatchedParameter = clientSideParamDispatchModes[i].dispatch(
                    clientSideEffectiveArguments[i],
                    delegatee.size());
            dispatchedParameters.add(dispatchedParameter);
        }

        Map<MethodCall, Integer> result = new HashMap<MethodCall, Integer>(expectedMethodCallsNb);

        // need to find matching method in server interface
        try {
            
            Method matchingMethodInServerInterface = matchingMethods.get(mc.getComponentInterfaceName()).get(mc.getReifiedMethod());
            
            // now we have all dispatched parameters
            // proceed to generation of method calls
            for (int generatedMethodCallIndex = 0;
                    generatedMethodCallIndex < expectedMethodCallsNb; generatedMethodCallIndex++) {
                Object[] individualEffectiveArguments = new Object[matchingMethodInServerInterface.getParameterTypes().length];

                for (int parameterIndex = 0; parameterIndex < individualEffectiveArguments.length;
                        parameterIndex++) {
                    individualEffectiveArguments[parameterIndex] = dispatchedParameters.get(parameterIndex)
                                                                                       .get(generatedMethodCallIndex); // initialize 
                }

                result.put(
                    new MethodCall(
                        matchingMethodInServerInterface,
                        individualEffectiveArguments,
                        mc.getMetadata()),
                    generatedMethodCallIndex % delegatee.size());
                // default is to do some round robin when nbGeneratedMethodCalls > nbReceivers
            }
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    public void specifyPolicy(String itfName, CollectiveInterfacePolicy policy) {
        if (policies.containsKey(itfName)) {
            logger.warn("policy for collective interface " + itfName + " will be overriden");
        }

        policies.put(itfName, policy);
    }

    /*
     * @see org.objectweb.proactive.core.component.controller.CollectiveInterfacesController#bindFc(java.lang.String, org.objectweb.proactive.core.component.ProActiveInterface)
     */
    public void bindFc(String clientItfName, ProActiveInterface serverItf) {
        if (logger.isDebugEnabled()) {
            try {
                logger.debug("multicast binding : " + clientItfName + " to : " + Fractal.getNameController(serverItf.getFcItfOwner()).getFcName() + "." + serverItf.getFcItfName());
            } catch (NoSuchInterfaceException e) {
                e.printStackTrace();
            }
        }
        if (multicastItfs.containsKey(clientItfName)) {
            this.multicastController.bindFc(clientItfName, serverItf);
        }

        policies.get(clientItfName)
                .setServerItfSignature(
            ((InterfaceType) serverItf.getFcItfType()).getFcItfSignature());

        // TODO_M gathercast itfs
    }

    /*
     * @see org.objectweb.proactive.core.component.controller.CollectiveInterfacesController#unbindFc(java.lang.String, org.objectweb.proactive.core.component.ProActiveInterface)
     */
    public void unbindFc(String clientItfName, ProActiveInterface serverItf) {
        if (multicastItfs.containsKey(clientItfName)) {
            if (ProActiveGroup.getGroup(multicastItfs.get(clientItfName)).remove(serverItf)) {
                logger.debug("removed connected interface from multicast interface : " +
                    clientItfName);
            } else {
                logger.error("cannot remove connected interface from multicast interface : " +
                    clientItfName);
            }
        }
    }

    /*
     * @see org.objectweb.proactive.core.component.controller.CollectiveInterfacesController#lookupFc(java.lang.String)
     */
    public ProxyForComponentInterfaceGroup lookupFcMulticast(String clientItfName) {
        if (multicastItfs.containsKey(clientItfName)) {
            return (ProxyForComponentInterfaceGroup) ((ProActiveInterface) multicastItfs.get(clientItfName)
                                                                                        .getFcItfImpl()).getProxy();
        } else {
            return null;
        }
    }

    private ProActiveInterface getCollectiveItf(String itfName) {
        ProActiveInterface itf = null;
        itf = multicastItfs.get(itfName);

        if (itf != null) {
            return itf;
        }

        itf = gathercastItfs.get(itfName);
        return itf;
    }

}
