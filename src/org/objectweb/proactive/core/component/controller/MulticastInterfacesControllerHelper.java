package org.objectweb.proactive.core.component.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.ProActiveInterface;
import org.objectweb.proactive.core.component.type.ProActiveInterfaceType;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.group.ProActiveComponentGroup;
import org.objectweb.proactive.core.group.ProxyForComponentInterfaceGroup;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.mop.Proxy;
import org.objectweb.proactive.core.mop.StubObject;


public class MulticastInterfacesControllerHelper {
    private Map clientSideProxies = new HashMap();
    private Component owner;

    public MulticastInterfacesControllerHelper(Component owner) {
        this.owner = owner;
        InterfaceType[] itfTypes = ((ComponentType)owner.getFcType()).getFcInterfaceTypes();
        for (int i = 0; i < itfTypes.length; i++) {
			ProActiveInterfaceType type = (ProActiveInterfaceType)itfTypes[i];
			if (type.isFcMulticastItf()) {
                try {
					addClientSideProxy(type.getFcItfName(),(ProActiveInterface) owner.getFcInterface(type.getFcItfName()));
				} catch (NoSuchInterfaceException e) {
					throw new ProActiveRuntimeException(e);
				}
            }
        }
    }

    public void bindFc(String clientItfName, ProActiveInterface serverItf) {

        try {
            ProxyForComponentInterfaceGroup clientSideProxy = (ProxyForComponentInterfaceGroup) clientSideProxies
                                                              .get(clientItfName);

            if (clientSideProxy.getDelegatee() == null) {
                ProActiveInterface groupItf = ProActiveComponentGroup
                                              .newComponentInterfaceGroup((ProActiveInterfaceType) serverItf
                                                                          .getFcItfType(),
                                                                          owner);
                ProxyForComponentInterfaceGroup proxy = (ProxyForComponentInterfaceGroup) ((StubObject) groupItf)
                                                        .getProxy();
                clientSideProxy.setDelegatee(proxy);
            }

            ((Group) clientSideProxy.getDelegatee()).add(serverItf);
        } catch (ClassNotReifiableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean hasClientSideProxy(String itfName) {
        return clientSideProxies.containsKey(itfName);
    }

    private void addClientSideProxy(String itfName, ProActiveInterface itf) {
        Proxy proxy = ((ProActiveInterface)itf.getFcItfImpl()).getProxy();
        
        if (!(proxy instanceof Group)) {
            throw new ProActiveRuntimeException("client side proxies for multicast interfaces must be Group instances");
        }

        clientSideProxies.put(itfName, proxy);
    }
}
