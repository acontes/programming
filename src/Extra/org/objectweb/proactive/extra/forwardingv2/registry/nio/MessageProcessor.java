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

    ChannelHandler srcChannelHandler;
    ChannelHandler dstChannelHandler;
    AgentID dstAgentID;
    SocketChannel sc;
    ByteBuffer msg;
    MessageType type;

    public MessageProcessor(ChannelHandler srcChannelHandler, ChannelHandler dstChannelHandler, ByteBuffer msg) {
        this.srcChannelHandler = srcChannelHandler;
        this.dstChannelHandler = dstChannelHandler;
        this.dstAgentID = dstChannelHandler.getAgentID();
        this.sc = dstChannelHandler.getSc();
        this.msg = msg;
        this.type = Message.readType(msg);
    }

    public void run() {

        msg.flip();

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
     * try sending the registration reply.
     * If it works, then set the status of the ChannelHandler to connected.
     * Else log the failure and clean the router
     */
    private void processRegistrationReply() {
        try {
            sc.write(msg);
        } catch (IOException e) {
            // could not send a registration Reply, log error and clean router
            if (logger.isDebugEnabled()) {
                logger.warn("Failed to send registration reply for dstAgentID: " + dstAgentID.getId() +
                    ". Cleaning this connection");
            }
            // the cleaning is not the same if it was a reply to a reconnection or a reply to a new connection			
            // if it was a new connection: the dstChannelHandler status is true by default
            // Else if it was a reconnection: the dstChannelHandler status is false at this point
            dstChannelHandler.stop(!dstChannelHandler.isConnected());
            return;
        }
        dstChannelHandler.setConnected(true); // for the case of a reconnection
    }

    private void processDataReply() {
        try {
            sc.write(msg);
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
            sc.write(msg);
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
    }

    private void processErrorMsg() {
        try {
            sc.write(msg);
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
