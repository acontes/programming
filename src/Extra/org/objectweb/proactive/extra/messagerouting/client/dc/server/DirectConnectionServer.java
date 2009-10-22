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
package org.objectweb.proactive.extra.messagerouting.client.dc.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.SweetCountDownLatch;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.messagerouting.client.AgentInternal;
import org.objectweb.proactive.extra.messagerouting.exceptions.MalformedMessageException;
import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DirectConnectionAdvertiseMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;
import org.objectweb.proactive.extra.messagerouting.router.Router;


/**
 * The local server for direct connections
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class DirectConnectionServer implements Runnable {

    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_SERVER_DC);
    /** Read {@link ByteBuffer} size. */
    private final static int READ_BUFFER_SIZE = 4096;
    private Selector selector = null;
    private ServerSocketChannel ssc = null;

    private ExecutorService tpe = null;
    // true if we have done resource cleanup
    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final SweetCountDownLatch isStopped = new SweetCountDownLatch(1);
    private final AtomicReference<Thread> selectThread = new AtomicReference<Thread>();
    private static final long CLEANUP_WAITING_TIME = 500L;
    // provide a shutdown hook for users
    private final Thread shutdownHook;

    private final AgentInternal localAgent;

    public DirectConnectionServer(AgentInternal agent, DirectConnectionServerConfig config)
            throws IOException {
        this.tpe = Executors.newCachedThreadPool();
        this.localAgent = agent;
        init(config);
        this.shutdownHook = new Thread(new ShutdownHook(this));
    }

    private void init(DirectConnectionServerConfig config) throws IOException {
        // Create a new selector
        selector = Selector.open();

        // Open a listener on the right port
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ServerSocket serverSocket = ssc.socket();

        InetSocketAddress isa = new InetSocketAddress(config.getInetAddress(), config.getPort());
        serverSocket.bind(isa);

        logger.info("Direct Connection Server for agent " + this.localAgent.getAgentID() + " listening on " +
            serverSocket.toString());

        // set the "real" port value
        config.setPort(serverSocket.getLocalPort());

        // register the listener with the selector
        ssc.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {

        boolean r = this.selectThread.compareAndSet(null, Thread.currentThread());
        if (r == false) {
            logger.error("A select thread has already been started, aborting the current thread ",
                    new Exception());
            return;
        }

        Set<SelectionKey> selectedKeys = null;
        Iterator<SelectionKey> it;
        SelectionKey key;

        while (this.stopped.get() == false) {
            try {
                selector.select();
                selectedKeys = selector.selectedKeys();
                it = selectedKeys.iterator();
                while (it.hasNext()) {
                    key = (SelectionKey) it.next();
                    it.remove();
                    if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                        this.handleAccept(key);
                    } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                        this.handleRead(key);
                    } else {
                        logger.warn("Unhandled SelectionKey operation");
                    }
                }

            } catch (IOException e) {
                logger.warn("Select failed", e);
            }
        }

        this.cleanup();

    }

    private void cleanup() {
        tpe.shutdown();

        try {
            this.ssc.socket().close();
            this.ssc.close();
            this.selector.close();
        } catch (IOException e) {
            ProActiveLogger.logEatedException(logger, e);
        }
        this.isStopped.countDown();
    }

    public void stop() {
        if (this.stopped.get() == true)
            throw new IllegalStateException("Already stopped");

        this.stopped.set(true);

        Thread t = this.selectThread.get();
        if (t != null) {
            t.interrupt();
            this.isStopped.await(CLEANUP_WAITING_TIME, TimeUnit.MILLISECONDS);
        }
    }

    // guarantees to free resources, even when stop() is not called
    @Override
    protected void finalize() throws Throwable {
        if (stopped.get() == false) {
            stop();
        }
        super.finalize();
    }

    public Thread getShutdownHook() {
        return this.shutdownHook;
    }

    private static class ShutdownHook implements Runnable {

        private final DirectConnectionServer dcServer;

        public ShutdownHook(DirectConnectionServer dcServer) {
            this.dcServer = dcServer;
        }

        @Override
        public void run() {
            if (dcServer.stopped.get() == false)
                this.dcServer.stop();
        }

    }

    private void handleAccept(SelectionKey key) {
        SocketChannel sc;
        try {
            sc = ((ServerSocketChannel) key.channel()).accept();
            sc.configureBlocking(false);

            // Add the new connection to the selector
            sc.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            logger.warn("Failed to accept a new connection", e);
        }
    }

    private void handleRead(SelectionKey key) {
        SocketChannel sc;
        ByteBuffer buffer = ByteBuffer.allocate(READ_BUFFER_SIZE);

        sc = (SocketChannel) key.channel();

        MessageAssembler assembler = (MessageAssembler) key.attachment();
        if (assembler == null) {
            assembler = new MessageAssembler(this, sc);
            key.attach(assembler);
        }

        // Read all the data available
        try {
            int byteRead;
            do {
                buffer.clear();
                byteRead = sc.read(buffer);
                buffer.flip();

                if (byteRead > 0) {
                    assembler.pushBuffer(buffer);
                }
            } while (byteRead > 0);

            if (byteRead == -1) {
                clientDisconnected(key);
            }
        } catch (MalformedMessageException e) {
            // Disconnect the client to avoid a disaster
            clientDisconnected(key);
        } catch (IOException e) {
            clientDisconnected(key);
        }
    }

    /** clean everything when a client disconnects */
    private void clientDisconnected(SelectionKey key) {

        key.cancel();
        key.attach(null);
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            sc.socket().close();
        } catch (IOException e) {
            // Miam Miam Miam
            ProActiveLogger.logEatedException(logger, e);
        }

        try {
            sc.close();
        } catch (IOException e) {
            // Miam Miam Miam
            ProActiveLogger.logEatedException(logger, e);
        }

        logger.debug("Remote agent " + sc.socket() + " disconnected");

    }

    /**
     * This method will be called by the {@link MessageAssembler} once a complete message is received.
     * The method delegates the processing of this message to a thread in the pool
     * @param currentMessage - a buffer containing a complete {@link Message}
     * @param attachment - identifies the communication endpoint
     */
    public void dispatchMessage(ByteBuffer currentMessage) {
        IncomingMessageDispatcher tlp = new IncomingMessageDispatcher(currentMessage, localAgent
                .getIncomingHandler());
        tpe.execute(tlp);
    }

    /**
     * This method is used to sent a {@link MessageType#DIRECT_CONNECTION_ADVERTISE}
     * message to the {@link Router} in order to mark that the local agent
     * is ready to receive Direct Connections
     * @param config - the information needed to construct the message
     * @throws MessageRoutingException if the advertisment could not be sent
     */
    public void advertise(DirectConnectionServerConfig config) throws MessageRoutingException {
        Long messageID = this.localAgent.getIDGenerator().getAndIncrement();
        DirectConnectionAdvertiseMessage dcAdMessage = new DirectConnectionAdvertiseMessage(messageID, config
                .getInetAddress(), config.getPort());
        // one-way send; no need to wait for reply
        this.localAgent.sendRoutingMessage(dcAdMessage, true);
    }

}
