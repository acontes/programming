package org.objectweb.proactive.core.remoteobject.rmissl;

import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.objectweb.proactive.core.Constants;
import org.objectweb.proactive.core.remoteobject.rmi.AbstractRmiRemoteObjectFactory;


public class RmiSslRemoteObjectFactory extends AbstractRmiRemoteObjectFactory {

    public RmiSslRemoteObjectFactory() {
        super(Constants.RMISSL_PROTOCOL_IDENTIFIER, RmiSslRemoteObjectImpl.class);
    }

    protected Registry getRegistry(URI url) throws RemoteException {
        return LocateRegistry.getRegistry(url.getHost(), url.getPort());
    }
}