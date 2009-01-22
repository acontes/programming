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

    ChannelHandler channelHandler;
    AgentID agentID;
    SocketChannel sc;
    ByteBuffer msg;

    public MessageProcessor(ChannelHandler channelHandler, AgentID agentID, SocketChannel sc, ByteBuffer msg) {
        this.channelHandler = channelHandler;
        this.agentID = agentID;
        this.sc = sc;
        this.msg = msg;
    }

    @Override
    public void run() {

        msg.flip();

        MessageType type = Message.readType(msg);
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
        channelHandler.submitNextMessage();
    }

    private void processRegistrationReply() {
        try {
            sc.write(msg);
        } catch (IOException e) {
            // could not send a registration Reply, log error and clean HashMaps
            if (logger.isDebugEnabled()) {
                logger.warn("Failed to send a registration reply for agentID: " + agentID.getId() +
                    ". Cleaning this connection");
            }
            // the cleaning is not the same if it was a reply to a reconnection or a reply to a new connection			
            // if it was a new connection: the channelHandler status is true by default
            // Else if it was a reconnection: the channelHandler status is false at this point
            channelHandler.stop(!channelHandler.isConnected());
        }
    }

    private void processDataReply() {

    }

    private void processDataRequest() {

    }

    private void processErrorMsg() {

    }

}
