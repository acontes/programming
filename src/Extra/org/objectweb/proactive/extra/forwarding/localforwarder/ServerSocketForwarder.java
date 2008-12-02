package org.objectweb.proactive.extra.forwarding.localforwarder;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.objectweb.proactive.core.util.ProActiveInet;
import org.objectweb.proactive.extra.forwarding.common.ForwardedMessage;
import org.objectweb.proactive.extra.forwarding.common.OutHandler;


/**
 * Handle a connection when initiated by a remote peer.
 */
public class ServerSocketForwarder extends SocketForwarder {

    public ServerSocketForwarder(Object localID, int localPort, Object targetID, int targetPort,
            OutHandler tunnel, LocalConnectionHandler handler) {
        super(localID, localPort, targetID, targetPort, tunnel, handler);
    }

    /**
     * Try to establish a connection to the server port.
     * reply a connection aborted or accepted message to the sender.
     */
    @Override
    protected void initSocket() {
        // create connection to server
        try {
            sockToHandle = new Socket(ProActiveInet.getInstance().getInetAddress(), localPort);
            tunnel.putMessage(ForwardedMessage.acceptMessage(localID, localPort, targetID, targetPort));
            startHandling();
        } catch (UnknownHostException e) {
            logger.warn("Unknown host: "+ProActiveInet.getInstance().getInetAddress(), e);
        } catch (IOException e) {
        	logger.warn("Unable to connect to host: "+ProActiveInet.getInstance().getInetAddress(), e);
            tunnel.putMessage(ForwardedMessage.abortMessage(localID, localPort, targetID, targetPort, e
                    .getMessage()));
        }
    }

}
