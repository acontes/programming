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

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public class Router {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_ROUTER);
    static public final int DEFAULT_SERVER_PORT = 2099;
    static public final int BUFFER_CAPACITY = 2048;

    // TODO: handle the cases where a connection fails: clean the maps 
    private final ConcurrentHashMap<SocketChannel, ChannelHandler> socketChannelToChannelHandlerMap = new ConcurrentHashMap<SocketChannel, ChannelHandler>();
    private final ConcurrentHashMap<AgentID, ChannelHandler> agentIDtoChannelHandlerMap = new ConcurrentHashMap<AgentID, ChannelHandler>();

    private int listeningPort = DEFAULT_SERVER_PORT;
    private Selector selector = null;
    private ServerSocketChannel ssc = null;

    private ServerSocket serverSocket = null;

    public Router(int listeningPort) {
        this.listeningPort = listeningPort;
    }

    public void start() {
        Set<SelectionKey> selectedKeys = null;
        Iterator<SelectionKey> it;
        SelectionKey key;

        init();
        while (true) {
            // select new keys
            try {
                selector.select();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            selectedKeys = selector.selectedKeys();
            it = selectedKeys.iterator();

            while (it.hasNext()) {
                key = (SelectionKey) it.next();

                if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                    // Accept the new connection
                    handleAccept(key);
                    it.remove();
                    // TODO: log the fact that we got a connection from sc
                }

                else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                    handleRead(key);
                    it.remove();
                    // TODO: log the reading operation
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

        } catch (IOException e) {
            // TODO Auto-generated catch block
            // Failed during initialization, notify and exit.
            e.printStackTrace();
        }
    }

    private void handleAccept(SelectionKey key) {
        SocketChannel sc;
        try {
            // Accept the new connection TODO: (Here key.channel() should always be equal to ssc), replace the call by ssc ?
            sc = ((ServerSocketChannel) key.channel()).accept();
            sc.configureBlocking(false);

            // Add the new connection to the selector
            sc.register(selector, SelectionKey.OP_READ);

            // Add the new connection in our Map 
            socketChannelToChannelHandlerMap.put(sc, new ChannelHandler(sc));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void handleRead(SelectionKey key) {
        SocketChannel sc;
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);

        // Read the data
        sc = (SocketChannel) key.channel();

        while (true) {
            buffer.clear();
            int r = 0;
            try {
                r = sc.read(buffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (r <= 0) {
                break;
            }

            ChannelHandler handler = socketChannelToChannelHandlerMap.get(sc);
            if (handler == null) { // should never happen except in case of an error
                // TODO: handle this case, notify to registry, clean registry, send error message to client
            }

            else {
                handler.putBuffer(buffer);
            }
        }
    }

}
