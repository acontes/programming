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
            currentlyReadMessage = ByteBuffer.allocate(length);
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
            case ERR_DISCONNECTED_RCPT:
            case ERR_UNKNOW_RCPT:
                handleForwardedMessage(msg);
                break;
            default:
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

        //TODO: check this
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
        write(new RegistrationReplyMessage(agentID).toByteBuffer());
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
        write(new RegistrationReplyMessage(agentID).toByteBuffer());
    }

    private void handleForwardedMessage(ByteBuffer msg) {
        ChannelHandler dstChannelHandler = null;
        AgentID dstAgentID = ForwardedMessage.readDstAgentID(msg);

        try {
            dstChannelHandler = router.getValueFromHashMap(dstAgentID);
        }
        // if channelHandler is null we catch an UnknownAgentIdException
        catch (UnknownAgentIdException e) {
            write(new ErrorMessage(MessageType.ERR_UNKNOW_RCPT, dstAgentID, agentID, ForwardedMessage
                    .readMessageID(msg), e).toByteBuffer());
            return;
        }

        // else just forward the message
        // TODO: handle the Remote connection problem (either here or in the write() function)
        //		try {
        dstChannelHandler.write(msg);
        /*		} catch (RemoteConnectionBrokenException e) {
         // could not send the message to its destination, notify the source by sending an ExceptionMessage
         try {
         sendMessage(new ErrorMessage(MessageType.ERR_DISCONNECTED_RCPT, dstAgentID, agentID, msg
         .getMsgID(), e).toByteArray());
         } catch (RemoteConnectionBrokenException e1) {
         // could not send a notification of the failure to the source, because the source tunnel has also failed... just stop the source tunnel
         stop();
         }
         }
         */

    }

    /**
     * asks a thread of the thread pool to write the message. 
     * If a thread was already called (writing is true), then puts the message in the list of messagesToWrite, else submit a runnable to the thread pool. 
     * The runnable writes the message and then asks if it should send another message (ie if checks if the arrayList of messages to be written is empty or not). 
     * If yes it processes the new message, else it is released.
     * @param buffer
     */

    private void write(ByteBuffer msg) {
        if (!writing) {
            MessageProcessor msgProcessor = new MessageProcessor(this, agentID, sc, msg);
            router.submitTask(msgProcessor);
            writing = true;
        } else {
            messagesToWrite.add(msg);
        }
    }

    /**
     * check if there is a new message to submit for this channelHandler,
     * if yes, submit it
     * Else set writing to false 
     */
    public void submitNextMessage() {
        if (!messagesToWrite.isEmpty()) {
            MessageProcessor msgProcessor = new MessageProcessor(this, agentID, sc, messagesToWrite.remove(0));
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

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

}
