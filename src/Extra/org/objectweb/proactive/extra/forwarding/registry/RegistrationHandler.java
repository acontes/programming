package org.objectweb.proactive.extra.forwarding.registry;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.*;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwarding.common.ConnectionFailureListener;
import org.objectweb.proactive.extra.forwarding.common.ForwardedMessage;
import org.objectweb.proactive.extra.forwarding.common.ForwardingSocketWrapper;
import org.objectweb.proactive.extra.forwarding.common.OutHandler;
import org.objectweb.proactive.extra.forwarding.common.ForwardedMessage.ForwardedMessageType;


/**
 * The RegistrationHandler represents a "tunnel" (connection) to a client. It handles the registration process for the client: creation of an {@link OutHandler} and a mapping in the and uses its current thread as the listening side of the tunnel.
 * Whenever a correct message is received, it is forwarded to the ForwardingRegistry which will handle it
 *   
 * @author A.Fawaz
 *
 */

public class RegistrationHandler implements Runnable, ConnectionFailureListener {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);

    final private ForwardingRegistry registry;
    private Object hostId = null;
    final private ForwardingSocketWrapper clientSocketWrapper;
    private OutHandler outHandler;
    private volatile boolean listening = true;

    public RegistrationHandler(Socket clientSocket, ForwardingRegistry registry) {
        this.registry = registry;
        this.clientSocketWrapper = new ForwardingSocketWrapper(clientSocket);
    }

    /**
     * Listens incoming {@link ForwardedMessage} and handles them.
     * If the socket is open
     * 		If the message is a {@link ForwardedMessageType #REGISTRATION}:
     * 			if it is a pre-existing tunnel (a failure probably happened)
     * 				then kill the pre-existing tunnel harshly... there might have been a failure somewhere
     * 			
     * 			process to a new registration
     * 				create new {@link OutHandler}
     * 				call {@link ForwardingRegistry #putMapping(Object, RegistrationHandler)}
     * 			
     * 			use this thread as the listening side of this connection
     * 		
     * 		else the received {@link ForwardedMessage} is not a {@link ForwardedMessageType #REGISTRATION}, just forward it
     * 			add {@link ForwardedMessage} to the list of messages to be handled by the {@link #registry}
     * Else the socket is closed
     * 		call {@link #connectionHasFailed(Exception)}
     */
    public void run() {
        ForwardedMessage msg = null;

        while (listening) {
            if (!(clientSocketWrapper.getSocket().isClosed())) {
                try {
                    // read new message
                    msg = (ForwardedMessage) clientSocketWrapper.readObject();
                    if (logger.isDebugEnabled()) {
                        logger.debug("RH read msg: " + msg);
                    }
                } catch (IOException e) {
                    connectionHasFailed(e);
                    continue;
                } catch (ClassNotFoundException e) {// should not occur
                    logger.error("read unknown object, exception: ", e);
                }

                // the received message is a REGISTRATION MSG -> handle the registration
                if (msg.getType() == ForwardedMessageType.REGISTRATION) {
                    this.hostId = msg.getSenderID();
                    if (logger.isDebugEnabled()) {
                        logger.debug("RH handling registration");
                    }

                    // if it is a pre-existing connection (a failure probably happened)
                    if (registry.isKeyInMap(msg.getSenderID())) {
                        // kill the pre-existing connection harshly... there might have been a failure somewhere
                        stop(false);
                        if (logger.isDebugEnabled()) {
                            logger.debug("RH killed the pre-existing connection for uniqueID: " + hostId);
                        }
                    }

                    if (logger.isDebugEnabled()) {
                        logger
                                .debug("RH, processing to a new registration (new outHandler and new mapping) for uniqueID " +
                                    hostId);
                    }
                    // process to a new registration
                    // create new OutHandler
                    outHandler = new OutHandler(clientSocketWrapper, this);
                    new Thread(outHandler).start();
                    if (logger.isDebugEnabled()) {
                        logger.debug("RH started the new outHandler for uniqueID " + hostId);
                    }
                    // add mapping in the HashMap
                    registry.putMapping(msg.getSenderID(), this);
                    if (logger.isDebugEnabled()) {
                        logger.debug("RH added new mapping for uniqueID " + hostId);
                        logger.debug("RH now using current thread as server side of the tunnel to " + hostId);
                    }
                }
                // use this thread as the listening side of this connection
                // the received message is not a REGISTRATION MSG, just forward it
                else {
                    //add msg to the list of messages to be handled by the registry
                    registry.putMessage(msg);
                }
            }
            // the socket is closed
            else {
                connectionHasFailed(null);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("RH, calling RegistrationHandler.stop()");
        }
        stop(true);
    }

    /**
     * Sets {@link #listening} to false to prevent the current RegistrationHandler from receiving new {@link ForwardedMessage} 
     */
    protected void stopListening() {
        listening = false;
        if (logger.isDebugEnabled()) {
            logger.debug("RH.stopListening() on uniqueId: " + hostId);
        }
    }

    /**
     * Stops listening if it was not already the case.
     * Then stops writing and closes the socket.
     * Finally, removes mapping from {@link #registry}'s map.
     * 
     * @param softly, whether or not to forward the remaining messages in the OutHandler
     */
    protected void stop(boolean softly) {
        //stop listening if it was not already the case
        listening = false;

        // stop writing
        if (logger.isDebugEnabled()) {
            logger.debug("RH.stop(), stopping outHandler, closing socket and removing mapping");
        }

        if (outHandler != null) {
            outHandler.stop(softly);
        }

        // close the socket
        clientSocketWrapper.close();

        // remove mapping from registry's HashMap
        registry.removeMapping(hostId);
    }

    /**
     * Calls {@link #stop(boolean) in order to stop properly the current Registration Handler}
     */
    public void connectionHasFailed(Exception e) {
        if (logger.isDebugEnabled()) {
            logger.debug("RH.connectionHasFailed tunnel to " + hostId + " has failed, exception: " + e);
        }
        stop(false);
    }

    public OutHandler getOutHandler() {
        return outHandler;
    }

    public Object getHostId() {
        return hostId;
    }
}