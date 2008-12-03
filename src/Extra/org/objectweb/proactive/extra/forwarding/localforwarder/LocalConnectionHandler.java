package org.objectweb.proactive.extra.forwarding.localforwarder;

import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwarding.common.ForwardedMessage;
import org.objectweb.proactive.extra.forwarding.common.ForwardingSocketWrapper;
import org.objectweb.proactive.extra.forwarding.common.OutHandler;


/**
 * Handle the message dispatching to the various local forwarders.
 */
public class LocalConnectionHandler implements Runnable {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);

    final private LinkedBlockingQueue<ForwardedMessage> messageQueue;
    // TODO Change ID type
    final private HashMap<Object, HashMap<ConnectionID, SocketForwarder>> mappings;
    private volatile boolean willClose;
    private volatile boolean isRunning;
    final private OutHandler outHandler;
    final private IncomingDispatcher incomingDispatcher;

    public LocalConnectionHandler(Socket sock, ForwardingAgent agent) {
        messageQueue = new LinkedBlockingQueue<ForwardedMessage>();
        mappings = new HashMap<Object, HashMap<ConnectionID, SocketForwarder>>();
        ForwardingSocketWrapper sw = new ForwardingSocketWrapper(sock);
        outHandler = new OutHandler(sw, agent);
        new Thread(outHandler).start();
        incomingDispatcher = new IncomingDispatcher(sw, agent, this);
        new Thread(incomingDispatcher).start();
        willClose = false;
        isRunning = true;
    }

    public void run() {

        debug("Start handling messages");

        while (isRunning) {
            ForwardedMessage msg = null;
            try {
                msg = messageQueue.poll(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.warn("Error while trying to get a message to handle", e);
            }
            if (msg != null) {
                SocketForwarder fw = null;
                switch (msg.getType()) {
                    case CONNECTION_REQUEST:
                        newConnectionRequest(msg);
                        break;
                    case CONNECTION_ACCEPTED:
                        // Send notification to ClientSocketForwarder
                        debug("accept message received: " + msg);
                        synchronized (mappings) {
                            HashMap<ConnectionID, SocketForwarder> map = mappings.get(msg.getSenderID());
                            if (map != null) {
                                fw = map.get(new ConnectionID(msg.getSenderPort(), msg.getTargetPort()));
                            }
                        }
                        if (fw != null && (fw instanceof ClientSocketForwarder)) {
                            ClientSocketForwarder client = (ClientSocketForwarder) fw;
                            client.notifyAccept();
                        }
                        break;
                    case CONNECTION_ABORTED:
                        // Send notification to ClientSocketForwarder
                        debug("abort message received: " + msg);
                        synchronized (mappings) {
                            //fw = mappings.get(new ConnectionID(msg.getSenderID(), msg.getSenderPort(), msg.getTargetPort()));
                            HashMap<ConnectionID, SocketForwarder> map = mappings.get(msg.getSenderID());
                            if (map != null) {
                                fw = map.remove(new ConnectionID(msg.getSenderPort(), msg.getTargetPort()));
                            }
                        }
                        if (fw != null) {
                            fw.notifyAbort();
                            logger.info("Connection aborted : " + (String) msg.getData());
                        }
                        break;
                    case DATA:
                        // Send data to SocketForwarder
                        debug("data message received: " + msg);
                        synchronized (mappings) {
                            //fw = mappings.get(new ConnectionID(msg.getSenderID(), msg.getSenderPort(), msg.getTargetPort()));
                            HashMap<ConnectionID, SocketForwarder> map = mappings.get(msg.getSenderID());
                            if (map != null) {
                                fw = map.get(new ConnectionID(msg.getSenderPort(), msg.getTargetPort()));
                            }
                        }
                        if (fw != null)
                            fw.receivedData((byte[]) msg.getData());
                        break;
                }
            } else if (willClose) {
                isRunning = false;
            }
        }
        closeConnection();

    }

    private void debug(String string) {
        if (logger.isDebugEnabled())
            logger.debug(string);
    }

    /**
     * Handle a new connection from a remote client.
     * @param msg the connection request message.
     */
    private void newConnectionRequest(ForwardedMessage msg) {

        debug("New connection request from " + msg.getSenderID() + ":" + msg.getSenderPort() + " on port " +
            msg.getTargetPort());
        SocketForwarder fw = null;
        HashMap<ConnectionID, SocketForwarder> map = null;
        // look if Connection already exist from this.
        synchronized (mappings) {
            //fw = mappings.get(new ConnectionID(msg.getSenderID(), msg.getSenderPort(), msg.getTargetPort()));
            map = mappings.get(msg.getSenderID());
            if (map != null) {
                fw = map.get(new ConnectionID(msg.getSenderPort(), msg.getTargetPort()));
            } else {
                map = new HashMap<ConnectionID, SocketForwarder>();
                mappings.put(msg.getSenderID(), map);
            }
        }
        if (fw != null) {
            // TODO Should not be accepted.
            logger.warn("Duplicated connection request detected for message " + msg);
            return;
        }

        // TRY TO ESTABLISH CONNECTION
        fw = new ServerSocketForwarder(msg.getTargetID(), msg.getTargetPort(), msg.getSenderID(), msg
                .getSenderPort(), outHandler, this);

        // If OK, send CONNECTION_ACCEPT message and register Socket
        synchronized (mappings) {
            mappings.get(msg.getSenderID()).put(new ConnectionID(msg.getSenderPort(), msg.getTargetPort()),
                    fw);
        }
    }

    private void closeConnection() {
        // TODO close all existing connections ?
        debug("Closing connection");
        outHandler.stop(false);
        incomingDispatcher.stop();
    }

    /**
     * Store a new received message to handle.
     * @param msg
     */
    public void receivedMessage(ForwardedMessage msg) {
        if (isRunning && !willClose) {
            try {
                boolean notFull = messageQueue.offer(msg);
                if (!notFull)
                    logger.warn("Queue is full; dropping message");
            } catch (NullPointerException e) {
                logger.debug("Message received should not be null", e);
            }
        }
    }

    /**
     * Put a message to send to the registry.
     * @param msg
     */
    public void messageToSend(ForwardedMessage msg) {
        outHandler.putMessage(msg);
    }

    /**
     * Create a new connection from the client to a remote target.
     * @param msg
     * @return
     */
    public ClientSocketForwarder createNewConnection(ForwardedMessage msg) {
        // msg = CONNECTION_REQUEST [from=localID:#port; to=destination:port]
        SocketForwarder prev = null;
        HashMap<ConnectionID, SocketForwarder> map = null;
        synchronized (mappings) {
            //prev = mappings.get(new ConnectionID(msg.getTargetID(), msg.getTargetPort(), msg.getSenderPort()));
            map = mappings.get(msg.getTargetID());
            if (map != null) {
                prev = map.get(new ConnectionID(msg.getSenderPort(), msg.getTargetPort()));
            } else {
                map = new HashMap<ConnectionID, SocketForwarder>();
                mappings.put(msg.getTargetID(), map);
            }
        }
        if (prev != null) {
            // TODO previous mapping already exist.
            logger.warn("previous mapping already exist for message " + msg);
        }

        ClientSocketForwarder fw = null;
        debug("Creating SocketForwarder with local port number " + msg.getSenderPort());
        fw = new ClientSocketForwarder(msg.getSenderID(), msg.getSenderPort(), msg.getTargetID(), msg
                .getTargetPort(), outHandler, this);

        synchronized (mappings) {
            //mappings.put(new ConnectionID(msg.getTargetID(), msg.getTargetPort(), msg.getSenderPort()), fw);
            mappings.get(msg.getTargetID()).put(new ConnectionID(msg.getTargetPort(), msg.getSenderPort()),
                    fw);
        }
        outHandler.putMessage(msg);
        debug("New connection request sent.");
        return fw;
    }

    public void stop(boolean softly) {
        if (softly) {
            willClose = true;
        } else {
            isRunning = false;
        }
    }

    /**
     * Method called when an agent is disconnected from the forwarding network.
     * We have to close all connections from/to this agent.
     * @param msg {@link ForwardedMessage} containing the agent ID
     */
    public void agentDisconnected(ForwardedMessage msg) {
        // TODO Auto-generated method stub
        HashMap<ConnectionID, SocketForwarder> map = null;
        synchronized (mappings) {
            map = mappings.remove(msg.getSenderID());
        }

        if (map != null) {
            // Close each connection
            for (SocketForwarder fw : map.values()) {
                fw.stop();
            }
        }
    }

    public void unregisterSocketForwarder(SocketForwarder fw) {
        synchronized (mappings) {
            HashMap<ConnectionID, SocketForwarder> map = mappings.get(fw.getTargetID());
            if (map != null) {
                map.remove(fw);
            }
        }
    }
}

/** 
 * Identify a connection in a unique way.
 */
class ConnectionID {
    private int distantPort;
    private int localPort;

    public ConnectionID(int distantPort, int localPort) {
        this.distantPort = distantPort;
        this.localPort = localPort;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ConnectionID) {
            ConnectionID id = (ConnectionID) o;
            return id.distantPort == this.distantPort && id.localPort == this.localPort;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return distantPort + localPort;
    }

}
