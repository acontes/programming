package org.objectweb.proactive.extra.forwarding.tests;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

public class MultiServerShutdownHook implements Runnable {

	ServerSocket serverSocket;
	Vector<MultiServerRunnable> connectionVector;
	
	public MultiServerShutdownHook (ServerSocket serverSocket, Vector<MultiServerRunnable> connectionVector) {
		this.serverSocket = serverSocket;
		this.connectionVector = connectionVector;
	}
	
	public void run() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.out.println("IOException, a failure occured while closing the serverSocket");
		}

		for (MultiServerRunnable connection : connectionVector) {
			connection.setRunning(false);
		}
	}
}
