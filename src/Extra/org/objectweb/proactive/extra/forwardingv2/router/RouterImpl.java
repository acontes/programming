package org.objectweb.proactive.extra.forwardingv2.router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ErrorMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ErrorMessage.ErrorType;


public class RouterImpl implements Runnable, Router {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_ROUTER);

    /** Read {@link ByteBuffer} size. */
    private final static int READ_BUFFER_SIZE = 4096;

    /** Thread pool used to execute all asynchronous tasks */
    private final ExecutorService tpe;

    /** All the clients known by {@link AgentID}*/
    private final ConcurrentHashMap<AgentID, Client> clientMap = new ConcurrentHashMap<AgentID, Client>();

    /** The local TCP port on which the router is listening */
    private int port;

    private Selector selector = null;
    private ServerSocketChannel ssc = null;
    private ServerSocket serverSocket = null;

    /** Create a new router
     * 
     * When a new router is created it binds onto the given port.
     * 
     * To start handling connections, a thread MUST be spawned by the caller.
     * <code>
     *  RouterImpl this.router = new RouterImpl(0);
     *  Thread t = new Thread(router);
     *  t.setDaemon(true);
     *  t.setName("Router");
     *  t.start();
     * </code>
     * 
     * @param port port number to bind to
     * @throws IOException if the router failed to bind
     */
    public RouterImpl(int port) throws IOException {
        init(port);
        tpe = Executors.newFixedThreadPool(10);
    }

    private void init(int port) throws IOException {
        // Create a new selector
        selector = Selector.open();

        // Open a listener on the right port
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        serverSocket = ssc.socket();
        serverSocket.bind(new InetSocketAddress(port));
        this.port = serverSocket.getLocalPort();
        logger.info("Message router listening on " + serverSocket.toString());

        // register the listener with the selector
        ssc.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void run() {
        Set<SelectionKey> selectedKeys = null;
        Iterator<SelectionKey> it;
        SelectionKey key;

        while (true) {
            // select new keys
            try {
                selector.select();
                selectedKeys = selector.selectedKeys();
                it = selectedKeys.iterator();
                while (it.hasNext()) {
                    key = (SelectionKey) it.next();
                    it.remove();
                    if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                        handleAccept(key);
                    } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                        handleRead(key);
                    } else {
                        logger.warn("Unhandled SelectionKey operation");
                    }
                }

            } catch (IOException e) {
                logger.warn("Select failed", e);
            }
        }
    }

    /** Accept a new connection */
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

    /** Read available data for this key */
    private void handleRead(SelectionKey key) {
        SocketChannel sc;
        ByteBuffer buffer = ByteBuffer.allocate(READ_BUFFER_SIZE);

        sc = (SocketChannel) key.channel();

        Attachment attachment = (Attachment) key.attachment();
        if (attachment == null) {
            attachment = new Attachment(this, sc);
            key.attach(attachment);
        }

        // Read all the data available
        try {
            int byteRead;
            do {
                buffer.clear();
                byteRead = sc.read(buffer);
                buffer.flip();

                if (byteRead > 0) {
                    MessageAssembler assembler = attachment.getAssembler();
                    assembler.pushBuffer(buffer);
                }
            } while (byteRead > 0);

            if (byteRead == -1) {
                clientDisconnected(key);
            }
        } catch (IOException e) {
            clientDisconnected(key);
        } catch (IllegalStateException e) {
            // Disconnect the client to avoid a disaster
            clientDisconnected(key);
        }
    }

    /** clean everything when a client disconnect */
    private void clientDisconnected(SelectionKey key) {
        Attachment attachment = (Attachment) key.attachment();

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
        attachment.getClient().discardAttachment();
        logger.debug("Client " + sc.socket() + " disconnected");

        // Broadcast the disconnection to every client
        Collection<Client> clients = clientMap.values();
        AgentID disconnectedAgent = attachment.getClient().getAgentId();
        tpe.submit(new DisconnectionBroadcaster(clients, disconnectedAgent));
    }

    public void handleAsynchronously(ByteBuffer message, Attachment attachment) {
        TopLevelProcessor tlp = new TopLevelProcessor(message, attachment, this);
        tpe.execute(tlp);
    }

    public Client getClient(AgentID agentId) {
        synchronized (clientMap) {
            return clientMap.get(agentId);
        }
    }

    public void addClient(Client client) {
        synchronized (clientMap) {
            clientMap.put(client.getAgentId(), client);
        }
    }

    public int getLocalPort() {
        return this.port;
    }

    private static class DisconnectionBroadcaster implements Runnable {
        final private List<Client> clients;
        final private AgentID disconnectedAgent;

        public DisconnectionBroadcaster(Collection<Client> clients, AgentID disconnectedAgent) {
            this.clients = new ArrayList<Client>(clients);
            this.disconnectedAgent = disconnectedAgent;
        }

        public void run() {
            for (Client client : this.clients) {
                ErrorMessage error = new ErrorMessage(this.disconnectedAgent, 0,
                    ErrorType.ERR_DISCONNECTED_RCPT_BROADCAST);
                try {
                    client.sendMessage(error.toByteArray());
                } catch (Exception e) {
                    ProActiveLogger.logEatedException(logger, e);
                }
            }
        }
    }
}
