package org.objectweb.proactive.core.ssh;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;


public class SshRMIClientSocketFactory implements RMIClientSocketFactory, Serializable {

    static final private SshTunnelPool tunnelPool = new SshTunnelPool(new SshConfig());

    public SshRMIClientSocketFactory(SshConfig config) {

    }

    public Socket createSocket(String host, int port) throws IOException {
        return tunnelPool.getSocket(host, port);
    }
}
