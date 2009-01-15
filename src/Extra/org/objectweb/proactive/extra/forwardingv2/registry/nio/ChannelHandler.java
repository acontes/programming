package org.objectweb.proactive.extra.forwardingv2.registry.nio;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public class ChannelHandler {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_ROUTER);

    private AgentID agentID = null;
    private SocketChannel sc;

    private ByteBuffer currentlyReadByteBuffer = null;
    private boolean writing = false;

    private final ArrayList<ByteBuffer> readMessages = new ArrayList<ByteBuffer>();
    private final ArrayList<ByteBuffer> messagesToWrite = new ArrayList<ByteBuffer>();

    public ChannelHandler(SocketChannel sc) {
        this.sc = sc;
    }

    /**
     * HAndles registration
     * adds the content of a byte buffer to the message being aggregated and once it is full, forwards it to the right channel for transmission
     * @param buffer
     */
    public void putBuffer(ByteBuffer buffer) {
    }

    /**
     * asks a thread of the thread pool to write the message. 
     * If a thread was already called (writing is true), then doesn't do anything, else submit a runnable to the thread pool. 
     * The runnable writes the message and then asks if it should send another message (ie if checks if the arrayList of messages to be written is empty or not). 
     * If yes it processes the new message, else it is released.
     * @param buffer
     */
    private void write(ByteBuffer buffer) {
    }

}
