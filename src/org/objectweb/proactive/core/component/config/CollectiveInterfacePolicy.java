package org.objectweb.proactive.core.component.config;

import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.proactive.core.component.ProActiveInterface;
import org.objectweb.proactive.core.component.type.ProActiveInterfaceType;
import org.objectweb.proactive.core.group.ProxyForComponentInterfaceGroup;
import org.objectweb.proactive.core.mop.MethodCall;

public interface CollectiveInterfacePolicy {

    public abstract MethodCall adaptAndDistribute(MethodCall mc, ProxyForComponentInterfaceGroup delegatee);
    
    public abstract MethodCall distributeParameters(MethodCall mc, ProxyForComponentInterfaceGroup delegatee);
    
    public boolean checkCompatibility(ProActiveInterfaceType clientSideItfType, ProActiveInterfaceType serverSideItfType) throws IllegalBindingException;
    
    public void setServerItfSignature(String serverItfSignature);
    

}