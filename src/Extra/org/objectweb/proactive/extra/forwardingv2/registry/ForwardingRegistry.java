package org.objectweb.proactive.extra.forwardingv2.registry;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.exceptions.UnknownAgentIdException;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;


/**
 * The ForwardingRegistry is a module that handles the registration of new
 * clients and the routing of {@link Message} between registered clients
 * 
 * The Forwarding Registry only handles messages of the following kind:
 * {@link Message}.
 * 
 */
public class ForwardingRegistry implements Runnable {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_ROUTER);

    static public final int DEFAULT_SERVER_PORT = 2099;

    private volatile boolean listening = true;
    private ServerSocket serverSocket = null;
    final private ConcurrentHashMap<AgentID, RegistrationHandler> registrationHandlerMap = new ConcurrentHashMap<AgentID, RegistrationHandler>();
    private CountDownLatch routerIsReady;

    public ForwardingRegistry(int listeningPort, boolean forked) throws IOException {
        this.routerIsReady = new CountDownLatch(1);
        //        Runtime.getRuntime().addShutdownHook(new Thread(new RegistryShutdownHook(this)));

        this.serverSocket = new ServerSocket(listeningPort);

        if (forked) {
            Thread t = new Thread(this);
            t.setDaemon(true);
            t.setName("Message Router thread");

            t.start();
        } else {
            this.run();
        }

        do {
            try {
                routerIsReady.await();
            } catch (InterruptedException e) {
                // Miam Miam Miam
            }
        } while (routerIsReady.getCount() != 0);
    }

    /**
     * Generates a new ForwardingRegistry and calls {@link #start()} function
     * 
     * @param args
     *            : <-regport>
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        int port = DEFAULT_SERVER_PORT;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-port")) {
                port = Integer.parseInt(args[++i]);
                if (logger.isDebugEnabled()) {
                    logger.debug("FR server port is: " + port);
                }
            } else {
                System.out.println("Unknown option: " + args[i]);
                System.exit(1);
            }
        }

        ForwardingRegistry router = new ForwardingRegistry(port, false);
    }

    /**
     * Launches the server.
     */
    public void run() {
        this.routerIsReady.countDown();
        while (listening) {
            try {
                Thread t = new Thread(new RegistrationHandler(serverSocket.accept(), this));
                t.setDaemon(true);
                t.start();
                if (logger.isDebugEnabled()) {
                    logger.debug("FR created new RegistrationHandler");
                }
            } catch (IOException e) {
                // log the fact that a connection wasn't correctly accepted and
                // keep listening
                if (logger.isDebugEnabled()) {
                    logger.debug("FR failed while accepting a connection, exception: ", e);
                }
            }
        }

        // FIXME this line of code can't be reached. A shutdown hook was added
        // to stop the registry properly. But this solution should normally only
        // be used defensively. Add a shell to be able to pass commands to the
        // registry such as start, stop...
        if (logger.isDebugEnabled()) {
            logger.debug("FR.start() going to call FR.stop");
        }
        stop();
    }

    /**
     * Makes the ForwardingRegistry stop listening so that no more
     * {@link RegistrationHandler} is created. Call
     * {@link RegistrationHandler #stop} on each existing registrationHandler
     */
    protected void stop() { // FIXME: For this function to be called, listening
        // must be set to false... But this function is the
        // only one able to set it to false. Currently a
        // shutdown hook is used as the solution to call
        // this function and stop the registry properly

        // stop the server if it was not stopped already
        listening = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("FR, a problem occured while closing the serverSocket, exception: ", e);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("FR.stop(), stopped server");
        }

        // stop the Registration Handlers, close the RegistrationHandlers'
        // sockets, deallocate the RegistrationHandlers
        if (logger.isDebugEnabled()) {
            logger.debug("FR.stop(), calling RH.stop() on every mapped RH");
        }
        for (RegistrationHandler regHandler : registrationHandlerMap.values()) {
            regHandler.stop();
        }
    }

    /**
     * Performs a lookup on {@link #registrationHandlerMap}
     * 
     * @param key
     *            The key to be looked up
     * @return True if the key is mapped and false otherwise
     */
    public boolean isKeyInMap(Object key) {
        boolean contained = false;
        contained = registrationHandlerMap.containsKey(key);
        return contained;
    }

    /**
     * Adds a mapping to the {@link #registrationHandlerMap}
     * 
     * @param senderID
     * @param regHandler
     */
    public void putMapping(AgentID srcAgentID, RegistrationHandler regHandler) {
        registrationHandlerMap.put(srcAgentID, regHandler);
    }

    /**
     * retrieves a registration handler given its agentId key
     * 
     * @param key
     * @return
     */
    public RegistrationHandler getValueFromHashMap(AgentID key) throws UnknownAgentIdException {
        RegistrationHandler regHandler = registrationHandlerMap.get(key);
        if (regHandler == null) {
            throw new UnknownAgentIdException("no tunnel registered for AgentID :" + key.getId());
        }
        return regHandler;
    }

    /**
     * Kills a {@link RegistrationHandler}. For example, if the tunnel failed on
     * client side and if this client is trying to establish a new tunnel while
     * ForwardingRegistry thinks that the first tunnel is still active.
     * 
     * @param key
     *            Key to the {@link RegistrationHandler}
     */
    public void killRegistrationHandler(AgentID key) {
        RegistrationHandler regHandler = null;
        regHandler = registrationHandlerMap.get(key);

        if (logger.isDebugEnabled()) {
            logger.debug("FR.killRegistrationHandler(), calling RH.stop() on RH.uniqueId: " +
                regHandler.getAgentID());
        }
        regHandler.stop();
    }

    /**
     * Performs a removal on {@link #registrationHandlerMap}
     * 
     * @param key
     *            The key to the mapping that should be removed
     */
    public void removeMapping(AgentID key) {
        registrationHandlerMap.remove(key);
    }

    public int getLocalPort() {
        return serverSocket.getLocalPort();
    }

    public InetAddress getInetAddress() {
        return serverSocket.getInetAddress();
    }
}