package org.objectweb.proactive.extra.forwardingv2.router;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message.MessageType;
import org.objectweb.proactive.extra.forwardingv2.router.processor.Processor;
import org.objectweb.proactive.extra.forwardingv2.router.processor.ProcessorDataReply;
import org.objectweb.proactive.extra.forwardingv2.router.processor.ProcessorDataRequest;
import org.objectweb.proactive.extra.forwardingv2.router.processor.ProcessorDebug;
import org.objectweb.proactive.extra.forwardingv2.router.processor.ProcessorRegistrationRequest;


/** Asynchronous message handler.
 * 
 * Each message received is asynchronously handled by a {@link TopLevelProcessor}.
 * This class dispatch the work to a dedicated message {@link Processor} according
 * to the type of the message.
 */
public class TopLevelProcessor implements Runnable {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_ROUTER);

    /** The message to process */
    final private ByteBuffer message;

    /** The attachment which received the message
     * 
     * Should NOT be passed to the processor, ProcessorRegistrationRequest excepted
     * since we need the attachment to create a new Client. 
     */
    final private Attachment attachment;

    /** The local router */
    final private Router router;

    public TopLevelProcessor(ByteBuffer message, Attachment attachment, Router router) {
        this.message = message;
        this.attachment = attachment;
        this.router = router;
    }

    public void run() {
        if (logger.isTraceEnabled()) {
            Message message = Message.constructMessage(this.message.array(), 0);
            logger.trace("Asynchronous handling of " + message);
        }

        MessageType type = Message.readType(message);
        Processor processor = null;
        switch (type) {
            case REGISTRATION_REQUEST:
                processor = new ProcessorRegistrationRequest(this.message, this.attachment, this.router);
                break;
            case DATA_REPLY:
                processor = new ProcessorDataReply(this.message, this.router);
                break;
            case DATA_REQUEST:
                processor = new ProcessorDataRequest(this.message, this.router);
                break;
            case DEBUG_:
                processor = new ProcessorDebug(this.message, this.attachment, this.router);
                break;
            default:
                Message msg = Message.constructMessage(message.array(), 0);
                logger.error("Unexpected message type: " + type + ". Dropped message " + msg);
                break;
        }

        if (processor != null) {
            processor.process();
        }
    }
}
