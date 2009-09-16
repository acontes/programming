package org.objectweb.proactive.core.ssh;

import java.util.concurrent.atomic.AtomicBoolean;

import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.ssh.SshHelper;


public class SshConfig {
    private final AtomicBoolean readOnly = new AtomicBoolean(false);

    private String username;
    private boolean tryPlainSocket;
    private long gcInterval;
    private long gcIdleTime;
    private int connectTimeout;
    private int port;
    private String knowHostFile;
    private String keyDir;
    private boolean tryProxyCommand;

    public SshConfig() {
        this.username = System.getProperty("user.name");
        this.tryPlainSocket = true;
        this.keyDir = System.getProperty("user.home") + "/.ssh";
        this.port = 22;
        this.gcInterval = 5000;
        this.gcIdleTime = 10000;
        this.tryProxyCommand = PAProperties.PA_RMISSH_TRY_PROXY_COMMAND.isTrue();
    }

    /** Seals the configuration */
    final void setReadOnly() {
        this.readOnly.set(true);
    }

    final private void checkReadOnly() {
        if (this.readOnly.get())
            throw new IllegalStateException(SshConfig.class.getName() +
                " bean is now read only, cannot be modified");
    }

    /** Returns the user name to be used for this host:port */
    final public String getUsername(String host, int port) {
        String uname = SshHelper.getInstance().getGatewayUsername(host);
        if (uname != null)
            return uname;
        return username;
    }

    final public void setUsername(String username) {
        checkReadOnly();
        this.username = username;
    }

    /** Should plain socket be used if direction is possible ? */
    final public boolean tryPlainSocket() {
        return tryPlainSocket;
    }

    final public void setTryPlainSocket(boolean tryNormalFirst) {
        checkReadOnly();
        this.tryPlainSocket = tryNormalFirst;
    }

    /** The amount of time to wait between each garbage collection */
    public long getGcInterval() {
        return gcInterval;
    }

    public void setGcInterval(long gcInterval) {
        checkReadOnly();
        this.gcInterval = gcInterval;
    }

    /** The connect timeout for plain and ssh socket */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        checkReadOnly();
        this.connectTimeout = connectTimeout;
    }

    /** Returns the port on which the SSH server is listening */
    public int getPort(String host) {
        String portt = SshHelper.getInstance().getGatewayUsername(host);
        if (portt != null)
            return Integer.parseInt(portt);
        return port;
    }

    /** Returns the location of the know host file */
    public String getKnowHostFile() {
        return knowHostFile;
    }

    public void setKnowHostFile(String knowHostFile) {
        checkReadOnly();
        this.knowHostFile = knowHostFile;
    }

    /** Returns the location of the .ssh directory */
    public String getKeyDir() {
        return keyDir;
    }

    public void setKeyDir(String keyDir) {
        checkReadOnly();
        this.keyDir = keyDir;
    }

    public String[] getPrivateKeys(String host) {
        // FIXME: Parse the configuration file and remove the getKeyDir() method ?
        return null;
    }

    /** SSH tunnels and connections are garbage collected if unused longer than this amount of time (ms) */
    public long getGcIdleTime() {
        return gcIdleTime;
    }

    public void setGcIdleTime(long gcIdleTime) {
        checkReadOnly();
        this.gcIdleTime = gcIdleTime;
    }

    public boolean tryProxyCommand() {
        return this.tryProxyCommand;
    }
}
