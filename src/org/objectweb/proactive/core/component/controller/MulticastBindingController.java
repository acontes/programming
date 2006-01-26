package org.objectweb.proactive.core.component.controller;

import java.util.List;

import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.component.ProActiveInterface;

public interface MulticastBindingController  {
    
    public Object getMulticastFcItfRef(String itfName) throws NoSuchInterfaceException;
    
    public void setMulticastFcItfRef(String itfName, Object itfRef);
    
}
