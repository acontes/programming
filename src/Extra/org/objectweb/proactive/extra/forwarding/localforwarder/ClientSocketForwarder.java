package org.objectweb.proactive.extra.forwarding.localforwarder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.objectweb.proactive.core.util.ProActiveInet;
import org.objectweb.proactive.extra.forwarding.common.OutHandler;


/**
 * The Client Socket forwarder send a connection request and wait for a response from the
 * target. If the timeout is reached, then an exception is launched and the connection fails.
 */
public class ClientSocketForwarder extends SocketForwarder {

    private CountDownLatch lock;
    private volatile boolean accepted;
    private volatile boolean launched;

    public ClientSocketForwarder(Object localID, int localPort, Object targetID, int targetPort,
            OutHandler tunnel, LocalConnectionHandler handler) {
        super(localID, localPort, targetID, targetPort, tunnel, handler);
    }

    /**
     * Lock until a abort or accept message arrives.
     */
    protected void initSocket() {
        // lock until message
        lock = new CountDownLatch(1);
        launched = false;
    }

    @Override
    public void notifyAbort() {
        if (!launched) {
            accepted = false;
            lock.countDown();
        } else {
            super.notifyAbort();
        }
    }

    /**
     * Notify that the connection is accepted: unlock.
     */
    public void notifyAccept() {
        accepted = true;
        launched = true;
        lock.countDown();
    }

    /**
     * Try to get the socket until the timeout.
     * @param lockTime the time to lock in milliseconds.
     * @return the socket or null.
     */
    public Socket getSocket(long lockTime) {
        Socket sock = null;
        try {
            if (logger.isDebugEnabled())
                logger
                        .debug("ClientSocketForwarder.getSocket() : lock until timeout or accept or abort connection.");
            lock.await(lockTime, TimeUnit.MILLISECONDS);
            if (accepted) {
                sock = createLocalSocket();
                startHandling();
            } else {
            	// need to unregister this socket forwarder
            	handler.unregisterSocketForwarder(this);
            }
        } catch (InterruptedException e) {
            logger.debug("ClientSocketForwarder interupted while waiting response from forwarding.", e);
        }
        return sock;
    }

    /**
     * In order not to implement a custom socket, we create a connection between 
     * two sockets, give one to the client and keep one to read and write.
     * @return
     */
    private Socket createLocalSocket() {
        int port;
        ServerSocket ss;
        try {
            ss = new ServerSocket(0);
        } catch (IOException e1) {
            logger.warn("Error while creating local socket for Client Socket Forwarder", e1);
            return null;
        }
        ServerSocketCreator creator = new ServerSocketCreator(ss);
        new Thread(creator).start();
        port = ss.getLocalPort();

        try {
            Socket sock1 = new Socket(ProActiveInet.getInstance().getInetAddress(), port);
            Socket sock2 = creator.getSocket();
            sockToHandle = sock1;
            return sock2;
        } catch (UnknownHostException e) {
            logger.error("Error while creating local socket for Client Socket Forwarder", e);
        } catch (IOException e) {
            logger.error("Error while creating local socket for Client Socket Forwarder", e);
        }
        return null;

    }

    /**
     * Class helping with the creation of the local Sockets.
     */
    private class ServerSocketCreator implements Runnable {
        private CountDownLatch sem;
        private ServerSocket s;
        private Socket sock = null;

        public ServerSocketCreator(ServerSocket s) {
            this.s = s;
            sem = new CountDownLatch(1);
        }

        public void run() {
            try {
                sock = s.accept();
                sem.countDown();
            } catch (IOException e) {
                logger.error("Error while creating local socket for Client Socket Forwarder", e);
            }
        }

        public Socket getSocket() {
            Socket res = null;
            try {
                sem.await();
                res = sock;
            } catch (InterruptedException e) {
                logger.warn("Error while creating local socket for Client Socket Forwarder", e);
            }
            return res;
        }

    }

}
