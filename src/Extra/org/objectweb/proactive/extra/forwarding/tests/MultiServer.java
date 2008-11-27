package org.objectweb.proactive.extra.forwarding.tests;

import java.net.*;
import java.util.Vector;
import java.io.*;

import org.objectweb.proactive.extra.forwarding.exceptions.ForwardingException;
import org.objectweb.proactive.extra.forwarding.localforwarder.ForwardingAgent;
import org.objectweb.proactive.extra.forwarding.registry.ForwardingRegistry;


/**
 * 
 * MultiServer listens for new connection and echoes messages that are received from clients
 * usage <-regaddr> <-localid>
 * Parameters:
 * 		-regaddr: the address of the {@link ForwardingRegistry}
 * 		-localid: the uniqueID attributed to the MultiServer
 * @author A.Fawaz, J.Martin
 *
 */
public class MultiServer {
    static final int DEFAULT_TARGET_SERVER_PORT = 4444;

    /**
     *  must be killed manually...
     * @param args, usage <-regaddr> <-localid>
     * @throws IOException
     */
    public static void main(String[] args) {

        ServerSocket serverSocket = null;
        boolean listening = true;
        Object uniqueID = null;
        InetAddress regAddress = null;
        int regPort = ForwardingRegistry.DEFAULT_SERVER_PORT;
        Vector<MultiServerRunnable> connectionVector = new Vector<MultiServerRunnable>();
        MultiServerRunnable connection = null;

        Runtime.getRuntime().addShutdownHook(
                new Thread(new MultiServerShutdownHook(serverSocket, connectionVector)));

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-regport")) {
                regPort = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-regaddr")) {
                try {
                    regAddress = InetAddress.getByName(args[++i]);
                } catch (UnknownHostException e) {
                    System.out.println("Unknown Host Exception: the address of the host is unknown, exiting");
                    System.exit(1);
                }
            } else if (args[i].equals("-localid")) {
                uniqueID = Integer.parseInt(args[++i]);
            } else {
                System.out.println("Unknown option: " + args[i]);
                System.exit(1);
            }
        }

        System.out.println("regAddr = " + regAddress + ", uniqueID = " + uniqueID);

        if (regAddress == null || uniqueID == null) {
            System.out.println("Registry address and Local host unique ID must be provided, exiting");
            System.exit(1);
        }

        //init the agent (connect to the registry)
        ForwardingAgent agent = ForwardingAgent.getAgent();
        try {
            agent.init(uniqueID, regAddress, regPort);
        } catch (ForwardingException e) {
            System.out.println("ForwardingException, init of Forwarding agent failed at some point, exiting");
            System.exit(1);
        }

        try {
            serverSocket = new ServerSocket(DEFAULT_TARGET_SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Could not listen on port: " + DEFAULT_TARGET_SERVER_PORT);
            System.exit(1);
        }

        while (listening) {
            try {
                connection = new MultiServerRunnable(serverSocket.accept());
                connectionVector.add(connection);
                new Thread(connection).start();
            } catch (IOException e) {
                System.out.println("IOException, Server failed while accepting a new connection, exiting");
                break;
            }
        }
    }
}
