package org.objectweb.proactive.extra.forwardingv2.router.processor;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.objectweb.proactive.extra.forwardingv2.protocol.message.DebugMessage;
import org.objectweb.proactive.extra.forwardingv2.router.Attachment;
import org.objectweb.proactive.extra.forwardingv2.router.Router;


public class ProcessorDebug extends Processor {
    DebugMessage message;
    Attachment attachment;
    Router router;

    public ProcessorDebug(ByteBuffer message, Attachment attachment, Router router) {
        this.attachment = attachment;
        this.router = router;

        try {
            this.message = new DebugMessage(message.array(), 0);
        } catch (InstantiationException e) {
            logger.warn(e);
            this.message = null;
        }

    }

    @Override
    public void process() {

        switch (message.getErrorType()) {
            case DEB_DISCONNECT:
                disconnect();
                break;
            case DEB_NOOP:
                logger.info("noop message received " + message);
                break;
            default:
                logger.error("Unhandled message " + message);
                break;
        }
    }

    private void disconnect() {
        Field f;
        try {
            f = this.attachment.getClass().getDeclaredField("socketChannel");
            f.setAccessible(true);
            SocketChannel socketChannel = (SocketChannel) f.get(this.attachment);
            socketChannel.socket().close();
            socketChannel.close();
        } catch (Exception e) {
            logger.error(e);
        }
    }

}
