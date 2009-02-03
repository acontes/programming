package org.objectweb.proactive.extra.forwardingv2.registry.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.exceptions.AgentNotConnectedException;
import org.objectweb.proactive.extra.forwardingv2.exceptions.UnknownAgentIdException;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ErrorMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ForwardedMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationReplyMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationRequestMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ErrorMessage.ErrorType;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message.MessageType;


public class ChannelHandler {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_ROUTER);

    // For agentID attribution
    static final AtomicLong attributedAgentID = new AtomicLong(1);

    // a reference to the router
    private Router router;

    // Characteristics of a tunnel
    private AgentID agentID = null;
    private SocketChannel sc;

    /* "connected" describes the status of this channel handler. 
     * "firstConnection" allows making the difference between an unknown agentID and a disconnected agentID.
     * Note that the cleaning of HashMaps is different if:
     * a registrationReply failed for a new connection (no message was sent yet so there is no need to keep the AgentID/ChannelHandler mapping)
     * or if a reconnection failed (keep this mapping for caching of messages purpose).
     */
    private volatile boolean connected = false;
    private boolean firstConnection = true;

    // For reading
    private int length = 0;
    private ByteBuffer currentlyReadMessage = null;
    private volatile boolean readyToRead = false;

    // For getting the length of a read message 
    private ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
    private int nbBytesOfLengthWritten = 0;
    private volatile boolean gettingLength = false;
    private volatile boolean gotLength = false;

    // For writing
    private final LinkedList<ByteBuffer> messagesToWrite = new LinkedList<ByteBuffer>();
    private volatile boolean writing = false; // for a single ChannelHandler, no more than one task should be submitted at the same time in order to avoid mixing two different messages on the client side

    public ChannelHandler(Router router, SocketChannel sc) {
        this.router = router;
        this.sc = sc;
    }

    private void getLength(ByteBuffer buffer) {
        int remaining = buffer.remaining();
        int initialLimit = buffer.limit();

        if (!gettingLength) {
            gettingLength = true;
            lengthBuffer.clear();
            nbBytesOfLengthWritten = 0;
        }

        if (remaining >= (4 - nbBytesOfLengthWritten)) {
            // get the missing bytes of the length
            buffer.limit(buffer.position() + 4 - nbBytesOfLengthWritten);
            lengthBuffer.put(buffer);
            nbBytesOfLengthWritten = 4;

            // restore the initial limit of buffer
            buffer.limit(initialLimit);

            // read the length
            lengthBuffer.flip();
            length = lengthBuffer.getInt();

            // set the status of the reading phase
            gettingLength = false;
            gotLength = true;
        } else {
            // get some of the length's missing bytes
            lengthBuffer.put(buffer);
            nbBytesOfLengthWritten += remaining;
        }
    }

    /**
     * adds the content of a byte buffer to the message being aggregated and once it is full, calls {@link #handleReadMessage(ByteBuffer)}
     * @param buffer a part of the currently read message, and possibly of the next message
     */
    public void putBuffer(ByteBuffer buffer) {

        // prepare for reading from the buffer
        buffer.flip();
        int initialLimit = buffer.limit();

        while (buffer.hasRemaining()) {
            // if we start receiving a new message
            if (!readyToRead) {
                getLength(buffer);

                // if we got the length
                if (gotLength) {
                    readyToRead = true;
                    if (logger.isDebugEnabled()) {
                        logger.trace("CH -> Start reading a new message; length = " + length);
                    }
                    currentlyReadMessage = ByteBuffer.allocate(length);
                    currentlyReadMessage.putInt(length);
                }
                // else we got only part of the length because we reached the end of the buffer (buffer.hasRemaining() is false), return and get the rest of it in next buffer
                else {
                    return;
                }
            }

            /* 
             * Now we are ready to read.
             * if the buffer contains parts of several messages (example given, the last part of the currently read message and the first part of a new message)
             * Note that messages might so small that more than two messages could fit in a single 2kB buffer.
             * These cases is handled by the following while loop. 
             */
            if (buffer.remaining() >= currentlyReadMessage.remaining()) {
                // copy last part of currently read message
                buffer.limit(buffer.position() + currentlyReadMessage.remaining());
                currentlyReadMessage.put(buffer);

                // handle read message
                handleReadMessage(currentlyReadMessage);

                // prepare the new message to receive its first part
                buffer.limit(initialLimit);
                gotLength = false;
                readyToRead = false;
            }

            else {
                // simply copy the part of the message contained in the buffer into the message
                currentlyReadMessage.put(buffer);
            }
        }
    }

    /**
     * Handles a read message in function of its type (registration message or message to be forwarded)
     * @param msg
     */
    private void handleReadMessage(ByteBuffer msg) {
        msg.flip();
        MessageType type = Message.readType(msg);

        switch (type) {
            case REGISTRATION_REQUEST:
                // in the case of a reconnection, a transition ChannelHandler is created and by default firstConnection is set to true. So a reconnection registrationRequest won't be discarded.
                if (firstConnection) {
                    handleRegistrationRequest(msg);
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.trace("CH -> received a registration request while channelHandler [agentID:" +
                            agentID.getId() + "] was connected... Just discard it");
                    }
                }
                break;
            case DATA_REPLY:
            case DATA_REQUEST:
                handleDataMessage(msg, type);
                break;
            default:
                logger.warn("CH -> Unknown type of message");
                break;
        }
    }

    private void handleRegistrationRequest(ByteBuffer msg) {

        if (logger.isDebugEnabled()) {
            logger.trace("CH handling registration request");
        }

        RegistrationRequestMessage request = (RegistrationRequestMessage) Message.constructMessage(msg
                .array(), 0);
        AgentID newAgentID = request.getAgentID();
        // if it is a re-connection
        if (newAgentID != null) {
            handleReConnection(newAgentID);
        }
        // else if it is a new connection
        else {
            handleNewConnection();
        }
    }

    /**
     * Handles the reconnection for the agentID given as a parameter
     * If this agentID is known, proceeds to the reconnection of a disconnected former ChannelHandler
     * Else proceeds to a new registration.
     * 
     * In the case of a reconnection of a former ChannelHandler, the ChannelHandler from which this function is called, is named the transition ChannelHandler.
     * A transition ChannelHandler has no more reference pointing on it at the end of the procedure of reconnection:
     * Its mapping in the SocketChannel/ChannelHandler HashMap of the router is replaced: the new value is the one of the former ChannelHandler.
     * And no mapping is added in the AgentID/ChannelHandler HashMap of the router for this transition ChannelHandler.
     * 
     * @param newAgentID The agentID for which to consider the reconnection
     */
    private void handleReConnection(AgentID newAgentID) {
        this.agentID = newAgentID;

        if (logger.isDebugEnabled()) {
            logger.trace("CH handling a re-connection request for agentID: " + agentID.getId());
        }
        // find the former ChannelHandler associated with this agentID
        ChannelHandler formerCH = null;
        try {
            formerCH = router.getValueFromHashMap(agentID);
        } catch (UnknownAgentIdException e) {
            // if the received agentID is unknown, log it and proceed to a new registration
            if (logger.isDebugEnabled()) {
                logger
                        .warn("AgentID: " + agentID.getId() +
                            " unknown. CH could not handle the re-connection. Going to handle a new connection instead");
            }
            handleNewConnection();
            return;
        }

        // if the received agentID is known, proceed to the reconnection of a disconnected former ChannelHandler
        formerCH.processFormerChannelHandlerReconnection(sc);
    }

    /**
     * Handles a new connection: attributes a unique agentID, puts mapping in the right HashMap of the router, send a registration reply
     */
    private void handleNewConnection() {
        // generate the agentID
        this.agentID = new AgentID(attributedAgentID.getAndIncrement());
        if (logger.isDebugEnabled()) {
            logger.trace("CH handling a new connection for agentID: " + agentID.getId());
        }

        // add mapping in the HashMap
        router.putMapping(agentID, this);
        if (logger.isDebugEnabled()) {
            logger.trace("CH added new mapping for uniqueID " + agentID.getId());
        }
        // Prepare registration reply and write it
        write(ByteBuffer.wrap(new RegistrationReplyMessage(agentID, 0).toByteArray()), false);
    }

    /**
     * Handles the mappings modification and registration reply sending of a former Channel Handler being reconnected
     * This function should be called from the former Channel Handler that is being reconnected.
     * This function can remain private since it should always be called by transition Channel Handlers which are indeed ChannelHandlers...
     * 
     * Note that we don't set the status of this former Channel Handler to connected here. This is done in the messageProcessor after having sent the registration reply successfully.
     *  
     * @param socketChannel the new socket channel to attribute to a former connection (ChannelHandler)
     */
    private void processFormerChannelHandlerReconnection(SocketChannel socketChannel) {
        // Replace the SocketChannel of the former ChannelHandler by the new one contained given as a parameter and got from the transition ChannelHandler
        sc = socketChannel;

        // Don't set the status of this former Channel Handler to connected before having sent the registration reply successfully

        // modify the mapping of this former ChannelHandler in the SocketChannel/ChannelHandler HashMap of the router: replace the value of the transition ChannelHandler by the one of the former ChannelHandler for this SocketChannel
        router.putMapping(sc, this);

        // send a registration reply
        // TODO: do we need to send buffered messages immediately or should we try to be fair regarding the number of messages sent by ChannelHandler
        write(ByteBuffer.wrap(new RegistrationReplyMessage(agentID, 0).toByteArray()), false);
    }

    private void handleDataMessage(ByteBuffer msg, MessageType type) {

        ChannelHandler dstChannelHandler = null;
        AgentID dstAgentID = ForwardedMessage.readDstAgentID(msg.array());

        if (logger.isDebugEnabled()) {
            logger.trace("CH -> handling data message from " + agentID + " to " + dstAgentID);
        }

        try {
            dstChannelHandler = router.getValueFromHashMap(dstAgentID);
        }
        // if dstChannelHandler is null we catch an UnknownAgentIdException
        catch (UnknownAgentIdException e) {
            write(ByteBuffer.wrap(new ErrorMessage(agentID, ForwardedMessage.readMessageID(msg.array(), 0),
                ErrorType.ERR_UNKNOW_RCPT).toByteArray()), false);
            return;
        }
        // the recipient is known, check if it is connected
        if (!dstChannelHandler.isConnected()) {
            if (logger.isDebugEnabled()) {
                logger.warn("CH -> destination ID " + dstAgentID + " is known but disconnected.");
            }
            // the recipient is known but disconnected
            handleDisconnectedRecipient(msg, type, dstAgentID, dstChannelHandler,
                    new AgentNotConnectedException("dstAgentID[" + dstAgentID.getId() + "] disconnected"));
        } else {
            // at this point the recipient is known, and connected, forward the message
            if (logger.isDebugEnabled()) {
                logger.trace("CH -> Forwarding the message to Channel handler in charge of ID " + dstAgentID);
            }
            dstChannelHandler.write(this, msg, false);
        }
    }

    /**
     * The exception given as a parameter can be an {@link AgentNotConnectedException} or another {@link IOException}.
     * In the first case the recipient was not connected even before trying to send the message. 
     * In the second case, a failure occurred while sending the message.
     * In both cases the recipient is considered as disconnected in the logging since the second case implies a disconnection of the recipient.
     * 
     * @param msg the message that was being sent
     * @param type the type of the message
     * @param dstAgentID the recipient of the message
     * @param e the exception detailing which error occurred.
     * @param first whether the message to cache should be added at the beginning or at the end of {@link #messagesToWrite} list
     */
    private void handleDisconnectedRecipient(ByteBuffer msg, MessageType type, AgentID dstAgentID,
            ChannelHandler dstChannelHandler, IOException e) {
        switch (type) {
            case DATA_REQUEST: // log and send an error message
                if (logger.isDebugEnabled()) {
                    logger.trace("CH recipient disconnected, DataRequest [srcAgentID: " + agentID.getId() +
                        "] [dstAgentID: " + dstAgentID.getId() +
                        "] could not be sent, returning error message");
                }
                // send error message
                write(ByteBuffer.wrap(new ErrorMessage(agentID, ForwardedMessage
                        .readMessageID(msg.array(), 0), ErrorType.ERR_DISCONNECTED_RCPT_BROADCAST)
                        .toByteArray()), first);
                break;
            case DATA_REPLY: // cache reply
                if (logger.isDebugEnabled()) {
                    logger.trace("CH recipient disconnected, DataReply [srcAgentID: " + agentID.getId() +
                        "] [dstAgentID: " + dstAgentID.getId() + "] could not be sent, caching reply");
                }
                // cache the reply (at this point the status of dstChannelHandler should be not connected, indeed we checked its value just before calling handleDisconnectedRecipient())
                dstChannelHandler.write(this, msg, false);
                break;
            case ERR_:
            default:
                break;
        }
    }

    /**
     * asks a thread of the thread pool to write the message. 
     * If a thread was already called (writing is true), then puts the message in the list of messagesToWrite, else submit a runnable to the thread pool. 
     * The runnable writes the message and then asks if it should send another message (ie if checks if the arrayList of messages to be written is empty or not). 
     * If yes it processes the new message, else it is released.
     * 
     * @param msg the message to send
     * @param first whether the message to send should be added at the beginning or at the end of the {@link #messagesToWrite} list if needed
     */
    public synchronized void write(ChannelHandler srcChannelHandler, ByteBuffer msg, boolean first) {
        /* if this is a Registration Reply, the status of the channelHandler might be "not connected" in the case of a reconnection.
         * In this case we bypass the test (!writing && connected), because this channel handler can't be writing (it is trying to connect) and we need to send the message in the case of a reconnection
         */
        synchronized (messagesToWrite) {
            if ((Message.readType(msg) == MessageType.REGISTRATION_REPLY) || (!writing && connected)) {
                MessageProcessor msgProcessor = new MessageProcessor(srcChannelHandler, this, msg);
                router.submitTask(msgProcessor);
                if (logger.isDebugEnabled()) {
                    logger.trace("CH -> created and submitted a msgProcessor task");
                }
                writing = true;
            } else {
                if (first) {
                    messagesToWrite.addFirst(msg);
                    if (logger.isDebugEnabled()) {
                        logger.trace("CH -> allready writing ");
                    }
                } else {
                    messagesToWrite.addLast(msg);
                    if (logger.isDebugEnabled()) {
                        logger.trace("CH -> created and submitted a msgProcessor task");
                    }
                }
            }

        }
    }

    /**
     * if this channel handler is connected and has a message to write, submit it
     * Else set writing to false 
     * Note that the next message can only be a ForwardedMessage. Indeed, a registration message can never be in the list {@link #messagesToWrite}
     * Note that if a message is in the list messagesToWrite, it means that it has been aggregated by its srcChannelHandler. This srcChannelHandler might be disconnected but can't be unknown.
     */
    public void submitNextMessage() {
        if (connected) {
            synchronized (messagesToWrite) {
                if (!messagesToWrite.isEmpty()) {
                    ByteBuffer msg = messagesToWrite.removeFirst(); // is necessarily a forwarded message
                    AgentID srcAgentID = ForwardedMessage.readSrcAgentID(msg);
                    ChannelHandler srcChannelHandler = null;
                    try {
                        srcChannelHandler = router.getValueFromHashMap(srcAgentID);
                    } catch (UnknownAgentIdException e) {
                        // Should never occur: if a message is in the list messagesToWrite, it means that it has been aggregated by its srcChannelHandler. This srcChannelHandler might be disconnected but can't be unknown. 
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    MessageProcessor msgProcessor = new MessageProcessor(srcChannelHandler, this, msg);
                    router.submitTask(msgProcessor);
                } else {
                    writing = false;
                }
            }
        } else {
            writing = false;
        }
    }

    /**
     * Update the channelHandler status
     * Remove the SocketChannel/ChannelHandler mapping
     * Close the SocketChannel
     * Remove the AgentID/ChannelHandler mapping if needed
     * 
     * @param removeAgentIDMapping if true, remove the AgentID/ChannelHandler mapping
     */
    public void stop(boolean removeAgentIDMapping) {
        // update the channelHandler status
        connected = false;

        // remove the SocketChannel/ChannelHandler mapping
        router.removeMapping(sc);

        // close the SocketChannel
        try {
            sc.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // remove the AgentID/ChannelHandler mapping if needed
        if (removeAgentIDMapping) {
            router.removeMapping(agentID);
        }
    }

    public AgentID getAgentID() {
        return agentID;
    }

    public SocketChannel getSc() {
        return sc;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isFirstConnection() {
        return firstConnection;
    }

    public void setFirstConnection(boolean firstConnection) {
        this.firstConnection = firstConnection;
    }
}
