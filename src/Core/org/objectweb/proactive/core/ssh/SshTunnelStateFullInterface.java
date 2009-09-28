package org.objectweb.proactive.core.ssh;

import java.io.IOException;
import java.net.Socket;


public interface SshTunnelStateFullInterface {

    public Socket getSocket() throws IOException;

    public String getDistantHost();

    public int getDistantPort();

    public long unusedSince();

    public void close() throws IOException;
}
