package org.objectweb.proactive.extra.forwardingv2.registry;

import java.io.IOException;
import java.net.Socket;
import org.apache.log4j.*;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.MessageInputStream;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message.MessageType;


/**
 * The RegistrationHandler represents a "tunnel" (connection) to a client. It handles the registration process for the client: creation of an {@link OutHandler} and a mapping in the and uses its current thread as the listening side of the tunnel.
 * Whenever a correct message is received, it is forwarded to the ForwardingRegistry which will handle it
 */

public class RegistrationHandler implements Runnable {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);

    final private ForwardingRegistry registry;
    private AgentID agentID;
    final private Socket clientSocket;
    private volatile boolean running = true;

    public RegistrationHandler(Socket clientSocket, ForwardingRegistry registry, long agentID) {
        this.registry = registry;
        this.clientSocket = clientSocket;
        this.agentID = new AgentID(agentID);
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
        if (Message.readType(msg, 0) == MessageType.REGISTRATION_REQUEST.getValue()) {
            if (logger.isDebugEnabled()) {
                logger.debug("RH handling registration request");
            }

            // add mapping in the HashMap
            registry.putMapping(agentID, this);
            if (logger.isDebugEnabled()) {
                logger.debug("RH added new mapping for uniqueID " + agentID.getId());
            }

            //send RegistrationReply
            sendMessage(Message.registrationReplyMessage(agentID).toByteArray());
        }

        // the received message is not a REGISTRATION MSG, just forward it to the right tunnel
        else {
            RegistrationHandler regHandler = registry.getValueFromHashMap(Message.readDstAgentID(msg, 0));
            if (regHandler != null) {
                regHandler.sendMessage(msg);
            } else {
                //notify this to the sender by sending an abort message with the cause set to "agent unreachable"
                sendMessage(Message.connectionAbortedMessage(Message.readDstAgentID(msg, 0),
                        Message.readSrcAgentID(msg, 0), Message.readMessageID(msg, 0),
                        "Target Unreachable".getBytes()).toByteArray());
            }
        }

    }

    public synchronized void sendMessage(byte[] msg) {
        try {
            clientSocket.getOutputStream().write(msg);
        } catch (IOException e) {
            // the tunnel might be dead, stop it
            stop();
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
