package org.objectweb.proactive.extra.forwardingv2.registry;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;

import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message;


/**
 * The ForwardingRegistry is a module that handles the registration of new clients and the routing of {@link Message} between registered clients
 * 
 * The Forwarding Registry only handles messages of the following kind: {@link Message}.
 * A {@link LinkedBlockingQueue} is used to handle the list of {@link Message}. This message queue is thread safe.
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
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);

    static public final int DEFAULT_SERVER_PORT = 2099;

    static long attributedAgentID = 1;
    private volatile boolean listening = true;
    private int listeningPort;
    private ServerSocket serverSocket = null;
    final private ConcurrentHashMap<AgentID, RegistrationHandler> registrationHandlerMap = new ConcurrentHashMap<AgentID, RegistrationHandler>();

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
                if (logger.isDebugEnabled()) {
                    logger.debug("FR server port is: " + port);
                }
            } else {
                System.out.println("Unknown option: " + args[i]);
                System.exit(1);
            }
        }

        ForwardingRegistry registry = new ForwardingRegistry(port);

        Runtime.getRuntime().addShutdownHook(new Thread(new RegistryShutdownHook(registry)));

        if (logger.isDebugEnabled()) {
            logger.debug("FR.main(), shutdown hook added, going to start() FR");
        }
        registry.start();

        // TODO connect to other registries if implemented
    }

    /**
     * Launches the server of the ForwardingRegistry in a new DAEMON thread.
     * Since this thread is the one that will create the RegistrationHandlers, which will in turn create the needed OutHandlers, every thread (except main) will be DAEMON  
     * Then handles the blocking message queue by calling {@link #forwardMessage(Message)} whenever a {@link Message} is received. 
     */
    private void start() {
        // launch the server side and listen
        try {
            serverSocket = new ServerSocket(listeningPort);
        } catch (IOException e) {
            //log and quit
            if (logger.isDebugEnabled()) {
                logger.debug("FR failed while opening server socket listening on port: " + listeningPort);
            }
            listening = false;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("FR created server socket and listening on port " + listeningPort);
        }

        while (listening) {
            try {
                new Thread(new RegistrationHandler(serverSocket.accept(), this, attributedAgentID++)).start();
                if (logger.isDebugEnabled()) {
                    logger.debug("FR created new RegistrationHandler");
                }
            } catch (IOException e) {
                //log the fact that a connection wasn't correctly accepted and keep listening
                if (logger.isDebugEnabled()) {
                    logger.debug("FR failed while accepting a connection, exception: " + e);
                }
            }
        }

        //FIXME this line of code can't be reached. A shutdown hook was added to stop the registry properly. But this solution should normally only be used defensively. Add a shell to be able to pass commands to the registry such as start, stop...
        if (logger.isDebugEnabled()) {
            logger.debug("FR.start() going to call FR.stop");
        }
        stop();
    }

    /**
     * Handles a {@link Message} in function of its targetId.
     * If the target is registered, forwards the message to the right destination.
     * Else discards the message and sends back an {@link ForwardedMessageType #CONNECTION_ABORTED} to the original sender.
     * 
     * @param msg the {@link Message} to be handled
     */

    /*	private void forwardMessage(Message msg) {
    	RegistrationHandler regHandler = null;
    	synchronized (registrationHandlerMap) {
    		regHandler = registrationHandlerMap.get(msg.getTargetID());
    	}

    	// if the target is registered, forward the message
    	if (regHandler != null) {
    		if (logger.isDebugEnabled()) {
    			logger.debug("FR.forwardMessage(), target: " + msg.getTargetID() +
    			" is registered, msg enqueued in OH");
    		}
    		regHandler.getOutHandler().putMessage(msg);
    	}

    	// if the target is not registered, create and send an ABORT MSG to the sender
    	else {
    		if (logger.isDebugEnabled()) {
    			logger.debug("FR.forwardMessage(), target: " + msg.getTargetID() +
    					" is not registered, sending back abortMessage to sender: " + msg.getSenderID());
    		}
    		Message response = Message.abortMessage(msg.getTargetID(), msg.getTargetPort(),
    				msg.getSenderID(), msg.getSenderPort(), "Target unreachable");
    		synchronized (registrationHandlerMap) {
    			regHandler = registrationHandlerMap.get(msg.getSenderID());
    		}
    		// check if the sender tunnel died and was removed from the regHandlers mappings in the meantime
    		if (regHandler != null) {
    			regHandler.getOutHandler().putMessage(response);
    		} else if (logger.isDebugEnabled()) {
    			logger
    			.debug("FR.forwardMessage(), sender tunnel died in the meantime, not sending abort message");
    		}
    	}
    }
     */
    /**
     * Stops the server of the ForwardingRegistry by calling {@link #stop(boolean)} function so that no more {@link RegistrationHandler} is created.
     * Prevents the existing {@link RegistrationHandler} from listening so that no more {@link Message} is added to the {@link ForwardingRegistry #messageQueue} (call to {@link RegistrationHandler #stopListening()}.
     * Forwards the remaining messages in the Forwarding Registry's messageQueue.
     * Call {@link RegistrationHandler #stop(boolean)} in order to:
     * 		stop the {@link OutHandler} (softly or not).
     * 		close the {@link RegistrationHandler} socket.
     * 		remove the mappings to the {@link RegistrationHandler}.
     * 
     * @param softly Used to determine whether the {@link OutHandler} that are going to be stopped should be stopped softly or not.
     */
    protected void stop() { //FIXME: For this function to be called, listening must be set to false... But this function is the only one able to set it to false. Currently a shutdown hook is used as the solution to call this function and stop the registry properly

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

        // stop the Registration Handlers, close the RegistrationHandlers' sockets, deallocate the RegistrationHandlers
        if (logger.isDebugEnabled()) {
            logger.debug("FR.stop(), calling RH.stop() on every mapped RH");
        }
        for (RegistrationHandler regHandler : registrationHandlerMap.values()) {
            regHandler.stop();
        }
    }

    /**
     * Performs a lookup on {@link #registrationHandlerMap}
     * @param key The key to be looked up
     * @return True if the key is mapped and false otherwise
     */
    public boolean isKeyInMap(Object key) {
        boolean contained = false;
        contained = registrationHandlerMap.containsKey(key);
        return contained;
    }

    /**
     * Adds a mapping to the {@link #registrationHandlerMap}
     * @param senderID
     * @param regHandler
     */
    public void putMapping(AgentID srcAgentID, RegistrationHandler regHandler) {
        registrationHandlerMap.put(srcAgentID, regHandler);
    }

    /**
     * retrieves a registration handler given its agentId key
     * @param key
     * @return
     */
    public RegistrationHandler getValueFromHashMap(AgentID key) {
        return registrationHandlerMap.get(key);
    }

    /**
     * Kills a {@link RegistrationHandler}. For example, if the tunnel failed on client side and if this client is trying to establish a new tunnel while ForwardingRegistry thinks that the first tunnel is still active. 
     * @param key Key to the {@link RegistrationHandler}
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
     * Notifies every other registered agent that tunnel connection, that is going to be removed from the map, 
     * has undergone a disconnection, and thus that any connection linking two nodes and involving this tunnel
     * should be closed. 
     * @param key The key to the mapping that should be removed
     */
    public void removeMapping(AgentID key) {
        registrationHandlerMap.remove(key);
        for (RegistrationHandler regHandler : registrationHandlerMap.values()) {
            regHandler.sendMessage(Message.agentDisconnected(key, regHandler.getAgentID()).toByteArray());
        }
    }
}