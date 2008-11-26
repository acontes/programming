package org.objectweb.proactive.extra.forwarding.tests;

import java.io.*;
import java.net.*;
import java.util.Vector;

import org.objectweb.proactive.extra.forwarding.exceptions.ForwardingException;
import org.objectweb.proactive.extra.forwarding.localforwarder.ForwardingAgent;
import org.objectweb.proactive.extra.forwarding.registry.ForwardingRegistry;

/**
 * 
 * ClientMultiTarget attempts to create several connections on a single server and send a determined number of messages.
 * It launches "connNb" {@link ConnectionRunnable} instances in new threads.
 * 
 * usage: <-regaddr> <-localid> <-targetid> [-regport] [-connNb] [-nbmessages]
 * parameters:
 * 		-regaddr: the address of the {@link ForwardingRegistry}
 * 		-localid: the id of this client, should be unique
 * 		-targetid: the id of the {@link MultiServer} to contact
 * 		-regport (optional): the port on which the {@link ForwardingRegistry} is listening
 * 		-connNb (optional): the number of connections to be established on the {@link MultiServer}
 * 		-nbmessages (optional): the number of messages to be sent to this server
 *  
 * @author A.fawaz, J.Martin
 *
 */

public class ClientMultiTarget {

	public static final int DEFAULT_CLIENT_NUMBER = 2;
	public static final int DEFAULT_NUMBER_OF_MESSAGES = 2;
	/**
	 * 
	 * @param args, usage: <-regaddr> <-localid> <-targetid> [-regport] [-connNb] [-nbmessages]
	 * @throws IOException
	 */
	public static void main(String[] args) {

		Object uniqueID = null;
		InetAddress regAddress = null;
		int regPort = ForwardingRegistry.DEFAULT_SERVER_PORT;
		int connNb = DEFAULT_CLIENT_NUMBER;
		int nbMessages = DEFAULT_NUMBER_OF_MESSAGES;
		int targetID = -1;
		ForwardingAgent agent = null;
		Vector<ConnectionRunnable> connectionVector = new Vector<ConnectionRunnable>();
		ConnectionRunnable connection = null;

		Runtime.getRuntime().addShutdownHook(new Thread(new ClientMultiTargetShutdownHook(agent, connectionVector)));
		
		
		for (int i=0;i<args.length;i++) {
			if (args[i].equals("-regport")) {
				regPort = Integer.parseInt(args[++i]);                
			}
			else if(args[i].equals("-regaddr")) {
				try {
					regAddress = InetAddress.getByName(args[++i]);
				} catch (UnknownHostException e) {
					System.out.println("Unknown Host Exception: the address of the host is unknown, exiting");
					System.exit(1);
				}
			}
			else if (args[i].equals("-localid")) {
				uniqueID = Integer.parseInt(args[++i]);                
			}
			else if (args[i].equals("-targetid")) {
				targetID = Integer.parseInt(args[++i]);                
			}
			else if(args[i].equals("-connNb")) {
				connNb = Integer.parseInt(args[++i]);
			}
			else if(args[i].equals("-nbmessages")) {
				nbMessages = Integer.parseInt(args[++i]);
			}
			else {
				System.out.println("Unknown option: " + args[i]);
				System.exit(1);
			}          
		}

		if (regAddress == null || uniqueID == null || targetID == -1) {
			System.out.println("Registry address, Local host unique ID, and at least one targetID must be provided, exiting");
			System.exit(1);
		}
		else
		{
			System.out.println("client launched, [local id = " + uniqueID + "], [targetid = " + targetID + "], [connNb = " + connNb + "], [nbMessages = " + nbMessages + "]");
		}

		//init the agent (connect to the registry)
		agent = ForwardingAgent.getAgent();
		try {
			agent.init(uniqueID, regAddress, regPort);
		} catch (ForwardingException e) {
			System.out.println("init of Forwarding agent failed at some point, exiting");
			System.exit(1);
		}

		//handle the connections

		for (int clientIndex = 1; clientIndex <= connNb; clientIndex++) {
			connection = new ConnectionRunnable(agent, targetID, clientIndex, nbMessages);
			connectionVector.add(connection);
			new Thread(connection).start();
		}
	}
}

