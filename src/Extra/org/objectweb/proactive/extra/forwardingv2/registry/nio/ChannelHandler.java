package org.objectweb.proactive.extra.forwardingv2.registry.nio;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
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

    static final AtomicLong attributedAgentID = new AtomicLong(1);

    private AgentID agentID = null;
    private Router router;
    private SocketChannel sc;

    private ByteBuffer currentlyReadMessage = null;
    private boolean reading = false;

    private final ArrayList<ByteBuffer> messagesToWrite = new ArrayList<ByteBuffer>();

    public ChannelHandler(Router router, SocketChannel sc) {
        this.router = router;
        this.sc = sc;
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

        // if the buffer contains the last part of the currently read message and the first part of a new message
        if (buffer.remaining() > currentlyReadMessage.remaining()) {
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

        // check if the buffer contained just the last part of the currently read message
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
        if (newAgentID != null) {
            this.agentID = newAgentID;
        } else {
            this.agentID = new AgentID(attributedAgentID.getAndIncrement());
        }
        // add mapping in the HashMap
        router.putMapping(agentID, this);
        if (logger.isDebugEnabled()) {
            logger.debug("RH added new mapping for uniqueID " + agentID.getId());
        }

        // Prepare registration reply and put it in the messagesToWrite array
        write(new RegistrationReplyMessage(agentID).toByteBuffer());
    }

    private void handleForwardedMessage(ByteBuffer msg) {
        ChannelHandler channelHandler = null;
        AgentID dstAgentID = ForwardedMessage.readDstAgentID(msg);

        try {
            channelHandler = router.getChannelHandlerFromAgentID(dstAgentID);
        }
        // if channelHandler is null we catch an UnknownAgentIdException
        catch (UnknownAgentIdException e) { //TODO: check if it is possible to use less parameters
            write(new ErrorMessage(MessageType.ERR_UNKNOW_RCPT, dstAgentID, agentID, ForwardedMessage
                    .readMessageID(msg), e).toByteBuffer());
            return;
        }

        // else just forward the message
        // TODO: handle the Remote connection problem (either here or in the write() function)
        //		try {
        channelHandler.write(msg);
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
    }

}
