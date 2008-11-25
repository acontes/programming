package org.objectweb.proactive.extra.forwarding.localforwarder;

import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.objectweb.proactive.extra.forwarding.common.ForwardedMessage;
import org.objectweb.proactive.extra.forwarding.common.ForwardingSocketWrapper;
import org.objectweb.proactive.extra.forwarding.common.OutHandler;
import org.objectweb.proactive.extra.forwarding.tests.TestLogger;

/**
 * Handle the message dispatching to the various local forwarders.
 */
public class LocalConnectionHandler implements Runnable{
	//protected Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);
	public static final Logger logger = TestLogger.getLogger();

	private LinkedBlockingQueue<ForwardedMessage> messageQueue;
	private HashMap<ConnectionID, SocketForwarder> mappings;
	private boolean willClose;
	private boolean isRunning;
	private OutHandler outHandler;
	private IncomingDispatcher incomingDispatcher;

	public LocalConnectionHandler(Socket sock, ForwardingAgent agent) {
		messageQueue = new LinkedBlockingQueue<ForwardedMessage>();
		mappings = new HashMap<ConnectionID, SocketForwarder>();
		ForwardingSocketWrapper sw = new ForwardingSocketWrapper(sock);
		outHandler = new OutHandler(sw, agent);
		new Thread(outHandler).start();
		incomingDispatcher = new IncomingDispatcher(sw, agent, this);
		new Thread(incomingDispatcher).start();
		willClose = false;
		isRunning = true;
	}

	public void run() {

		debug("Start handling messages");

		while(isRunning) {
			ForwardedMessage msg = null;
			try {
				msg = messageQueue.poll(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(msg != null) {
				SocketForwarder fw = null;
				switch (msg.getType()) {
				case CONNECTION_REQUEST:
					newConnectionRequest(msg);
					break;
				case CONNECTION_ACCEPTED:
					// Send notification to ClientSocketForwarder
					debug("accept message received: "+ msg);
					synchronized (mappings) {
						fw = mappings.get(new ConnectionID(msg.getSenderID(), msg.getSenderPort(), msg.getTargetPort()));
					}
					if(fw != null && (fw instanceof ClientSocketForwarder)) {
						ClientSocketForwarder client = (ClientSocketForwarder) fw;
						client.notifyAccept();
					}
					break;
				case CONNECTION_ABORTED:
					// Send notification to ClientSocketForwarder
					debug("abort message received: "+ msg);
					synchronized (mappings) {
						fw = mappings.get(new ConnectionID(msg.getSenderID(), msg.getSenderPort(), msg.getTargetPort()));
					}
					if(fw != null) {
						fw.notifyAbort();
						logger.info("Connection aborted : "+(String) msg.getData());
					}
					break;
				case DATA:
					// Send data to SocketForwarder
					debug("data message received: "+ msg);
					synchronized (mappings) {
						fw = mappings.get(new ConnectionID(msg.getSenderID(), msg.getSenderPort(), msg.getTargetPort()));
					}
					if(fw != null) fw.receivedData((byte[]) msg.getData());
					break;
				}
			} else if (willClose) {
				isRunning = false;
			}
		}
		closeConnection();

	}

	private void debug(String string) {
		if(logger.isDebugEnabled())
			logger.debug(string);
	}

	/**
	 * Handle a new connection from a remote client.
	 * @param msg the connection request message.
	 */
	private void newConnectionRequest(ForwardedMessage msg) {

		debug("New connection request from "+msg.getSenderID()+":"+
					msg.getSenderPort()+" on port "+msg.getTargetPort());
		SocketForwarder fw = null;
		// look if Connection already exist from this.
		synchronized (mappings) {
			fw = mappings.get(new ConnectionID(msg.getSenderID(), msg.getSenderPort(), msg.getTargetPort()));
		}
		if(fw != null) {
			// TODO Should not be accepted.
			logger.warn("Duplicated connection request detected for message "+msg);
			return;
		}
		
		// TRY TO ESTABLISH CONNECTION
		fw = new ServerSocketForwarder(msg.getTargetID(), msg.getTargetPort(), msg.getSenderID(), msg.getSenderPort(), outHandler);

		// If OK, send CONNECTION_ACCEPT message and register Socket
		synchronized (mappings) {
			mappings.put(new ConnectionID(msg.getSenderID(), msg.getSenderPort(), msg.getTargetPort()), fw);
		}
	}

	private void closeConnection() {
		// TODO close all existing connections ?
		debug("Closing connection");
		outHandler.stop(false);
		incomingDispatcher.stop();
	}

	/**
	 * Store a new received message to handle.
	 * @param msg
	 */
	public void receivedMessage(ForwardedMessage msg) {
		if(isRunning && !willClose) {
			try {
				boolean notFull = messageQueue.offer(msg);
				if(!notFull) logger.warn("Queue is full; dropping message");
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Put a message to send to the registry.
	 * @param msg
	 */
	public void messageToSend(ForwardedMessage msg) {
		outHandler.putMessage(msg);
	}

	/**
	 * Create a new connection from the client to a remote target.
	 * @param msg
	 * @return
	 */
	public ClientSocketForwarder createNewConnection(ForwardedMessage msg) {
		// msg = CONNECTION_REQUEST [from=localID:#port; to=destination:port]
		SocketForwarder prev = null;
		synchronized (mappings) {
			prev = mappings.get(new ConnectionID(msg.getTargetID(), msg.getTargetPort(), msg.getSenderPort()));
		}
		if(prev != null) {
			// TODO previous mapping already exist.
			logger.warn("previous mapping already exist for message " + msg);
		}
		
		ClientSocketForwarder fw = null;
		debug("Creating SocketForwarder with local port number "+msg.getSenderPort());
		fw = new ClientSocketForwarder(msg.getSenderID(), msg.getSenderPort(), msg.getTargetID(), msg.getTargetPort(), outHandler);
		synchronized (mappings) {
			mappings.put(new ConnectionID(msg.getTargetID(), msg.getTargetPort(), msg.getSenderPort()), fw);
		}
		outHandler.putMessage(msg);
		debug("New connection request sent.");
		return fw;
	}

	public void stop (boolean softly) {
		if(softly) {
			willClose = true;
		} else {
			isRunning = false;
		}
	}
}

/** 
 * Identify a connection in a unique way.
 */
class ConnectionID {
	private Object distantID;
	private int distantPort;
	private int localPort;

	public ConnectionID(Object distantID, int distantPort, int localPort) {
		this.distantID = distantID;
		this.distantPort = distantPort;
		this.localPort = localPort;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof ConnectionID) {
			ConnectionID id = (ConnectionID) o;
			return id.distantID.equals(this.distantID) && id.distantPort == this.distantPort && id.localPort == this.localPort;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return distantPort+localPort+distantID.hashCode();
	}
	
}
