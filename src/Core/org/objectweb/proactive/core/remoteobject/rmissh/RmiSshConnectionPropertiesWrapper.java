package org.objectweb.proactive.core.remoteobject.rmissh;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.remoteobject.InternalRemoteRemoteObject;
import org.objectweb.proactive.core.remoteobject.RemoteRemoteObject;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;
import org.objectweb.proactive.core.util.GatewaysInfos;


public class RmiSshConnectionPropertiesWrapper implements RemoteRemoteObject, Serializable{

    protected String properties;
    final private RemoteRemoteObject rro;
    
    public RmiSshConnectionPropertiesWrapper(RemoteRemoteObject rro) {
        this.rro = rro;
        this.properties = PAProperties.PA_SSH_PROXY_GATEWAY.getValue();
    }

    @Override   
    public Reply receiveMessage(Request message) throws ProActiveException, IOException,
            RenegotiateSessionException {
        GatewaysInfos gatewaysInfos = GatewaysInfos.getInstance();
        gatewaysInfos.addProperties(properties);
           return rro.receiveMessage(message);
    }

    public String getProperties(){
        return this.properties;
    }
}
