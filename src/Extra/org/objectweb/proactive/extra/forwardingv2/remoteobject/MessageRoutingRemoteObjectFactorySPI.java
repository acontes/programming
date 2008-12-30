package org.objectweb.proactive.extra.forwardingv2.remoteobject;

import org.objectweb.proactive.core.remoteobject.RemoteObjectFactory;
import org.objectweb.proactive.core.remoteobject.RemoteObjectFactorySPI;


public class MessageRoutingRemoteObjectFactorySPI implements RemoteObjectFactorySPI {

    public Class<? extends RemoteObjectFactory> getFactoryClass() {
        return MessageRoutingRemoteObjectFactory.class;
    }

    public String getProtocolId() {
        return "pamr";
    }

}
