package org.objectweb.proactive.extra.forwardingv2.registry.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationReplyMessage;
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

    /* "connected" describes the status of this channel handler. It allows making the difference between an unknown agentID and a disconnected agentID.
     * Initially true. This means that upon reception of a RegistrationRequest, if connected is true, it is a new connection, else it is a reconnection.
     * Indeed the cleaning of HashMaps is different if:
     * a registrationReply failed for a new connection (no message was sent yet so there is no need to keep the AgentID/ChannelHandler mapping)
     * or if a reconnection failed (keep this mapping for caching of messages purpose).
     */
    private volatile boolean connected = true;

    // For reading
    private ByteBuffer currentlyReadMessage = null;
    private volatile boolean reading = false; // for initialization of the process of aggregating various parts of a message

    // For writing
    private final List<ByteBuffer> messagesToWrite; // synchronized, encapsulates a LinkedList. We need to addfirst, addlast and getfirst. Thus using a method for indexed access will be in constant time since the only index to be used will be 0.
    private volatile boolean writing = false; // for a single ChannelHandler, no more than one task should be submitted at the same time in order to avoid mixing two different messages on the client side

    public ChannelHandler(Router router, SocketChannel sc) {
        this.router = router;
        this.sc = sc;
        messagesToWrite = Collections.synchronizedList(new LinkedList<ByteBuffer>());
    }

    /**
     * adds the content of a byte buffer to the message being aggregated and once it is full, calls {@link #handleReadMessage(ByteBuffer)}
     * @param buffer a part of the currently read message, and possibly of the next message
     */
    public void putBuffer(ByteBuffer buffer) {
        int length = 0;

        // prepare for reading from the buffer
        buffer.flip();

        // start receiving a new message
        if (!reading) {
            reading = true;
            // absolute function, does not affect the position of the buffer
            length = buffer.getInt();
            if(logger.isDebugEnabled()) {
            	logger.debug("CH -> Start reading a new message; length = "+length);
            }
            currentlyReadMessage = ByteBuffer.allocate(length);
            currentlyReadMessage.putInt(length);
        }

        /* if the buffer contains parts of several messages (example given, the last part of the currently read message and the first part of a new message)
         * Note that messages might so small that more than two messages could fit in a single 2kB buffer.
         * These cases is handled by the following while loop. 
         */
        while (buffer.remaining() > currentlyReadMessage.remaining()) {
            // copy last part of currently read message
            buffer.limit(currentlyReadMessage.remaining());
            currentlyReadMessage.put(buffer);

            // handle read message
            handleReadMessage(currentlyReadMessage);

            // prepare the new message to receive its first part
            buffer.limit(buffer.capacity());
            length = buffer.getInt();
            currentlyReadMessage = ByteBuffer.allocate(length);
            currentlyReadMessage.putInt(length);
        }

        // copy the part of the message contained in the buffer into the message
        currentlyReadMessage.put(buffer);

        // check if the buffer contained exactly the last part of the currently read message and if yes, set reading to false
        if (currentlyReadMessage.remaining() == 0) {
            handleReadMessage(currentlyReadMessage);
            reading = false;
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
                handleRegistrationRequest(msg);
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
            logger.debug("CH handling registration request");
        }

        AgentID newAgentID = RegistrationMessage.readAgentID(msg);
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
            logger.debug("CH handling a re-connection request for agentID: " + agentID.getId());
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

        connected = false; // this is a transition ChannelHandler...

        // if the received agentID is known, proceed to the reconnection of a disconnected former ChannelHandler
        formerCH.processFormerChannelHandlerReconnection(sc);
    }

    /**
     * Handles a new connection: attributes a unique agentID, puts mapping in the right HashMap of the router, send a registration reply
     */
    private void handleNewConnection() {
        // generate the agentID
        this.agentID = new AgentID(attributedAgentID.getAndIncrement());

        // add mapping in the HashMap
        router.putMapping(agentID, this);
        if (logger.isDebugEnabled()) {
            logger.debug("CH added new mapping for uniqueID " + agentID.getId());
        }
        // Prepare registration reply and write it
        write(this, new RegistrationReplyMessage(agentID).toByteBuffer(), false);
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
        write(this, new RegistrationReplyMessage(agentID).toByteBuffer(), false);
    }

    private void handleDataMessage(ByteBuffer msg, MessageType type) {
        
        ChannelHandler dstChannelHandler = null;
        AgentID dstAgentID = ForwardedMessage.readDstAgentID(msg);
        

        if (logger.isDebugEnabled()) {
            AgentID srcAgentID = ForwardedMessage.readSrcAgentID(msg);
            logger.debug("CH -> handling data message from "+srcAgentID+" to "+dstAgentID);
        }

        try {
            dstChannelHandler = router.getValueFromHashMap(dstAgentID);
        }
        // if dstChannelHandler is null we catch an UnknownAgentIdException
        catch (UnknownAgentIdException e) {
        	logger.warn("CH -> No channel handler found for destination ID "+dstAgentID, e);
            write(this, new ErrorMessage(MessageType.ERR_UNKNOW_RCPT, dstAgentID, agentID, ForwardedMessage
                    .readMessageID(msg), e).toByteBuffer(), false);
            return;
        }
        // the recipient is known, check if it is connected
        if (!dstChannelHandler.isConnected()) {
        	 if (logger.isDebugEnabled()) {
                 logger.warn("CH -> destination ID "+dstAgentID+" is known but disconnected.");
             }
            // the recipient is known but disconnected
            handleDisconnectedRecipient(msg, type, dstAgentID, dstChannelHandler,
                    new AgentNotConnectedException("dstAgentID[" + dstAgentID.getId() + "] disconnected"));
        } else {
            // at this point the recipient is known, and connected, forward the message
       	 if (logger.isDebugEnabled()) {
             logger.warn("CH -> Forwarding the message to Channel handler in charge of ID "+dstAgentID);
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
                    logger.debug("CH recipient disconnected, DataRequest [srcAgentID: " + agentID.getId() +
                        "] [dstAgentID: " + dstAgentID.getId() +
                        "] could not be sent, returning error message");
                }
                // send error message
                write(this, new ErrorMessage(MessageType.ERR_DISCONNECTED_RCPT, dstAgentID, agentID,
                    ForwardedMessage.readMessageID(msg), e).toByteBuffer(), false);
                break;
            case DATA_REPLY: // cache reply
                if (logger.isDebugEnabled()) {
                    logger.debug("CH recipient disconnected, DataReply [srcAgentID: " + agentID.getId() +
                        "] [dstAgentID: " + dstAgentID.getId() + "] could not be sent, caching reply");
                }
                // cache the reply (at this point the status of dstChannelHandler should be not connected)
                dstChannelHandler.write(this, msg, false);
                break;
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
         * In this case we by pass the test (!writing && connected), because this channel handler can't be writing (it is trying to connect) and we need to send the message in the case of a reconnection
         */
        if ((Message.readType(msg) == MessageType.REGISTRATION_REPLY) || (!writing && connected)) {
            MessageProcessor msgProcessor = new MessageProcessor(srcChannelHandler, this, msg);
            router.submitTask(msgProcessor);
            writing = true;
        } else {
            if (first) {
                messagesToWrite.add(0, msg);
            } else {
                messagesToWrite.add(msg);
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
        if (!messagesToWrite.isEmpty() && connected) {
            ByteBuffer msg = messagesToWrite.remove(0); // is necessarily a forwarded message
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

}
