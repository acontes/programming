package org.objectweb.proactive.extra.forwarding.common;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * The OutHandler is given a Socket where to write messages.
 * You can push messages in this OutHandler in a concurrent way, it is thread-safe.
 * The OutHandler push the messages one after the other.
 */
public class OutHandler implements Runnable {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);

    private LinkedBlockingQueue<ForwardedMessage> messageQueue;
    private ForwardingSocketWrapper sock;
    private ConnectionFailureListener listener;
    private boolean willClose;
    private boolean isRunning;

    /**
     * Creation of the OutHandler.
     * @param socketToWrite Socket where to write the messages.
     * @param listener Who to contact in case of failure.
     */
    public OutHandler(ForwardingSocketWrapper socketToWrite, ConnectionFailureListener listener) {
        this.sock = socketToWrite;
        messageQueue = new LinkedBlockingQueue<ForwardedMessage>();
        willClose = false;
        isRunning = true;
        this.listener = listener;
    }

    /**
     * Run loop: pushing messages in the socket.
     */
    public void run() {
        if (logger.isDebugEnabled())
            logger.trace("OutHandler.run(): start loop");
        while (isRunning && !sock.getSocket().isClosed()) {
            ForwardedMessage msg = null;
            try {
                msg = messageQueue.poll(1, TimeUnit.SECONDS);
            } catch (InterruptedException e1) {
                // The waiting has been interrupted.
                if (logger.isDebugEnabled())
                    logger.warn("The waiting of the blocking queue was interrupted");
            }
            if (msg != null) {
                if (logger.isDebugEnabled())
                    logger.trace("message to write: " + msg);
                try {
                    writeOnSocket(msg);
                    if (logger.isDebugEnabled())
                        logger.trace("message written.");
                } catch (IOException e) {
                    listener.connectionHasFailed(e);
                    isRunning = false;
                }
            } else if (willClose) {
                isRunning = false;
            }
        }
        closeConnection();
    }

    /**
     * Put a message in the toWriteQueue.
     * @param message
     */
    public void putMessage(ForwardedMessage message) {
        if (isRunning && !willClose) {
            try {
                boolean notFull = messageQueue.offer(message);
                if (!notFull)
                    logger.warn("Queue is full; dropping message");
                else if (logger.isDebugEnabled())
                    logger.trace("message queued.");
            } catch (NullPointerException e) {
                logger.warn("Should not pass null messages to OutHandler!");
            }
        }
    }

    /**
     * Close the socket.
     * if softly is true, it will send all the already registered messages
     * before closing the connection.
     * @param softly
     */
    public void stop(boolean softly) {
        if (softly) {
            willClose = true;
        } else {
            isRunning = false;
        }
    }

    /**
     * Write a message in the socket.
     * @param msg
     * @throws IOException
     */
    private void writeOnSocket(ForwardedMessage msg) throws IOException {
        sock.writeObject(msg);
    }

    /**
     * Close the connection
     */
    private void closeConnection() {
        sock.close();
    }

}
