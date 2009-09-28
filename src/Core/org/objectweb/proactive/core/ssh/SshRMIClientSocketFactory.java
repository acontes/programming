package org.objectweb.proactive.core.ssh;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;


public class SshRMIClientSocketFactory implements RMIClientSocketFactory, Serializable {

    static SshConfigStorer storer = new SshConfigStorer();
    static SshConfigFileParser parser = new SshConfigFileParser(storer);

    static final private SshTunnelPool tunnelPool = new SshTunnelPool(storer);

    public SshRMIClientSocketFactory(SshConfig config) {

    }

    public Socket createSocket(String host, int port) throws IOException {
        return tunnelPool.getSocket(host, port);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass());
    }
}
