/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extra.messagerouting.client.dc.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;


/**
 * Manages a single direct connection with a remote agent
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class DirectConnection {

    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_CLIENT_DC);

    // the Direct Connection Manager
    private final DirectConnectionManager dcMan;
    // the remote agent to which we are trying to connect
    private final AgentID remoteAgent;
    // the communication channel
    private final SocketChannel channel;
    // marks if the connection was established succesfully
    private volatile boolean connectionEstablished;
    // the queue of pending messages
    private final Queue<ByteBuffer> pendingMessages;

    public DirectConnection(DirectConnectionManager manager, AgentID remoteAgent) throws IOException {
        this.dcMan = manager;
        this.remoteAgent = remoteAgent;
        this.pendingMessages = new ConcurrentLinkedQueue<ByteBuffer>();
        // open a non-blocking channel
        this.channel = SocketChannel.open();
        this.channel.configureBlocking(false);
    }

    public SocketChannel getChannel() {
        return this.channel;
    }

    /**
     * Attempt to connect to a remote DC server
     *  
     * @param remote - the remote endpoint address
     * @return true if the connection is already established
     * @throws IOException if the connect attempt fails
     */
    public boolean connect(InetSocketAddress remote) throws IOException {
        this.connectionEstablished = channel.connect(remote);
        return this.connectionEstablished;
    }

    public void handleConnect(boolean successFlag) {
        this.connectionEstablished = successFlag;
        this.dcMan.connectionFinished(remoteAgent, this, successFlag);

    }

    // synchronize on this Direct Connection; 
    // only one thread should write 
    // on the underlying Channel. To allow multiple threads 
    // to do that is pointless since only one thread could 
    // get into the write() on the channel 
    public synchronized void handleWrite() throws IOException {
        if (!connectionEstablished)
            throw new IllegalStateException("The direct connection is not yet established");

        // now it is possible to write
        while (!pendingMessages.isEmpty()) {
            ByteBuffer currentMessage = pendingMessages.peek();
            while (currentMessage.hasRemaining()) {
                int writtenBytes = this.channel.write(currentMessage);
                if (writtenBytes == 0) {
                    logger.debug("No more space to write in the buffer of socket " +
                        this.channel.socket().getRemoteSocketAddress().toString());
                    // try later when available
                    return;
                } else {
                    if (logger.isDebugEnabled()) {
                        String remaining = currentMessage.remaining() > 0 ? currentMessage.remaining() +
                            " remaining to send" : "";
                        logger.debug("Sent a " + writtenBytes + " bytes message to Agent " + this.remoteAgent +
                            " socket " + this.channel.socket().getRemoteSocketAddress().toString() + ". " +
                            remaining);
                    }
                }
            }
            pendingMessages.poll();
        }
    }

    public void push(Message msg) throws IOException {
        // put it in the list of pending messages
        ByteBuffer msgBuffer = ByteBuffer.wrap(msg.toByteArray());
        if (logger.isDebugEnabled())
            logger.debug("Message " + msg + " put in the queue for transmission to the Agent " +
                this.remoteAgent + " at the endpoint " +
                this.channel.socket().getRemoteSocketAddress().toString());
        pendingMessages.add(msgBuffer); // unbounded queue; cannot fail due to capacity limitation
    }

    public void close() throws IOException {
        // close the socket
        channel.socket().close();
        // close the channel
        channel.close();
    }

    @Override
    public String toString() {
        return " AgentID " + this.remoteAgent + " endpoint " + this.channel.socket().getRemoteSocketAddress() != null ? this.channel
                .socket().getRemoteSocketAddress().toString()
                : "unknown";
    }

}
