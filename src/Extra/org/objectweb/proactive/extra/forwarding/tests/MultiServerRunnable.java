package org.objectweb.proactive.extra.forwarding.tests;

import java.net.*;
import java.io.*;

public class MultiServerRunnable implements Runnable {
	private Socket socket = null;

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
		} catch (IOException e1) { //TODO here the socket is not closed before exiting, handle this !!!
			e1.printStackTrace();
			System.err.println("could not initialize input or output stream, exiting");
			System.exit(1);
		}

		try {
			inputLine = (String)in.readObject();
			while(!inputLine.equals("exit")) { // easier handling of try/catch blocks by using this infinite loop
				outputLine = inputLine;
				out.writeObject("echo of \"" + outputLine + "\"");
				inputLine = (String)in.readObject();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.err.println("an exit message might have been missed, closing the connection");
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) { //should not occur
			e1.printStackTrace();
		}

		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
