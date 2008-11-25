package org.objectweb.proactive.extra.forwarding.localforwarder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.objectweb.proactive.extra.forwarding.common.ConnectionFailureListener;
import org.objectweb.proactive.extra.forwarding.common.ForwardedMessage;
import org.objectweb.proactive.extra.forwarding.exceptions.ForwardingException;
import org.objectweb.proactive.extra.forwarding.tests.TestLogger;

/**
 * Class used to connect to registry and to get forwarded sockets.
 *
 */
public class ForwardingAgent implements ConnectionFailureListener {
	
	// SINGLETON 
	private static ForwardingAgent _singleton = null;
	
	public static ForwardingAgent getAgent() {
		if(_singleton == null) {
			_singleton = new ForwardingAgent();
		}
		return _singleton;
	}
	
	// ATTRIBUTES
	public static final int NB_RETRY = 3;
	public static final long LOCK_TIME = 10000;
    //protected Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);
	public static final Logger logger = TestLogger.getLogger();
    protected LocalConnectionHandler localHandler;
    protected Object uniqueID;
    protected InetAddress regAddress;
    protected int regPort;
	
	// CONSTRUCTOR
	protected ForwardingAgent() {
	}
	
	// PUBLIC METHODS
	/**
	 * Initialize the <b>ForwardingAgent</b> with the address of the registry and the port to connect.
	 * @param uniqueID the uniqueID of the local JVM.
	 * @param regAddress the registry address
	 * @param regPort the registry port
	 */
	public void init(Object uniqueID, InetAddress regAddress, int regPort) throws ForwardingException{
		// TODO initialize agent.
		initialize();
		// Connect to the registry
		try {
			connect(uniqueID, regAddress, regPort);
		} catch(Exception e) {
			throw new ForwardingException("Error during initialization of ForwardingAgent", e);
		}
		
	}

	/**
	 * Cleanup method.
	 */
	public void cleanupAndExit() {
		// TODO clean all connections.
	}
	
	/**
	 * Get a forwarded socket.
	 * If an error occurs, then an exception is thrown.
	 * @param id the unique ID of the target.
	 * TODO Change the type of the ID
	 * @param port port to connect on the target host.
	 * @return the custom socket expected.
	 * @throws ForwardingException
	 */
	public Socket getSocket(Object id, int port) throws ForwardingException{

		if(logger.isDebugEnabled()) logger.debug("Trying to init forwarded connection to "+id+" on port "+port);
		// send connection request
		ClientSocketForwarder fw = localHandler.createNewConnection(ForwardedMessage.connectionMessage(uniqueID, id, port));
		// wait for response or timeout
		Socket socket = fw.getSocket(LOCK_TIME);
		
		if (socket != null) return socket;
		throw new ForwardingException("Not able to connect");
	}

	// PRIVATE METHODS
	
	/**
	 * 
	 */
	private void initialize() {
		// TODO Auto-generated method stub
		if(logger.isDebugEnabled()) logger.debug("Initializating Forwarding Agent");
	}

	/**
	 * Try to establish a connection to the registry.
	 * @param uniqueID the local UniqueID
	 * @param regAddress the registry {@link InetAddress}
	 * @param regPort the registry port.
	 * @throws IOException if the connection fails.
	 */
	private void connect(Object uniqueID, InetAddress regAddress, int regPort) 
		throws IOException{
		if(logger.isDebugEnabled()) logger.debug("Connecting to registry on "+regAddress+":"+regPort);
		
		Socket sock = null;
		int retry = NB_RETRY;
		// Store coordinates and ID
		this.uniqueID = uniqueID;
		this.regAddress = regAddress;
		this.regPort = regPort;
		
		// Connect a socket.
		while (sock == null && retry-- > 0) {
			try {
				sock = new Socket(regAddress, regPort);
			} catch (IOException e) {
				if(retry > 0) {
					logger.warn("Connection to registry failed. Retrying...");
				} else {
					logger.error("Connection to registry failed.", e);
					throw e;
				}
			}
		}

		if(logger.isDebugEnabled()) logger.debug("Connection successful. Registering with ID: "+uniqueID);
		
		// Start LocalConnectionHandler
		localHandler = new LocalConnectionHandler(sock, this);
		new Thread(localHandler).start();
		
		// Send registration message.
		localHandler.messageToSend(ForwardedMessage.registrationMessage(uniqueID));
		
	}

	/**
	 * @see  ConnectionFailureListener
	 */
	public void connectionHasFailed(Exception e) {
		// TODO Auto-generated method stub
		logger.fatal("Connection to registry has failed", e);
		localHandler.stop(false);
	}
	

}
