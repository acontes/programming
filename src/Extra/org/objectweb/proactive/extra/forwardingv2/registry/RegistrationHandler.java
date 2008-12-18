package org.objectweb.proactive.extra.forwardingv2.registry;

import java.io.IOException;
import java.net.Socket;
import org.apache.log4j.*;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.exceptions.RemoteConnectionBrokenException;
import org.objectweb.proactive.extra.forwardingv2.exceptions.UnknownAgentIdException;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.MessageInputStream;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ExceptionMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ForwardedMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationReplyMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationRequestMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message.MessageType;


/**
 * The RegistrationHandler represents a "tunnel" (connection) to a client. 
 * It handles the registration process for the client uses its current thread as the listening side of the tunnel.
 * Whenever a new ForwardedMessage is received, it is passed to the right RegistrationHandler in order to be forwarded.
 */

public class RegistrationHandler implements Runnable {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);

    static long attributedAgentID = 1;

    final private ForwardingRegistry registry;
    private AgentID agentID = null;
    final private Socket clientSocket;
    private volatile boolean running = true;

    public RegistrationHandler(Socket clientSocket, ForwardingRegistry registry) {
        this.registry = registry;
        this.clientSocket = clientSocket;
    }

    /**
     * Listens incoming {@link Message} and handles them.
     * If the socket is open
     * 		If the message is a {@link MessageType #REGISTRATION_REQUEST}:
     * 			process to a new registration
     * 				call {@link ForwardingRegistry #putMapping(AgentID, RegistrationHandler)}
     * 				send a {@link MessageType #REGISTRATION_REPLY}
     * 			use this thread as the listening side of this connection
     * 		
     * 		else the received {@link Message} is not a {@link MessageType #REGISTRATION_REQUEST}, just forward it
     * 			call processMessage on the message
     * Else the socket is closed
     * 		call {@link #stop()}
     */
    public void run() {
        MessageInputStream input = null;
        // initialize the input stream
        try {
            input = new MessageInputStream(clientSocket.getInputStream());
        } catch (IOException e1) {
            if (logger.isDebugEnabled()) {
                logger
                        .debug("RH, IOException, failed to initialize the MessageInputStream, stopping RH with agentID: " +
                            agentID.getId());
                e1.printStackTrace();
            }
            running = false;
        }

        // handle incoming messages
        while (running) {
            if (!(clientSocket.isClosed() && input != null)) {
                // read new message
                try {
                    processMessage(input.readMessage());
                } catch (IOException e) { //the input failed somehow, stop running
                    if (logger.isDebugEnabled()) {
                        logger.debug("RH, the input stream failed");
                    }
                    running = false;
                    e.printStackTrace();
                }
            }
            // the socket is closed or the input could not be initialized
            else {
                stop();
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("RH, calling RegistrationHandler.stop()");
        }
        stop();
    }

    private void processMessage(byte[] msg) {
        // the received message is a REGISTRATION MSG -> handle the registration
        if (Message.readType(msg, 0) == MessageType.REGISTRATION_REQUEST) {
            if (logger.isDebugEnabled()) {
                logger.debug("RH handling registration request");
            }
            AgentID newAgentID = RegistrationRequestMessage.readAgentID(msg, 0);
            if (agentID != null) {
                this.agentID = newAgentID;
            } else {
                this.agentID = new AgentID(attributedAgentID);
                attributedAgentID++;
            }
            // add mapping in the HashMap
            registry.putMapping(agentID, this);
            if (logger.isDebugEnabled()) {
                logger.debug("RH added new mapping for uniqueID " + agentID.getId());
            }

            //send RegistrationReply
            try {
                sendMessage(new RegistrationReplyMessage(agentID).toByteArray());
            } catch (RemoteConnectionBrokenException e) { //TODO: check if there is a better solution
                // The registration reply could not be sent, the user can't be notified. Just stop the current registrationHandler
                stop();
            }
        }

        // the received message is not a REGISTRATION_REQUEST MSG, just forward it to the right tunnel
        else {
            RegistrationHandler regHandler = null;
            AgentID dstAgentID = ForwardedMessage.readDstAgentID(msg, 0);
            try {
                regHandler = registry.getValueFromHashMap(dstAgentID);
            }
            // if regHandler is null we catch an UnknownAgentIdException
            catch (UnknownAgentIdException e) { //TODO: check if it is possible to use less parameters
                try {
                    sendMessage(new ExceptionMessage(MessageType.ROUTING_EXCEPTION_MSG, dstAgentID, agentID,
                        ForwardedMessage.readMessageID(msg, 0), e).toByteArray());
                    return;
                } catch (RemoteConnectionBrokenException e1) {
                    // could not notify that the destination was unknown because the source tunnel has failed. Just stop the current RegistrationHandler
                    stop();
                }
            }
            // else just forward the message
            try {
                regHandler.sendMessage(msg);
            } catch (RemoteConnectionBrokenException e) {
                // could not send the message to its destination, notify the source by sending an ExceptionMessage
                try {
                    sendMessage(new ExceptionMessage(MessageType.ROUTING_EXCEPTION_MSG, dstAgentID, agentID,
                        ForwardedMessage.readMessageID(msg, 0), e).toByteArray());
                } catch (RemoteConnectionBrokenException e1) {
                    // could not send a notification of the failure to the source, because the source tunnel has also failed... just stop the source tunnel
                    stop();
                }
            }
        }

    }

    public synchronized void sendMessage(byte[] msg) throws RemoteConnectionBrokenException {
        try {
            clientSocket.getOutputStream().write(msg);
        } catch (IOException e) {
            stop();
            throw new RemoteConnectionBrokenException("could not send message: " + msg.toString()); //TODO: give more info ?
        }
    }

    /**
     * Stops running if it was not already the case.
     * Then closes the socket.
     * Finally, removes mapping from {@link #registry}'s map.
     */
    protected void stop() {
        //stop running if it was not already the case
        running = false;

        // close the socket
        try {
            clientSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // remove mapping from registry's HashMap
        registry.removeMapping(agentID);
    }

    public AgentID getAgentID() {
        return agentID;
    }
}
