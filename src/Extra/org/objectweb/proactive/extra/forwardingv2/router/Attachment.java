package org.objectweb.proactive.extra.forwardingv2.router;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * An attachment belongs to a client as soon as a registration reply
 * have been send. One and only one attachment can exist for any client at
 * a given time.
 *
 * The attachment is in charge of to thing:
 * <ul>
 * 	<li>
 * 		<b>Front end</b>: it is a statefull entity which is in charge of reassembling
 * 		the message from chunks of data.
 * 	</li>
 * 	<li>
 * 		<b>Back end</b>: it holds the only reference onto the SocketChannel associated
 * 		to the current tunnel.
 * 	</li>
 * </ul>
 *
 *
 */
public class Attachment {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_ROUTER);

    /** The id of this attachment
     *
     * Never used by any other object but can be usefull when debugging
     */
    final private long attachmentId;

    /** The client */
    /* Not final because can't be set in the constructor.
     * Once this field is set, it MUST NOT be updated again.
     */
    private Client client = null;

    /** The assembler is charge of reassembling the message for this given client */
    final private MessageAssembler assembler;

    /** The socket channel where to write for this given client */
    final private SocketChannel socketChannel;

    public Attachment(Router router, SocketChannel socketChannel) {
        this.attachmentId = AttachmentIdGenerator.getId();
        this.assembler = new MessageAssembler(router, this);
        this.socketChannel = socketChannel;
        this.client = null;
    }

    public MessageAssembler getAssembler() {
        return assembler;
    }

    public long getAttachmentId() {
        return attachmentId;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("attachmentId= " + attachmentId + " ");
        sb.append("socketChannel=" + socketChannel.socket() + " ");
        sb.append("client=" + (client == null ? "unknown" : client.getAgentId()) + " ");
        return sb.toString();
    }

    public void setClient(Client client) {
        if (this.client == null) {
            this.client = client;
        } else {
            logger.warn("Attachement.setClientId() cannot be called twice. Current client: " + this.client +
                ", discarded: " + client);
        }
    }

    public void send(int timeout, ByteBuffer byteBuffer) throws IOException {
        /* SocketChannel ARE thread safe.
         * Extra locking to ensure serialization of the calls will be useless
         */
        byteBuffer.clear();
        int bytes = this.socketChannel.write(byteBuffer);

        if (logger.isDebugEnabled()) {
            String dstClient = this.client == null ? "unknown" : client.getAgentId().toString();
            logger.debug("Sent a " + bytes + " bytes message to client " + dstClient + " with " +
                this.socketChannel.socket());
        }
    }

    static abstract private class AttachmentIdGenerator {
        static final private AtomicLong generator = new AtomicLong(0);

        static public long getId() {
            return generator.getAndIncrement();
        }
    }
}