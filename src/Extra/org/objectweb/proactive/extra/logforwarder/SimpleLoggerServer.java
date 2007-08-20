/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extra.logforwarder;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

public class SimpleLoggerServer implements Runnable {

	/** The default port number of remote logging server (4560) */
	static final int DEFAULT_PORT = 4560;

	// socket port
	private int port = DEFAULT_PORT;
	private boolean terminate = false;

	// to close sockets
	private Vector<ConnectionHandler> connections;

	public SimpleLoggerServer() {}

	/**
	 * Create a log server.
	 * 
	 * @param port
	 *            the port on which incoming log connection are performed
	 */
	public SimpleLoggerServer(int port) {
		this.port = port;
		this.connections = new Vector<ConnectionHandler>();
	}

	@Override
	public void run() {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (!terminate) {
			try {
				Socket s = serverSocket.accept();
				ConnectionHandler ch = new ConnectionHandler(s);
				this.connections.add(ch);
				new Thread(ch).start();
				// new Thread(new
				// SocketNode(s,LogManager.getLoggerRepository()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// close connection
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void stop() {
		for (ConnectionHandler c : this.connections) {
			c.stop();
		}
		this.terminate = true;
	}

	/**
	 * Thread for handling incoming blocking connection.
	 * 
	 * @author cdelbe
	 * @since 2.2
	 */
	private class ConnectionHandler implements Runnable {

		private Socket input;
		private ObjectInputStream inputStream;
		private boolean terminate;

		public ConnectionHandler(Socket input) {
			try {
				this.input = input;
				this.inputStream = new ObjectInputStream(new BufferedInputStream(input.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			LoggingEvent currentEvent;
			Logger localLogger;

			try {
				while (!terminate) {
					// read an event from the wire
					currentEvent = (LoggingEvent) inputStream.readObject();
					// get the local logger. The name of the logger is taken to
					// be the name contained in the event.
					localLogger = Logger.getLogger(currentEvent.getLoggerName());
					// apply the logger-level filter
					if (currentEvent.getLevel().isGreaterOrEqual(localLogger.getEffectiveLevel())) {
						// finally log the event as if was generated locally
						localLogger.callAppenders(currentEvent);
					}
				}
			} catch (EOFException e) {
				// normal case ...
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			// close stream
			try {
				System.out.println(this + " is terminating...");
				this.inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public synchronized void stop() {
			this.terminate = true;
		}

	}

}
