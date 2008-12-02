package org.objectweb.proactive.extra.forwarding.localforwarder;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwarding.common.ForwardedMessage;
import org.objectweb.proactive.extra.forwarding.common.OutHandler;


/**
 * The {@link SocketForwarder} is in charge of forwarding data from and to the client or server.
 * each bunch of data is encapsulated in a {@link ForwardedMessage} in order to be correctly sent
 * throw the registry.
 */
public abstract class SocketForwarder {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);
    public static final int BUFFER_SIZE = 1024;

    // Tunnel
    protected OutHandler tunnel;
    protected LocalConnectionHandler handler;

    // Local ID & port
    protected Object localID;
    protected int localPort;

    // Remote ID & port
    protected Object targetID;
    protected int targetPort;

    protected Socket sockToHandle;
    protected SocketReader reader;
    protected SocketWritter writter;

    public SocketForwarder(Object localID, int localPort, Object targetID, int targetPort, OutHandler tunnel,
            LocalConnectionHandler handler) {
        this.localPort = localPort;
        this.targetPort = targetPort;
        this.localID = localID;
        this.targetID = targetID;
        this.tunnel = tunnel;
        this.handler = handler;
        sockToHandle = null;

        initSocket();
    }

    protected abstract void initSocket();

    /**
     *  Encapsulate data and send it.
     * @param data
     * @param size
     */
    protected void outgoingData(byte[] data, int size) {
        // TODO See if copy array or forward not full array.
        byte[] buf = new byte[size];
        System.arraycopy(data, 0, buf, 0, size);
        tunnel.putMessage(ForwardedMessage.dataMessage(localID, localPort, targetID, targetPort, buf));
    }

    /**
     * Start handling incoming and outgoing messages.
     */
    protected void startHandling() {
        reader = new SocketReader(sockToHandle, this);
        writter = new SocketWritter(sockToHandle, this);
        new Thread(reader).start();
        new Thread(writter).start();
    }

    /**
     * receive message from the outside,
     * forward the data to the socket.
     * @param data
     */
    public void receivedData(byte[] data) {
        writter.enqueueData(data);
    }

    public void stop() {
        // TODO handle stopping
        handler.unregisterSocketForwarder(this);
        reader.stop();
        writter.stop();
        closeConnection();
    }

    public void notifyAbort() {
        //closeConnection();
        stop();
    }

    protected void closeConnection() {
        try {
            sockToHandle.close();
        } catch (IOException e) {
            logger.debug("Error while closing socket: ", e);
        }
    }

    public void abort(String cause) {
        tunnel.putMessage(ForwardedMessage.abortMessage(localID, localPort, targetID, targetPort, cause));
    }

    private class SocketReader implements Runnable {

        private Socket sock;
        private boolean isRunning;
        private SocketForwarder forw;

        public SocketReader(Socket sock, SocketForwarder forw) {
            this.sock = sock;
            isRunning = true;
            this.forw = forw;
        }

        public void run() {
            byte[] buf = new byte[BUFFER_SIZE];
            while (isRunning) {
                try {
                    int read = sock.getInputStream().read(buf);
                    if (read <= 0) {
                        // Socket closed
                        forw.abort("Connection closed by peer");
                        forw.stop();
                    } else
                        forw.outgoingData(buf, read);
                } catch (IOException e) {
                    // TODO Handle disconnection
                    forw.abort(e.getMessage());
                    logger.warn("Exception on socket while reading, aborting forwarded connection", e);
                    forw.stop();
                }
            }
        }

        public void stop() {
            isRunning = false;
        }

    }

    private class SocketWritter implements Runnable {

        private Socket sock;
        private boolean isRunning;
        private SocketForwarder forw;
        private LinkedBlockingQueue<byte[]> messageQueue;

        public SocketWritter(Socket sock, SocketForwarder forw) {
            messageQueue = new LinkedBlockingQueue<byte[]>();
            this.sock = sock;
            isRunning = true;
            this.forw = forw;
        }

        public void run() {
            byte[] buf = null;
            while (isRunning) {
                try {
                    try {
                        buf = messageQueue.poll(1, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        logger.debug("Error while trying to get a message to handle: ", e);
                    }

                    if (buf != null) {
                        sock.getOutputStream().write(buf, 0, buf.length);
                        sock.getOutputStream().flush();
                    }
                } catch (IOException e) {
                    // TODO Handle disconnection
                    forw.abort(e.getMessage());
                    forw.stop();
                    logger.warn("Error while trying to write into forwarded socket, aborting forwarded connection", e);
                }
            }
        }

        public void enqueueData(byte[] data) {
            messageQueue.offer(data);
        }

        public void stop() {
            isRunning = false;
        }

    }

    // GETTERS
    /**
     * @return the localID
     */
    public Object getLocalID() {
        return localID;
    }

    /**
     * @return the localPort
     */
    public int getLocalPort() {
        return localPort;
    }

    /**
     * @return the targetID
     */
    public Object getTargetID() {
        return targetID;
    }

    /**
     * @return the targetPort
     */
    public int getTargetPort() {
        return targetPort;
    }

}
