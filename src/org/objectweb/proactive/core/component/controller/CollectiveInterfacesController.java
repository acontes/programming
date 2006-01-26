package org.objectweb.proactive.core.component.controller;

import java.util.List;
import java.util.Map;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.proactive.core.component.ProActiveInterface;
import org.objectweb.proactive.core.component.config.CollectiveInterfacePolicy;
import org.objectweb.proactive.core.component.exceptions.ParameterDispatchException;
import org.objectweb.proactive.core.group.ProxyForComponentInterfaceGroup;
import org.objectweb.proactive.core.mop.MethodCall;

public interface CollectiveInterfacesController {
    
//    public void addInterface(String itfName) throws NoSuchInterfaceException;
    
    public void specifyPolicy(String itfName, CollectiveInterfacePolicy policy);
    
    public Map<MethodCall, Integer> generateMethodCallsForDelegatee(MethodCall mc, ProxyForComponentInterfaceGroup delegatee) throws ParameterDispatchException;
    
    public void checkCompatibility(String itfName, ProActiveInterface itf) throws IllegalBindingException;
    
    public void bindFc(String clientItfName, ProActiveInterface serverItf);
    
    public void unbindFc(String itfName, ProActiveInterface itfRef);
    
    public ProxyForComponentInterfaceGroup lookupFcMulticast(String clientItfName);


    
}
