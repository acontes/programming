package org.objectweb.proactive.extra.forwarding.tests;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.objectweb.proactive.extra.forwarding.exceptions.ForwardingException;
import org.objectweb.proactive.extra.forwarding.localforwarder.ForwardingAgent;


/**
 * 
 * Connection Runnable generates a connection to a {@link MultiServer} given its ID.
 * It then attempts to send it a determined number of messages.
 * @author A.Fawaz, J.Martin
 *
 */
public class ConnectionRunnable implements Runnable {

	ForwardingAgent agent;
	int targetID;
	int connectionID;
	int nbMessages;
	boolean running = true;
	
	public ConnectionRunnable(ForwardingAgent agent, int targetID, int connectionID, int nbMessages){
		this.agent = agent;
		this.targetID = targetID;
		this.connectionID = connectionID * 10;
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
			System.out.println("ForwardingException, [connection = " + connectionID + "], could not connect to target number: " + targetID + ", exiting");
			running = false;
		} catch (IOException e) {
			System.out.println("IOException, [connection = " + connectionID + "], could not initialize input or output stream for target number: " + targetID + ", exiting");
			running = false;
		}

		//we send and receive Strings for now, but handle them as objects
		String fromServer;
		StringBuffer fromUser = new StringBuffer("I am connection [" + connectionID + ":" + clientSocket.getLocalPort() + "]");
		int i=0;
		
		// send one message to each target and loop. To stop sending messages, send "exit" to any of the targets
		while (running && i<nbMessages) {
			//check that socket is open
			if (!clientSocket.isClosed()) {
				//we only write Strings for now, could use writeChars
				try {
					out.writeObject(fromUser + ", [MSG NB = " + i + "]");
//					System.out.println("[connection = " + connectionID + ":" + clientSocket.getLocalPort() + "], message sent: [" + fromUser + "]");
					
					//we only read Strings for now
					fromServer = (String)in.readObject();
					System.out.println("[Server = " + targetID + "], "+ fromServer);
					i++;
				} catch (IOException e1) {
					System.out.println("IOException, [connection = " + connectionID + ":" + clientSocket.getLocalPort() + "], IOException while sending or receiving a message");
					running = false;
				} catch (ClassNotFoundException e) { //should not occur
					System.out.println("ClassNotFoundExcetpion, [connection = " + connectionID + ":" + clientSocket.getLocalPort() + "], ClassNotFoundException while receiving message");
					e.printStackTrace();
					running = false;
				}
			}
			else {
				System.out.println("[connection = " + connectionID + ":" + clientSocket.getLocalPort() + "], socket to target number " + targetID + " is closed. Message: "+ fromUser + " not sent");
				running = false;
			}
		}

		System.out.println("[connection = " + connectionID + ":" + clientSocket.getLocalPort() + "], managed to send " + i + " messages to target " + targetID);
		System.out.println("[connection = " + connectionID + ":" + clientSocket.getLocalPort() + "], Cleaning IO streams and Socket before exiting");
		//cleanup before exiting
		try { 
			out.close();
			in.close();
			clientSocket.close();
			System.out.println("connection = " + connectionID + " I/O Streams and clientSocket closed");
		}
		catch (IOException e) {
			System.out.println("IOException, connection = " + connectionID + " failed while closing IO streams and client Socket");
		}
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
