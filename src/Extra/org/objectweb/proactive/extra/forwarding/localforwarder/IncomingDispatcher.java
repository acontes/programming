package org.objectweb.proactive.extra.forwarding.localforwarder;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwarding.common.ConnectionFailureListener;
import org.objectweb.proactive.extra.forwarding.common.ForwardedMessage;
import org.objectweb.proactive.extra.forwarding.common.ForwardingSocketWrapper;


/**
 * Class in charge of reading messages from the tunnel.
 */
public class IncomingDispatcher implements Runnable {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);

    final private ForwardingSocketWrapper sock;
    final private ConnectionFailureListener listener;
    private boolean isRunning;
    final private LocalConnectionHandler connectionHandler;

    public IncomingDispatcher(ForwardingSocketWrapper sock, ConnectionFailureListener listener,
            LocalConnectionHandler handler) {
        this.sock = sock;
        this.listener = listener;
        this.connectionHandler = handler;
        this.isRunning = true;
    }

    public void run() {
        while (isRunning && !sock.getSocket().isClosed()) {
            ForwardedMessage msg = null;
            try {
                msg = (ForwardedMessage) sock.readObject();
            } catch (IOException e) {
                // Socket has failed ! notification of failure.
                logger.error("Error during read-from-socket", e);
                listener.connectionHasFailed(e);
                stop();
            } catch (ClassNotFoundException e) {
                // Class not known : shouldn't happen...
                logger.warn("Message not readable obtained : " + e.getCause());
            } catch (ClassCastException e) {
                // Class not known : shouldn't happen...
                logger.warn("Message not readable obtained : " + e.getCause());
            }

            if (msg != null) {
                dispatch(msg);
            }
        }
        closeConnection();
    }

    /**
     * Close the socket.
     */
    public void stop() {
        isRunning = false;
    }

    private void closeConnection() {
        sock.close();
    }

    /**
     * Dispatch the different messages to the different places
     * @param msg Message to handle
     */
    private void dispatch(ForwardedMessage msg) {
        switch (msg.getType()) {
            case CONNECTION_REQUEST:
            case CONNECTION_ACCEPTED:
            case CONNECTION_ABORTED:
            case DATA:
                // Forward to the Connection handler
                connectionHandler.receivedMessage(msg);
                break;
            case AGENT_DISCONNECTED:
                connectionHandler.agentDisconnected(msg);
                break;
            case REGISTRATION: // Not handled
            default:
                logger.warn("Message not handled received : " + msg);
                break;
        }

    }

}
