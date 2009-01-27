package org.objectweb.proactive.extra.forwardingv2.registry.nio;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.exceptions.UnknownAgentIdException;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public class Router {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_ROUTER);
    static public final int DEFAULT_SERVER_PORT = 2099;
    static public final int BUFFER_CAPACITY = 2048;

    /**
     * ThreadPool options:
     * - CORE_POOL_SIZE: If there are less than CORE_POOL_SIZE threads, a new thread is created for each new task (even if an existing thread is available). If there are more than CORE_POOL_SIZE threads, a new thread is created only if the queue of tasks is full.
     * Note that there might be more than CORE_POOL_SIZE threads only if the queue of tasks is bounded.
     * - MAX_POOL_SIZE: Max number of threads if the task queue is bounded. Else, if the queue is unbounded, CORE_POOL_SIZE is the max number of threads since the queue can never be full.
     * - KEEP_ALIVE_TIME: if there are more than CORE_POOL_SIZE threads, excess threads will be terminated if they have been idle for more than the KeepAliveTime (in seconds).
     * Note that in the case of an unbounded queue, since there can't be more than CORE_POOL_SIZE threads, the KEEP_ALIVE_TIME won't be applied. Thus once the number of threads has reached CORE_POOL_SIZE, it remains constant.
     */
    private static final int CORE_POOL_SIZE = 10;

    // useless here because we use an unbounded queue of tasks.
    // private static final int MAX_POOL_SIZE = 20; 
    // private static final long KEEP_ALIVE_TIME = 10;

    private final ExecutorService tpe;

    private final ConcurrentHashMap<SocketChannel, ChannelHandler> socketChannelToChannelHandlerMap = new ConcurrentHashMap<SocketChannel, ChannelHandler>();
    private final ConcurrentHashMap<AgentID, ChannelHandler> agentIDtoChannelHandlerMap = new ConcurrentHashMap<AgentID, ChannelHandler>();

    private int listeningPort = DEFAULT_SERVER_PORT;
    private Selector selector = null;
    private ServerSocketChannel ssc = null;

    private ServerSocket serverSocket = null;

    public Router(int listeningPort) {
        this.listeningPort = listeningPort;

        // a FixedThreadPool operates off a shared UNBOUNDED queue of tasks. Thus the number of threads is indeed fixed once it has reached CORE_POOL_SIZE
        tpe = Executors.newFixedThreadPool(CORE_POOL_SIZE);
    }

    public void start() {
        logger.debug("Starting Router on port " + listeningPort);
        Set<SelectionKey> selectedKeys = null;
        Iterator<SelectionKey> it;
        SelectionKey key;

        // add a shutdownHook
        Runtime.getRuntime().addShutdownHook(new Thread(new RouterShutdownHook(this)));

        init();
        while (true) {
            // select new keys
            try {
                selector.select();
            } catch (IOException e) {
                // TODO: an error occurred while selecting, the server side of the router failed, notify and quit. Reflect if there is a better solution
                logger.warn("Router failed while selecting, quitting", e);
                stop();
            }

            selectedKeys = selector.selectedKeys();
            it = selectedKeys.iterator();

            while (it.hasNext()) {
                key = (SelectionKey) it.next();

                if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                    // Accept the new connection
                    handleAccept(key);
                    it.remove();
                }

                else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                    handleRead(key);
                    it.remove();
                }
            }
        }
    }

    private void init() {
        try {
            // Create a new selector
            selector = Selector.open();

            // Open a listener on the right port
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            serverSocket = ssc.socket();
            serverSocket.bind(new InetSocketAddress(listeningPort));

            // register the listener with the selector
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            if (logger.isDebugEnabled()) {
                logger.debug("Router correctly started on port " + listeningPort);
            }
        } catch (IOException e) {
            // Failed during initialization: notify and stop the router.
            logger.warn("Router failed during initialization, aborting");
            stop();
        }
    }

    private void handleAccept(SelectionKey key) {
        SocketChannel sc = null;
        try {
            // Accept the new connection TODO: (Here key.channel() should always be equal to ssc), replace the call by ssc ?
            sc = ((ServerSocketChannel) key.channel()).accept();
            if (logger.isDebugEnabled()) {
                logger.debug("Router -> New connection from " + sc.socket().getInetAddress());
            }
            sc.configureBlocking(false);

            // Add the new connection to the selector
            sc.register(selector, SelectionKey.OP_READ);

            // Add the new connection in our Map (is not added if an exception occurred just before)
            socketChannelToChannelHandlerMap.put(sc, new ChannelHandler(this, sc));
        } catch (IOException e) {
            // An error occurred while accepting a new connection, simply close the socket Channel
            try {
                if (logger.isDebugEnabled()) {
                    logger
                            .debug("Router, an error occured while accepting a new connection. Closing the socket channel corresponding to this connection");
                }
                if (sc != null) {
                    sc.close();
                }
            } catch (IOException e1) {
                logger.warn("Router, an error occured while closing a failing socket channel", e);
            }
        }
    }

    private void handleRead(SelectionKey key) {
        SocketChannel sc;
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);

        // Read the data
        sc = (SocketChannel) key.channel();
        ChannelHandler handler = socketChannelToChannelHandlerMap.get(sc);
        if (handler == null) { // should never happen
            // TODO: Should really never happen. If this happens, there is huge bug !!! kill the router
            logger.warn("Router, a fatal error happened in the way ChannelHandlers are handled. Quitting");
            stop();
        } else {
            while (true) {
                buffer.clear();
                int r = 0;
                try {
                    r = sc.read(buffer);
                } catch (IOException e) {
                    // an error occurred while reading from the channel. What to do ? Stop this ChannelHandler
                    logger
                            .warn(
                                    "Router an error occured while reading from a channel, cleaning this channelHandler connection",
                                    e);
                    handler.stop(false);
                    break;
                }

                if (r <= 0) {
                    break;
                }

                handler.putBuffer(buffer);
            }
        }
    }

    /**
     * If a mapping for the key given as a parameter exists, replaces the value of this mapping with the value given as a parameter.
     * Else, adds a mapping to {@link #agentIDtoChannelHandlerMap}.
     * @param srcAgentID
     * @param channelHandler
     */
    public void putMapping(AgentID srcAgentID, ChannelHandler channelHandler) {
        agentIDtoChannelHandlerMap.put(srcAgentID, channelHandler);
    }

    /**
     * If a mapping for the key given as a parameter exists, replaces the value of this mapping with the value given as a parameter.
     * Else, adds a mapping to {@link #socketChannelToChannelHandlerMap}.
     * @param socketChannel
     * @param channelHandler
     */
    public void putMapping(SocketChannel socketChannel, ChannelHandler channelHandler) {
        socketChannelToChannelHandlerMap.put(socketChannel, channelHandler);
    }

    /**
     * Removes a mapping from {@link #socketChannelToChannelHandlerMap}
     * @param key
     */
    public void removeMapping(SocketChannel key) {
        socketChannelToChannelHandlerMap.remove(key);
    }

    /**
     * Removes a mapping from {{@link #agentIDtoChannelHandlerMap}}
     * @param key
     */
    public void removeMapping(AgentID key) {
        agentIDtoChannelHandlerMap.remove(key);
    }

    /**
     * @param key The AgentID for which to find the ChannelHandler
     * @return The ChannelHandler associated with the key
     * @throws UnknownAgentIdException If no ChannelHandler was found
     */
    public ChannelHandler getValueFromHashMap(AgentID key) throws UnknownAgentIdException {
        ChannelHandler channelHandler = agentIDtoChannelHandlerMap.get(key);
        if (channelHandler == null) {
            throw new UnknownAgentIdException("Router -> no tunnel registered for AgentID :" + key.getId());
        }
        return channelHandler;
    }

    public void submitTask(Runnable task) {
        tpe.submit(task);
    }

    private void stop() {

        // Stop listening: close the ServerSocket, the ServerSocketChannel
        try {
            serverSocket.close();
            ssc.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Stop writing: shutdown the threadPool executor
        tpe.shutdown();

        // Stop every ChannelHandler
        for (ChannelHandler channelHandler : agentIDtoChannelHandlerMap.values()) {
            channelHandler.stop(true);
        }
        // Stop the possibly existing transition ChannelHandler (which have no agentID mappings)
        for (ChannelHandler channelHandler : socketChannelToChannelHandlerMap.values()) {
            channelHandler.stop(false);
        }

        // Stop the selector
        try {
            selector.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public class RouterShutdownHook implements Runnable {

        Router router;

        /**
         * @param router the router to be stopped
         */
        public RouterShutdownHook(Router router) {
            this.router = router;
        }

        /**
         * stops the router
         */
        public void run() {
            router.stop();
        }
    }
}
