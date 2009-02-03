package org.objectweb.proactive.extra.forwardingv2.router.processor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ErrorMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ForwardedMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ErrorMessage.ErrorType;
import org.objectweb.proactive.extra.forwardingv2.router.Client;
import org.objectweb.proactive.extra.forwardingv2.router.Router;


public class ProcessorData extends Processor {
    final private ByteBuffer messageAsByteBuffer;
    final private Router router;

    public ProcessorData(ByteBuffer messageAsByteBuffer, Router router) {
        this.messageAsByteBuffer = messageAsByteBuffer;
        this.router = router;
    }

    @Override
    public void process() {
        AgentID agentId = ForwardedMessage.readDstAgentID(messageAsByteBuffer.array());
        Client destClient = this.router.getClient(agentId);

        if (destClient != null) {
            try {
                destClient.sendMessage(DEFAULT_TIMEOUT, this.messageAsByteBuffer);
            } catch (Exception e) {
                // Notify the sender of the failure
                AgentID srcAgentId = ForwardedMessage.readSrcAgentID(messageAsByteBuffer.array(), 0);
                long messageId = Message.readMessageID(messageAsByteBuffer.array(), 0);
                ErrorMessage error = new ErrorMessage(srcAgentId, agentId, messageId,
                    ErrorType.ERR_NOT_CONNECTED_RCPT);

                Client srcClient = router.getClient(srcAgentId);
                // Cache the message is the sender is currently disconnected
                srcClient.sendMessageOrCache(0, error.toByteArray());
            }
        } else {
            ForwardedMessage message = (ForwardedMessage) Message.constructMessage(messageAsByteBuffer
                    .array(), 0);
            AgentID srcAgentId = message.getSrcAgentID();
            Client client = router.getClient(srcAgentId);
            if (client != null) {
                logger.warn("Received invalid: unknow reciptient: " + agentId + ". Notifying sender");
                // Cache on error to avoid a blocked a sender
                ErrorMessage error = new ErrorMessage(srcAgentId, message.getMessageID(),
                    ErrorType.ERR_UNKNOW_RCPT);
                client.sendMessageOrCache(DEFAULT_TIMEOUT, error.toByteArray());
            } else {
                // Something is utterly broken: Unknown sender & recipient
                logger.error("Dropped invalid message: unknown sender and recipient, " + message);
            }
        }

    }
}
