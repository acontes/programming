package org.objectweb.proactive.extra.forwardingv2.router;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message.MessageType;
import org.objectweb.proactive.extra.forwardingv2.router.processor.Processor;
import org.objectweb.proactive.extra.forwardingv2.router.processor.ProcessorData;
import org.objectweb.proactive.extra.forwardingv2.router.processor.ProcessorDebug;
import org.objectweb.proactive.extra.forwardingv2.router.processor.ProcessorRegistrationRequest;


public class TopLevelProcessor implements Runnable {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_ROUTER);

    final private ByteBuffer message;
    final private Attachment attachment;
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
            case DATA_REQUEST:
                processor = new ProcessorData(this.message, this.router);
                break;
            case DEBUG_:
                processor = new ProcessorDebug(this.message, this.attachment, this.router);
                break;
            default:
                logger.error("Unhandled message type " + type);
                break;
        }

        if (processor != null) {
            processor.process();
        } else {
            logger.error("Processor is null", new Exception());
        }
    }
}
