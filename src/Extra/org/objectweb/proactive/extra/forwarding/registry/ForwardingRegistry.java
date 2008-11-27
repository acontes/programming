package org.objectweb.proactive.extra.forwarding.registry;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.objectweb.proactive.extra.forwarding.common.ForwardedMessage;
import org.objectweb.proactive.extra.forwarding.common.OutHandler;
import org.objectweb.proactive.extra.forwarding.common.ForwardedMessage.ForwardedMessageType;
import org.objectweb.proactive.extra.forwarding.tests.TestLogger;


/**
 * The ForwardingRegistry is a module that handles the registration of new clients and the routing of {@link ForwardedMessage} between registered clients
 * 
 * The Forwarding Registry only handles messages of the following kind: {@link ForwardedMessage}.
 * A {@link LinkedBlockingQueue} is used to handle the list of {@link ForwardedMessage}. This message queue is thread safe.
 * The {@link #messageQueue} is filled by {@link RegistrationHandler}s (server side of a tunnel) 
 * Whenever the {@link #messageQueue} is not empty, messages are popped out and pushed into the queue of the right {@link OutHandler} (client side of the tunnel to a client) 
 * 
 * For each client, its registration and connection to the registry is equivalent to the existence of a {@link RegistrationHandler}.
 * A HashMap keeps track of the mapping between a {@link RegistrationHandler} and a client unique id. Several wrapping functions are provided to performs synchronized calls on this HashMap. 
 * 
 * @author A.Fawaz, J.Martin
 *
 */
public class ForwardingRegistry {
    //	static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);
    public static final Logger logger = TestLogger.getLogger();

    static public final int DEFAULT_SERVER_PORT = 2099;

    private boolean running = false;
    private int listeningPort;

    final RegistryServerSideRunnable server = new RegistryServerSideRunnable(this);
    final private HashMap<Object, RegistrationHandler> registrationHandlerMap = new HashMap<Object, RegistrationHandler>();
    final private LinkedBlockingQueue<ForwardedMessage> messageQueue = new LinkedBlockingQueue<ForwardedMessage>();

    public ForwardingRegistry(int listeningPort) {
        this.listeningPort = listeningPort;
    }

