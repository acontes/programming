package org.objectweb.proactive.extra.forwarding.tests;

import java.net.*;
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
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        Object uniqueID = null;
        InetAddress regAddress = null;
        int regPort = ForwardingRegistry.DEFAULT_SERVER_PORT;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-regport")) {
                regPort = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-regaddr")) {
                regAddress = InetAddress.getByName(args[++i]);
            } else if (args[i].equals("-localid")) {
                uniqueID = Integer.parseInt(args[++i]);
            } else {
                System.err.println("Unknown option: " + args[i]);
                System.exit(1);
            }
        }

        System.out.println(regAddress + " " + uniqueID);

        if (regAddress == null || uniqueID == null) {
            System.err.println("Registry address and Local host unique ID must be provided, exiting");
            System.exit(1);
        }

        //init the agent (connect to the registry)
        ForwardingAgent agent = ForwardingAgent.getAgent();
        try {
            agent.init(uniqueID, regAddress, regPort);
        } catch (ForwardingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println("init of Forwarding agent failed at some point, exiting");
            System.exit(1);
        }

        try {
            serverSocket = new ServerSocket(DEFAULT_TARGET_SERVER_PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + DEFAULT_TARGET_SERVER_PORT);
            System.exit(1);
        }

        while (listening)
            new Thread(new MultiServerRunnable(serverSocket.accept())).start();

        serverSocket.close();
    }
}
