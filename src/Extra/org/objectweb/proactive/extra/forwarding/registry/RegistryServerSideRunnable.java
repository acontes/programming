package org.objectweb.proactive.extra.forwarding.registry;

import java.io.IOException;
import java.net.ServerSocket;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * The ServerRunnable is a runnable dedicated to launch the server side of a {@link ForwardingRegistry} set as an attribute
 * @author A.Fawaz, J.Martin
 *
 */

public class RegistryServerSideRunnable implements Runnable {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);

    private ForwardingRegistry registry;
    private volatile boolean listening = true;

    public RegistryServerSideRunnable(ForwardingRegistry registry) {
        this.registry = registry;
    }

    /**
     * instantiates a {@link ServerSocket} and listens. Whenever a new connection attempt is accepted, creates a new {@link RegistrationHandler} a new {@link Thread} to handle the connection attempt.
     */
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(registry.getListeningPort());
        } catch (IOException e) {
            //log and quit
            if (logger.isDebugEnabled()) {
                logger.debug("ST failed while opening server socket listening on port: " +
                    registry.getListeningPort());
            }
            System.exit(-1);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("ST created server socket and listening on port " + registry.getListeningPort());
        }
        while (listening) {
            try {
                new Thread(new RegistrationHandler(serverSocket.accept(), registry)).start();
                if (logger.isDebugEnabled()) {
                    logger.debug("ST created new RegistrationHandler");
                }
            } catch (IOException e) {
                //log the fact that a connection wasn't correctly accepted and keep listening
                if (logger.isDebugEnabled()) {
                    logger.debug("ST failed while accepting a connection, exception: " + e);
                }
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            if (logger.isDebugEnabled()) {
                logger.debug("ST, a problem occured while closing the serverSocket, exception: ", e);
            }
        }
    }

    /**
     * sets {@link #listening} to false
     */
    protected void stop() {
        listening = false;
    }
}