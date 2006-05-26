package org.objectweb.proactive.core.component.controller;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.proactive.core.component.ProActiveInterface;
import org.objectweb.proactive.core.component.type.ProActiveInterfaceType;
/**
 * Abstract parent class for controllers of collective interfaces
 *  
 * @author Matthieu Morel
 *
 */
public abstract class AbstractCollectiveInterfaceController extends AbstractProActiveController {
    
    Map<String, Map<Method, Method>> matchingMethods = new HashMap<String, Map<Method,Method>>();


    public AbstractCollectiveInterfaceController(Component owner) {
        super(owner);
        // TODO Auto-generated constructor stub
    }
    
    public void checkCompatibility(String itfName, ProActiveInterface itf) throws IllegalBindingException {
        try {
            
            ProActiveInterfaceType clientItfType = (ProActiveInterfaceType)((ComponentType)owner.getFcType()).getFcInterfaceType(itfName);
            
            checkCompatibility(clientItfType,
                (ProActiveInterfaceType) itf.getFcItfType());
        } catch (NoSuchInterfaceException e) {
            throw new IllegalBindingException("cannot find client interface " + itfName);
        }
    }
    
    protected abstract Method searchMatchingMethod(Method clientSideMethod, Method[] serverSideMethods);

    /**
     * client and server interfaces must have the same methods, except that
     * the client methods always returns a java.util.List<E>, whereas
     * the server methods return E. (for multicast interfaces)
     * <br>
     *
     */
    void checkCompatibility(ProActiveInterfaceType clientSideItfType, ProActiveInterfaceType serverSideItfType) throws IllegalBindingException {
        try {
            Class clientSideItfClass;
            clientSideItfClass = Class.forName(clientSideItfType.getFcItfSignature());
            Class serverSideItfClass = Class.forName(serverSideItfType.getFcItfSignature());
    
    
            Method[] clientSideItfMethods = clientSideItfClass.getMethods();
            Method[] serverSideItfMethods = serverSideItfClass.getMethods();
    
            if (clientSideItfMethods.length != serverSideItfMethods.length) {
                throw new IllegalBindingException("incompatible binding between client interface " + clientSideItfType.getFcItfName() + " (" + clientSideItfType.getFcItfSignature() + ")  and server interface " + serverSideItfType.getFcItfName() + " ("+serverSideItfType.getFcItfSignature()+") : there is not the same number of methods (including those inherited) in both interfaces !");
            }
    
            Map<Method, Method> matchingMethodsForThisItf = new HashMap<Method, Method>(clientSideItfMethods.length);
    
            for (Method method : clientSideItfMethods) {
                    Method serverSideMatchingMethod = searchMatchingMethod(method, serverSideItfMethods);
                    if (serverSideMatchingMethod == null) {
                        throw new IllegalBindingException("binding incompatibility between " + clientSideItfType.getFcItfName() + " and " + serverSideItfType.getFcItfName() + " : cannot find matching method");
                    }
                    matchingMethodsForThisItf.put(method, serverSideMatchingMethod);
            }
    
            matchingMethods.put(clientSideItfType.getFcItfName(), matchingMethodsForThisItf);
        } catch (ClassNotFoundException e) {
            throw new IllegalBindingException("cannot find class corresponding to given signature " +
                e.getMessage());
        }
    }


}
