package org.objectweb.proactive.extra.forwardingv2.router;

import java.net.InetAddress;
import java.net.UnknownHostException;


/** A bean for router configuration. */
public class RouterConfig {

    volatile private boolean readyOnly;

    private int port;

    private boolean isDaemon;

    private int nbWorkerThreads;

    private InetAddress inetAddress;

    public RouterConfig() {
        this.port = 0;
        this.isDaemon = false;
        this.nbWorkerThreads = 4;
        try {
            this.inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            this.inetAddress = null;
        }
    }

    public void setReadOnly() {
        this.readyOnly = true;
    }

    private void checkReadOnly() {
        if (this.readyOnly)
            throw new IllegalStateException(RouterConfig.class.getName() + "beans is read only");
    }

    int getPort() {
        return port;
    }

    /** The port on which the server will bind 
     * 
     * If 0 the router will bind to a random free port
     * 
     * @throws IllegalArgumentException if the port number is invalid
     */
    public void setPort(int port) {
        checkReadOnly();

        if (port < 0 || port > 65535)
            throw new IllegalArgumentException("port must be between 0 and 65535");

        this.port = port;
    }

    boolean isDaemon() {
        return isDaemon;
    }

    /** Set if the router is a daemon thread */
    public void setDaemon(boolean isDaemon) {
        checkReadOnly();
        this.isDaemon = isDaemon;
    }

    int getNbWorkerThreads() {
        return nbWorkerThreads;
    }

    /** Set the number of worker threads
     * 
     * Each received message is handled asynchronously by a pool of workers. 
     * Increasing the amount of worker will increase the parallelism of message
     * handling and sending. 
     * 
     * Incoming messages are read by a single thread.
     * 
     */
    public void setNbWorkerThreads(int nbWorkerThreads) {
        checkReadOnly();
        this.nbWorkerThreads = nbWorkerThreads;
    }

    InetAddress getInetAddress() {
        return inetAddress;
    }

    /** The {@link InetAddress} on which the router will listen */
    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

}
