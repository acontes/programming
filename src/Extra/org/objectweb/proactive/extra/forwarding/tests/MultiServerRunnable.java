package org.objectweb.proactive.extra.forwarding.tests;

import java.net.*;
import java.io.*;


/**
 * 
 * MultiServerRunnable is the Runnable that handles the echo functionality
 * @author A.Fawaz, J.Martin
 *
 */
public class MultiServerRunnable implements Runnable {
	private Socket socket = null;
	boolean running = true;

	public MultiServerRunnable(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		String inputLine = null;
		String outputLine = null;


		try {
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e1) {
			System.out.println("could not initialize input or output stream, exiting");
			running = false;
		}

		try {
			inputLine = (String)in.readObject();
		} catch (IOException e2) {
			System.out.println("IOException, while reading first message, closing this connection on server side");
			running = false;
		} catch (ClassNotFoundException e2) { //should not occur
			System.out.println("ClassNotFoundException");
			e2.printStackTrace();
			running = false;
		}


		while(running && inputLine != null) {
			outputLine = inputLine;
			try{
				out.writeObject("echo of \"" + outputLine + "\"");
				inputLine = (String)in.readObject();
			} catch (IOException e1) {
				System.out.println("IOException, while reading or writing in the socket, closing this connection on server side");
				running = false;
			} catch (ClassNotFoundException e1) { //should not occur
				System.out.println("ClassNotFoundException");
				e1.printStackTrace();
				running = false;
			}
		}

		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("IOException while closing IO streams and socket");
		}
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
}
