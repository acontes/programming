package org.objectweb.proactive.extra.forwardingv2.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.TypeHelper;


/** The entity in charge to send and receive data on the wire
 *
 * Tunnel manager goal is to keep the connection to the router open and
 * to send and receive the data. It is the lowest level entity involved in
 * Message Routing
 * 
 * Error handling performed by higher level component. If the connection goes down
 * or something bad happens then an IOException is thrown. It is the responsibility
 * of the caller to create a new tunnel  
 */
public class Tunnel {
    static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_CLIENT_TUNNEL);

    final private Socket socket;

    public Tunnel(InetAddress routerAddr, int routerPort) throws IOException {
        this.socket = new Socket(routerAddr, routerPort);

        // Configure the socket
        this.socket.setKeepAlive(true);
    }

    public void write(byte[] buf) throws IOException {
        this.write(buf, 0, buf.length, 0);
    }

    public void write(byte[] buf, long timeout) throws IOException {
        this.write(buf, 0, buf.length, timeout);
    }

    public void write(byte[] buf, int offset, int length, long timeout) throws IOException {
        this.socket.getOutputStream().write(buf, offset, length);
        this.socket.getOutputStream().flush();
    }

    public void read(byte[] buf) throws IOException {
        this.read(buf, 0, buf.length, 0);
    }

    public void read(byte[] buf, long timeout) throws IOException {
        this.read(buf, 0, buf.length, timeout);
    }

    public void read(byte[] buf, int offset, int length, long timeout) throws IOException {
        int retVal = this.socket.getInputStream().read(buf, offset, length);
        if (retVal != length) {
            // According to the InputStream.read() contract there is nothing we can do
            // Just warn the caller that something went wrong
            throw new IOException("Failed to read " + length + " byte (read returned only " + length +
                " bytes)");
        }
    }

    public boolean shouldWork() {
        if (this.socket == null)
            return false;

        return this.socket.isConnected();
    }

    public void shutdown() {
        try {
            this.socket.close();
        } catch (IOException e) {
            // We were trying to close a socket gracefully
            // Can't do anything better than log the event and ignore the error
            logger.info("Failed to close the socket " + this.socket);
        }
    }

    public byte[] readMessage() throws IOException {
        byte[] arrayLength = new byte[4];

        // Read the first four bytes (message length)
        this.read(arrayLength);
        int messageLength = TypeHelper.byteArrayToInt(arrayLength, 0);

        byte[] msg = new byte[messageLength];
        System.arraycopy(arrayLength, 0, msg, 0, arrayLength.length);

        this.read(msg, 4, messageLength - 4, 0);
        return msg;
    }
}