package org.objectweb.proactive.extra.forwarding.tests;

import java.io.*;
import java.net.*;
import org.objectweb.proactive.extra.forwarding.exceptions.ForwardingException;
import org.objectweb.proactive.extra.forwarding.localforwarder.ForwardingAgent;
import org.objectweb.proactive.extra.forwarding.registry.ForwardingRegistry;


public class Client {

    /**
     * 
     * @param args, usage: [-regport] <-regaddr> <-localid> <-targetid> (at least one target id)
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        Socket clientSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        Object uniqueID = null;
        InetAddress regAddress = null;
        int regPort = ForwardingRegistry.DEFAULT_SERVER_PORT;

        int targetID = -1;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-regport")) {
                regPort = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-regaddr")) {
                regAddress = InetAddress.getByName(args[++i]);
            } else if (args[i].equals("-localid")) {
                uniqueID = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-targetid")) {
                targetID = Integer.parseInt(args[++i]);
            } else {
                System.err.println("Unknown option: " + args[i]);
                System.exit(1);
            }
        }

        if (regAddress == null || uniqueID == null || targetID == -1) {
            System.err
                    .println("Registry address, Local host unique ID, and at least one targetID must be provided, exiting");
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

        //handle the connection
        //prepare the socket and the input and output streams
        try {
            clientSocket = agent.getSocket(targetID, MultiServer.DEFAULT_TARGET_SERVER_PORT);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (ForwardingException e) {
            e.printStackTrace();
            System.err.println("could not connect to target number: " + targetID + ", exiting");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("could not initialize input or output stream for target number: " + targetID +
                ", exiting");
            System.exit(1);
        }

        //prepare standard input
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        //we send and receive Strings for now, but handle them as objects
        String fromServer;
        String fromUser = stdIn.readLine();

        // send one message to each target and loop. To stop sending messages, send "exit" to any of the targets
        while (fromUser != null && !fromUser.equals("exit")) {
            //check that socket is open
            if (!clientSocket.isClosed()) {
                //we only write Strings for now, could use writeChars
                out.writeObject(fromUser);
                System.out.println("Client: " + fromUser);

                //we only read Strings for now
                try {
                    fromServer = (String) in.readObject();
                    System.out.println("Server number " + targetID + " : " + fromServer);
                } catch (ClassNotFoundException e) { //should not occur
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            else
                System.out.println("socket to target number " + targetID + " is closed. Message: " +
                    fromUser + " not sent");

            fromUser = stdIn.readLine();
        }

        //cleanup before exiting
        stdIn.close();
        try {
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
