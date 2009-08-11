package org.objectweb.proactive.core.remoteobject.rmissl;

import org.objectweb.proactive.core.remoteobject.InternalRemoteRemoteObject;
import org.objectweb.proactive.core.remoteobject.rmi.RmiRemoteObjectImpl;
import org.objectweb.proactive.core.ssl.rmissl.SslRmiClientSocketFactory;
import org.objectweb.proactive.core.ssl.rmissl.SslRmiServerSocketFactory;


public class RmiSslRemoteObjectImpl extends RmiRemoteObjectImpl {

    public RmiSslRemoteObjectImpl(InternalRemoteRemoteObject target) throws java.rmi.RemoteException {
        super(target, new SslRmiServerSocketFactory(), new SslRmiClientSocketFactory());
    }
}