package org.objectweb.proactive.extra.forwarding.tests;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.objectweb.proactive.extra.forwarding.exceptions.ForwardingException;
import org.objectweb.proactive.extra.forwarding.localforwarder.ForwardingAgent;

public class ConnectionRunnable implements Runnable {

	ForwardingAgent agent;
	int targetID;
	int connectionID;
	int nbMessages;
	
	boolean running = true;
	
	public ConnectionRunnable(ForwardingAgent agent, int targetID, int connectionID, int nbMessages){
		this.agent = agent;
		this.targetID = targetID;
		this.connectionID = connectionID;
		this.nbMessages = nbMessages;
	}

	public void run() {

		Socket clientSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try {
			clientSocket = agent.getSocket(targetID, MultiServer.DEFAULT_TARGET_SERVER_PORT);
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
		} catch (ForwardingException e) {
			e.printStackTrace();
			System.out.println("could not connect to target number: " + targetID + ", exiting");
			running = false;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("could not initialize input or output stream for target number: " + targetID + ", exiting");
			running = false;
		}

		//we send and receive Strings for now, but handle them as objects
		String fromServer;
		String fromUser = "I am connection nb: " + connectionID;
		int i=0;
		
		// send one message to each target and loop. To stop sending messages, send "exit" to any of the targets
		while (running && i<nbMessages) {
			//check that socket is open
			if (!clientSocket.isClosed()) {
				//we only write Strings for now, could use writeChars
				try {
					out.writeObject(fromUser);
					System.out.println("Client: " + fromUser);
					
					//we only read Strings for now
					fromServer = (String)in.readObject();
					System.out.println("Server number " + targetID + " : "+ fromServer);
					i++;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("[connectionID = " + "], IOException while sending or receiving a message");
					e1.printStackTrace();
					running = false;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.println("[connectionID = " + "], ClassNotFoundException while receiving message");
					e.printStackTrace();
					running = false;
				}
			}
			else {
				System.out.println("[ConnectionID = " + connectionID + "], socket to target number " + targetID + " is closed. Message: "+ fromUser + " not sent");
				running = false;
			}
		}

		System.out.println("[connectionID = " + "], managed to send " + i + " messages to target " + targetID);
		//cleanup before exiting
		try { 
			out.close();
			in.close();
			clientSocket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
