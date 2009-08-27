package org.objectweb.proactive.core.remoteobject.rmissh;

import java.io.IOException;
import java.io.Serializable;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.remoteobject.RemoteRemoteObject;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;


public class RmiSshConnectionPropertiesWrapper implements RemoteRemoteObject, Serializable {

    protected String properties;
    final private RemoteRemoteObject rro;

    public RmiSshConnectionPropertiesWrapper(RemoteRemoteObject rro) {
        this.rro = rro;
        this.properties = PAProperties.PA_SSH_PROXY_GATEWAY.getValue();
    }

    public Reply receiveMessage(Request message) throws ProActiveException, IOException,
            RenegotiateSessionException {
        return rro.receiveMessage(message);
    }

    public String getProperties() {
        return this.properties;
    }
}
