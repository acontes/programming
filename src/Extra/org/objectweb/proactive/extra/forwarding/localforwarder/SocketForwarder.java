package org.objectweb.proactive.extra.forwarding.localforwarder;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.objectweb.proactive.extra.forwarding.common.ForwardedMessage;
import org.objectweb.proactive.extra.forwarding.common.OutHandler;
import org.objectweb.proactive.extra.forwarding.tests.TestLogger;

/**
 * The {@link SocketForwarder} is in charge of forwarding data from and to the client or server.
 * each bunch of data is encapsulated in a {@link ForwardedMessage} in order to be correctly sent
 * throw the registry.
 */
public abstract class SocketForwarder{
	//protected Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);
	public static final Logger logger = TestLogger.getLogger();
	public static final int BUFFER_SIZE = 1024;

	// Tunnel
	protected OutHandler tunnel;

	// Local ID & port
	protected Object localID;
	protected int localPort;

	// Remote ID & port
	protected int targetPort;
	protected Object targetID;

	protected Socket sockToHandle;
	protected SocketReader reader;
	protected SocketWritter writter;

	public SocketForwarder(Object localID, int localPort, Object targetID, int targetPort, OutHandler tunnel){
		this.localPort = localPort;
		this.targetPort = targetPort;
		this.localID = localID;
		this.targetID = targetID;
		this.tunnel = tunnel;
		sockToHandle = null;

		initSocket();
	}

	protected abstract void initSocket();

	/**
	 *  Encapsulate data and send it.
	 * @param data
	 * @param size
	 */
	protected void outgoingData(byte[] data, int size) {
		// TODO See if copy array or forward not full array.
		byte[] buf = new byte[size];
		System.arraycopy(data, 0, buf, 0, size);
		tunnel.putMessage(ForwardedMessage.dataMessage(localID, localPort, targetID, targetPort, buf));
	}

	/**
	 * Start handling incoming and outgoing messages.
	 */
	protected void startHandling() {
		reader = new SocketReader(sockToHandle, this);
		writter = new SocketWritter(sockToHandle, this);
		new Thread(reader).start();
		new Thread(writter).start();
	}

	/**
	 * receive message from the outside,
	 * forward the data to the socket.
	 * @param data
	 */
	public void receivedData(byte[] data) {
		writter.enqueueData(data);
	}

	public void stop() {
		// TODO handle stopping
		reader.stop();
		writter.stop();
		closeConnection();
	}

	public void notifyAbort() {
		closeConnection();
	}

	protected void closeConnection(){
		try {
			sockToHandle.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void abort(String cause) {
		tunnel.putMessage(ForwardedMessage.abortMessage(localID, localPort, targetID, targetPort, cause));
	}

	private class SocketReader implements Runnable {

		private Socket sock;
		private boolean isRunning;
		private SocketForwarder forw;

		public SocketReader(Socket sock, SocketForwarder forw) {
			this.sock = sock;
			isRunning = true;
			this.forw = forw;
		}

		public void run() {
			byte[] buf = new byte[BUFFER_SIZE];
			while(isRunning) {
				try {
					int read = sock.getInputStream().read(buf);
					forw.outgoingData(buf, read);
				} catch (IOException e) {
					// TODO Handle disconnection
					e.printStackTrace();
					isRunning = false;
					forw.abort(e.getMessage());
				}
			}
		}

		public void stop() {
			isRunning = false;
		}

	}

	private class SocketWritter implements Runnable {

		private Socket sock;
		private boolean isRunning;
		private SocketForwarder forw;
		private LinkedBlockingQueue<byte[]> messageQueue;

		public SocketWritter(Socket sock, SocketForwarder forw) {
			messageQueue = new LinkedBlockingQueue<byte[]>();
			this.sock = sock;
			isRunning = true;
			this.forw = forw;
		}

		public void run() {
			byte[] buf = null;
			while(isRunning) {
				try {
					try {
						buf = messageQueue.poll(1, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(buf != null) {
						sock.getOutputStream().write(buf, 0, buf.length);
						sock.getOutputStream().flush();
					}
				} catch (IOException e) {
					// TODO Handle disconnection
					e.printStackTrace();
					isRunning = false;
					forw.abort(e.getMessage());
				}
			}
		}

		public void enqueueData(byte[] data) {
			messageQueue.offer(data);
		}

		public void stop() {
			isRunning = false;
		}

	}

}
