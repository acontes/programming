package org.objectweb.proactive.extra.forwardingv2.router;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public class Client {
    static final private Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_ROUTER);

    /** This client represents one remote Agent. */
    final private AgentID agentId;

    /**
     * Current attachment
     *
     * Change each time the client reconnects.
     *
     * Set to null each time the router detects the remote agent is
     * disconnected. But a non null value does not mean the remote agent is
     * connected (can fail any time)
     */
    private Attachment attachment;

    /** This lock must be held each time the attachment is used.
     *
     * It ensure that one and only one client will update or discard the
     * attachment
     */
    final private Object attachment_lock = new Object();

    /** List of messages to be sent when the client will reconnect */
    final private Queue<ByteBuffer> pendingMessage;

    public Client(Attachment attachment, AgentID agentID) {
        this.attachment = attachment;
        this.attachment.setClient(this);
        this.agentId = agentID;
        this.pendingMessage = new ConcurrentLinkedQueue<ByteBuffer>();
    }

    public void sendMessage(int timeout, ByteBuffer message) throws IOException, TimeoutException {
        // attachment is not allowed to change while sending the message
        synchronized (this.attachment_lock) {
            try {
                if (this.attachment != null) {
                    this.attachment.send(timeout, message);
                } else {
                    throw new IOException("Client " + this.agentId + " is not connected");
                }
            } catch (IOException e) {
                // The tunnel just failed. Discard the current attachment and
                // wait
                // for client reconnection
                this.discardAttachment();
                throw e;
            }
        }
    }

    public void sendMessageOrCache(int timeout, ByteBuffer message) {
        // attachment is not allowed to change while sending the message
        synchronized (this.attachment_lock) {
            try {
                if (this.attachment != null) {
                    attachment.send(timeout, message);
                } else {
                    this.pendingMessage.add(message);
                }
            } catch (IOException e) {
                // The tunnel just failed. Discard the current attachment and
                // wait
                // for client reconnection
                this.discardAttachment();
                this.pendingMessage.add(message);
            }
        }
    }

    public void sendMessage(int timeout, byte[] message) throws IOException, TimeoutException {
        this.sendMessage(timeout, ByteBuffer.wrap(message));
    }

    public void sendMessageOrCache(int timeout, byte[] message) {
        this.sendMessageOrCache(timeout, ByteBuffer.wrap(message));
    }

    public void discardAttachment() {
        synchronized (this.attachment_lock) {
            logger.debug("Discarded attachment for " + this.agentId);
            this.attachment = null;
        }
    }

    public void setAttachment(Attachment attachment) {
        synchronized (this.attachment_lock) {
            logger.debug("New attachment for " + this.agentId);
            this.attachment = attachment;
            this.attachment.setClient(this);
        }
    }

    public void send_pending_message() {
        /* Coarse grained locking: should be improved
         *
         * This lock currently ensure that their is no race condition
         * between add() and peek()
         * The justification of this global lock is that it is safe and easy to
         * implement _AND_ flushing all the pending messages before forwarding new ones
         * sound reasonable.
         */
        synchronized (this.attachment_lock) {
            ByteBuffer msg;
            while ((msg = this.pendingMessage.peek()) != null) {
                try {
                    this.sendMessage(0, msg);
                    this.pendingMessage.remove(msg);
                } catch (Exception e) {
                    // The tunnel failed again and the attachment has been set
                    // to null. Nothing we can do. This method will be called again on
                    // client connection
                    break;
                }
            }
        }
    }

    public AgentID getAgentId() {
        return this.agentId;
    }
}