package org.objectweb.proactive.extra.forwardingv2.registry.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message.MessageType;


public class MessageProcessor implements Runnable {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_ROUTER);

    private final ChannelHandler srcChannelHandler;
    private final ChannelHandler dstChannelHandler;
    private final AgentID dstAgentID;
    private final SocketChannel sc;
    private final ByteBuffer msg;
    private final MessageType type;

    /** 
     * 
     * @param srcChannelHandler
     * @param dstChannelHandler
     * @param msg The message should be ready to be read
     */
    public MessageProcessor(ChannelHandler srcChannelHandler, ChannelHandler dstChannelHandler, ByteBuffer msg) {
        if (!msg.hasRemaining()) {
            logger.warn("Message not ready to be read, flipping it. ");
            msg.flip();
        }
        this.srcChannelHandler = srcChannelHandler;
        this.dstChannelHandler = dstChannelHandler;
        this.dstAgentID = dstChannelHandler.getAgentID();
        this.sc = dstChannelHandler.getSc();
        this.msg = msg;
        this.type = Message.readType(msg);
    }

    public void run() {

        logger.trace("Message to write: " + msg.remaining());

        switch (type) {
            case REGISTRATION_REPLY:
                processRegistrationReply();
                break;
            case DATA_REPLY:
                processDataReply();
                break;
            case DATA_REQUEST:
                processDataRequest();
                break;
            case ERR_DISCONNECTED_RCPT:
            case ERR_UNKNOW_RCPT:
                processErrorMsg();
                break;
            default:
                break;
        }

        // submit next message
        dstChannelHandler.submitNextMessage();
    }

    /**
     * Takes care of writing the totality of the message in the socketChannel
     * @throws IOException
     */
    private void write() throws IOException {
        int nbToWrite = msg.remaining();

        int nbWritten = 0;

        synchronized (sc) {
            while (nbWritten < nbToWrite) {
                nbWritten += sc.write(msg);
            }
        }
    }

    /**
     * try sending the registration reply.
     * If it works, then set the status of the ChannelHandler to connected.
     * Else log the failure and clean the router
     */
    private void processRegistrationReply() {
        try {
            write();
        } catch (IOException e) {
            // could not send a registration Reply, log error and clean router
            if (logger.isDebugEnabled()) {
                logger.warn("Failed to send registration reply for dstAgentID: " + dstAgentID.getId() +
                    ". Cleaning this connection");
            }
            // the cleaning is not the same if it was a reply to a reconnection or a reply to a new connection			
            dstChannelHandler.stop(dstChannelHandler.isFirstConnection());
            return;
        }
        dstChannelHandler.setConnected(true);
        dstChannelHandler.setFirstConnection(false);
    }

    private void processDataReply() {
        try {
            write();
        } catch (IOException e) {
            // could not send a Data Reply, log error, cache reply, clean router
            if (logger.isDebugEnabled()) {
                logger.warn("Failed to send data reply for dstAgentID: " + dstAgentID.getId() +
                    ". Caching reply and Cleaning this connection");
            }
            // clean handler
            dstChannelHandler.stop(false);
            // cache reply
            dstChannelHandler.write(srcChannelHandler, msg, true);
            return;
        }
    }

    private void processDataRequest() {
        try {
            write();
        } catch (IOException e) {
            // could not send a Data Request, log error, return error message, clean router
            if (logger.isDebugEnabled()) {
                logger.warn("Failed to send data request for dstAgentID: " + dstAgentID.getId() +
                    ". Returning error message to sender and Cleaning this connection");
            }
            // clean router
            dstChannelHandler.stop(false);
            // return error message
            srcChannelHandler.write(dstChannelHandler, msg, false);
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.trace("Data request correctly sent to destination: " + dstAgentID);
        }
    }

    private void processErrorMsg() {
        try {
            write();
        } catch (IOException e) {
            // could not send an Error Message, log error, cache Error Message, clean router
            if (logger.isDebugEnabled()) {
                logger.warn("Failed to send error message for dstAgentID: " + dstAgentID.getId() +
                    ". Caching Error Message and Cleaning this connection");
            }
            // clean handler
            dstChannelHandler.stop(false);
            // cache ErrorMessage
            dstChannelHandler.write(srcChannelHandler, msg, true);
            return;
        }
    }
}
