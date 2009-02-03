package org.objectweb.proactive.extra.forwardingv2.router;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;


public class MessageAssembler {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_ROUTER);

    final private Router router;
    final private Attachment attachment;

    private ByteBuffer currentMessage;
    private LengthAndProto lengthAndProto;

    public MessageAssembler(Router router, Attachment attachment) {
        this.router = router;
        this.attachment = attachment;

        this.currentMessage = null;
        this.lengthAndProto = null;
    }

    synchronized public void pushBuffer(ByteBuffer buffer) {

        while (buffer.remaining() != 0) {

            if (this.currentMessage == null) {

                if (this.lengthAndProto == null) {
                    this.lengthAndProto = new LengthAndProto();
                }

                while (buffer.remaining() > 0 && !lengthAndProto.isReady()) {
                    lengthAndProto.push(buffer.get());
                }

                if (lengthAndProto.isReady()) {
                    // Check the protocol is correct. Otherwise something is really fucked up
                    int proto = lengthAndProto.getProto();
                    if (lengthAndProto.getProto() != Message.PROTOV1) {
                        logger.error("Invalid protocol ID received from " + attachment + ": expected=" +
                            Message.PROTOV1 + " received=" + proto);
                        // TODO: close the socket
                    }

                    int l = this.lengthAndProto.getLength();
                    // Allocate a buffer for the reassembled message
                    currentMessage = ByteBuffer.allocate(l);

                    // Buffer position is no more 0, we copy the data that have been read
                    // by the previous loop
                    currentMessage.putInt(l);
                    currentMessage.putInt(proto);
                } else {
                    // Length is still not available, it means that buffer.remaing() has been reached
                    // We can safely exit the loop
                    break;
                }
            }

            // This point can only be reached if length & proto have been read
            // currentMessage is not null

            // Number of bytes missing to complete the currentMessage
            int missingBytes = currentMessage.remaining();
            // Number of bytes available in the buffer
            int availableBytes = buffer.remaining();

            int toCopy = missingBytes > availableBytes ? availableBytes : missingBytes;

            // Don't use put(ByteBuffer) it does NOT use the limit
            currentMessage.put(buffer.array(), buffer.position(), toCopy);
            buffer.position(buffer.position() + toCopy);

            // Checks if current message is complete
            if (currentMessage.remaining() == 0) {
                if (logger.isDebugEnabled()) {
                    String dest = this.attachment.getClient() == null ? " unknown" : this.attachment
                            .getClient().toString();
                    logger.debug("Assembled one message for client " + dest);
                }

                this.router.handleAsynchronously(currentMessage, this.attachment);
                this.currentMessage = null;
                this.lengthAndProto = null;
            }
        }
    }

    static class LengthAndProto {
        static private int SIZE = Message.Field.LENGTH.getLength() + Message.Field.PROTO_ID.getLength();

        private byte[] buf;
        private int index;

        protected LengthAndProto() {
            buf = new byte[SIZE];
            index = 0;
        }

        protected void push(byte b) {
            buf[index++] = b;
        }

        protected boolean isReady() {
            return index == SIZE;
        }

        protected int getLength() {
            return Message.readLength(buf, 0);
        }

        protected int getProto() {
            return Message.readProtoID(buf, 0);
        }
    }
}