    /**
     * Generates a new ForwardingRegistry and calls {@link #start()} function
     * @param args: <-regport>
     */
    public static void main(String[] args) {
        int port = DEFAULT_SERVER_PORT;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-port")) {
                port = Integer.parseInt(args[++i]);
                if (logger.isDebugEnabled())
                    logger.debug("FR server port is: " + port);
            } else {
                System.err.println("Unknown option: " + args[i]);
                System.exit(1);
            }
        }

        ForwardingRegistry registry = new ForwardingRegistry(port);

        Runtime.getRuntime().addShutdownHook(new Thread(new RegistryShutdownHook(registry)));

        if (logger.isDebugEnabled())
            logger.debug("FR.main(), shutdown hook added, going to start() FR");
        registry.start();

        // TODO connect to other registries if implemented
    }

    /**
     * Launches the server of the ForwardingRegistry in a new thread.
     * Then handles the blocking message queue by calling {@link #forwardMessage(ForwardedMessage)} whenever a {@link ForwardedMessage} is received. 
     */
    private void start() {
        // launch the server thread
        running = true;
        new Thread(server).start();
        if (logger.isDebugEnabled())
            logger
                    .debug("FR.start(), created and started a new thread for the registry server\n now processing messageQueue");

        // handle messageQueue
        while (running) {
            ForwardedMessage msg = null;
            try {
                msg = messageQueue.poll(1, TimeUnit.SECONDS);
                if (logger.isDebugEnabled() && msg != null)
                    logger.debug("FR.start() dequeued msg: " + msg);
            } catch (InterruptedException e) {
                // The waiting has been interrupted.
                if (logger.isDebugEnabled())
                    logger
                            .debug("FR.start(), The waiting of the blocking queue was interrupted, exception: " +
                                e);
            }
            if (msg != null) {
                if (logger.isDebugEnabled())
                    logger.debug("FR.start(), forwarding the following message: \n" + msg.toString());
                forwardMessage(msg);
            }
        }

        //FIXME this line of code can't be reached. Add a shutdown hook or similar solution
        if (logger.isDebugEnabled())
            logger.debug("FR.start() going to call FR.stop");
        stop(true);
    }

    /**
     * Handles a {@link ForwardedMessage} in function of its targetId.
     * If the target is registered, forwards the message to the right destination.
     * Else discards the message and sends back an {@link ForwardedMessageType #CONNECTION_ABORTED} to the original sender.
     * 
     * @param msg the {@link ForwardedMessage} to be handled
     */
    private void forwardMessage(ForwardedMessage msg) {
        RegistrationHandler regHandler = null;
        synchronized (registrationHandlerMap) {
            regHandler = registrationHandlerMap.get(msg.getTargetID());
        }

        // if the target is registered, forward the message
        if (regHandler != null) {
            if (logger.isDebugEnabled())
                logger.debug("FR.forwardMessage(), target: " + msg.getTargetID() +
                    " is registered, msg enqueued in OH");
            regHandler.getOutHandler().putMessage(msg);
        }

        // if the target is not registered, create and send an ABORT MSG to the sender
        else {
            if (logger.isDebugEnabled())
                logger.debug("FR.forwardMessage(), target: " + msg.getTargetID() +
                    " is not registered, sending back abortMessage to sender: " + msg.getSenderID());
            ForwardedMessage response = ForwardedMessage.abortMessage(msg.getTargetID(), msg.getTargetPort(),
                    msg.getSenderID(), msg.getSenderPort(), "Target unreachable");
            synchronized (registrationHandlerMap) {
                regHandler = registrationHandlerMap.get(msg.getSenderID());
            }
            // check if the sender tunnel died and was removed from the regHandlers mappings in the meantime
            if (regHandler != null)
                regHandler.getOutHandler().putMessage(response);
            else {
                if (logger.isDebugEnabled())
                    logger
                            .debug("FR.forwardMessage(), sender tunnel died in the meantime, not sending abort message");
            }
        }
    }

    /**
     * Stops the server of the ForwardingRegistry by calling {@link #stop(boolean)} function so that no more {@link RegistrationHandler} is created.
     * Prevents the existing {@link RegistrationHandler} from listening so that no more {@link ForwardedMessage} is added to the {@link ForwardingRegistry #messageQueue} (call to {@link RegistrationHandler #stopListening()}.
     * Forwards the remaining messages in the Forwarding Registry's messageQueue.
     * Call {@link RegistrationHandler #stop(boolean)} in order to:
     * 		stop the {@link OutHandler} (softly or not).
     * 		close the {@link RegistrationHandler} socket.
     * 		remove the mappings to the {@link RegistrationHandler}.
     * 
     * @param softly Used to determine whether the {@link OutHandler} that are going to be stopped should be stopped softly or not.
     */
    protected void stop(boolean softly) { //FIXME: For this function to be called, running must be set to false... But this function is the only one able to set it to false
        running = false;
        ForwardedMessage msg = null;

        // stop the server
        if (logger.isDebugEnabled())
            logger.debug("FR.stop(), calling ServTh.stop()");
        server.stop();

        // stop the Registration Handlers
        if (logger.isDebugEnabled())
            logger.debug("FR.stop(), calling RH.stopListening() on every mapped RH");
        for (RegistrationHandler regHandler : registrationHandlerMap.values()) {
            regHandler.stopListening();
        }

        // forward the remaining messages in the Forwarding Registry's messageQueue
        if (logger.isDebugEnabled())
            logger.debug("FR.stop(), forwarding remaining messages");
        msg = messageQueue.poll();
        while (msg != null) {
            if (logger.isDebugEnabled())
                logger.debug("FR.stop(), forwarding the following message: \n" + msg);
            forwardMessage(msg);
            msg = messageQueue.poll();
        }

        // stop the OutHandlers, close the RegistrationHandlers' sockets, deallocate the RegistrationHandlers
        if (logger.isDebugEnabled())
            logger.debug("FR.stop(), calling RH.stop() on every mapped RH");
        for (RegistrationHandler regHandler : registrationHandlerMap.values()) {
            regHandler.stop(softly);
        }
    }

    /**
     * Performs a synchronized lookup on {@link #registrationHandlerMap}
     * @param key The key to be looked up
     * @return True if the key is mapped and false otherwise
     */
    public boolean isKeyInMap(Object key) {
        boolean contained = false;
        synchronized (registrationHandlerMap) {
            contained = registrationHandlerMap.containsKey(key);
        }
        return contained;
    }

    /**
     * Adds a mapping to the {@link #registrationHandlerMap} in a synchronized way
     * @param senderID
     * @param regHandler
     */
    public void putMapping(Object senderID, RegistrationHandler regHandler) {
        synchronized (registrationHandlerMap) {
            registrationHandlerMap.put(senderID, regHandler);
        }
    }

    /**
     * Kills a {@link RegistrationHandler}. For example, if the tunnel failed on client side and if this client is trying to establish a new tunnel while ForwardingRegistry thinks that the first tunnel is still active. 
     * @param key Key to the {@link RegistrationHandler}
     * @param softly Whether or not to stop the {@link OutHandler} softly
     */
    public void killRegistrationHandler(Object key, boolean softly) {
        RegistrationHandler regHandler = null;
        synchronized (registrationHandlerMap) {
            regHandler = registrationHandlerMap.get(key);
        }
        if (logger.isDebugEnabled())
            logger.debug("FR.killRegistrationHandler(), calling RH.stop() on RH.uniqueId: " +
                regHandler.getHostId());
        regHandler.stop(softly);
    }

    /**
     * Performs a synchronized removal on {@link #registrationHandlerMap}
     * @param key The key to the mapping that should be removed
     */
    public void removeMapping(Object key) {
        synchronized (registrationHandlerMap) {
            registrationHandlerMap.remove(key);
            ForwardedMessage msg = ForwardedMessage.agentDisconnectedMessage(key);
            for (RegistrationHandler regHandler : registrationHandlerMap.values()) {
                regHandler.getOutHandler().putMessage(msg);
            }
        }
    }

    /**
     * Adds a message to {@link #messageQueue}. This operation is thread safe. see {@link BlockingQueue}
     * @param msg The message to be added to the {@link #messageQueue}
     */
    public void putMessage(ForwardedMessage msg) {
        try {
            boolean notFull = messageQueue.offer(msg);
            if (!notFull)
                logger.warn("Queue is full; dropping message");
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int getListeningPort() {
        return listeningPort;
    }
}