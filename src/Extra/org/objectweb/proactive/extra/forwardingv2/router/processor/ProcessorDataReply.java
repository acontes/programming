package org.objectweb.proactive.extra.forwardingv2.router.processor;

import java.nio.ByteBuffer;

import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DataRequestMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ForwardedMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;
import org.objectweb.proactive.extra.forwardingv2.router.Client;
import org.objectweb.proactive.extra.forwardingv2.router.Router;

public class ProcessorDataReply extends Processor {

    final private ByteBuffer messageAsByteBuffer;
    final private Router router;

    public ProcessorDataReply(ByteBuffer messageAsByteBuffer, Router router) {
        this.messageAsByteBuffer = messageAsByteBuffer;
        this.router = router;
    }

    @Override
    public void process() {
        AgentID agentId = ForwardedMessage.readDstAgentID(messageAsByteBuffer.array());
        Client destClient = this.router.getClient(agentId);

        if (destClient != null) {
        	/* The recipient is known. Try to forward the message.
        	 * If the reply cannot be send now, we have to cache it to send it later.
        	 * We don't want to send a error message to the sender. Our goal is to unblock
        	 * the recipient which is waiting for the reply
        	 */
        	destClient.sendMessageOrCache(this.messageAsByteBuffer);
        } else {
        	/* The recipient is unknown.
        	 * 
        	 * We can't do better than dropping the reply. Notifying the sender is useless since
        	 * it will not unblock the recipient. 
			 */
        	try {
        		Message message;
        		message = new DataRequestMessage(messageAsByteBuffer.array(), 0);
        		logger.error("Dropped invalid data reply: unknown recipient. " + message);
        	} catch (InstantiationException e) {
					ProActiveLogger.logImpossibleException(logger, e);
			}
        }
    }
}
