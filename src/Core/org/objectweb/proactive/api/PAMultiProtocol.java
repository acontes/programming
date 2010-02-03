package org.objectweb.proactive.api;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.UniversalBodyRemoteObjectAdapter;
import org.objectweb.proactive.core.body.proxy.UniversalBodyProxy;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.remoteobject.SynchronousProxy;
import org.objectweb.proactive.core.remoteobject.exception.UnknownProtocolException;


@PublicAPI
public class PAMultiProtocol {

    public static void forceProtocol(Object obj, String protocol) throws UnknownProtocolException {
        UniversalBodyProxy ubp = (UniversalBodyProxy) ((StubObject) obj).getProxy();
        UniversalBody ub = ubp.getBody();
        // Object is a stub which point to a remote Body
        if (ub instanceof UniversalBodyRemoteObjectAdapter) {
            UniversalBodyRemoteObjectAdapter ubroa = (UniversalBodyRemoteObjectAdapter) ubp.getBody();
            SynchronousProxy sp = (SynchronousProxy) ((StubObject) ubroa).getProxy();
            sp.forceProtocol(protocol);
            return;
        }

        if (ub instanceof Body) {
            ((AbstractBody) ub).getRemoteObjectExposer().forceProtocol(protocol);
        }

    }

    public static void forceToDefault(Object obj) throws UnknownProtocolException {
        forceProtocol(obj, PAProperties.PA_COMMUNICATION_PROTOCOL.getValue());
    }
}
