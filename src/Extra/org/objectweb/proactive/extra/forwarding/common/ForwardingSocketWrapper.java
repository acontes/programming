package org.objectweb.proactive.extra.forwarding.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.objectweb.proactive.extra.forwarding.tests.TestLogger;


/**
 * The socket wrapper encapsulate a socket, and provide a direct way to push object in the socket.
 */
public class ForwardingSocketWrapper {
    //static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);
    public static final Logger logger = TestLogger.getLogger();

    private Socket sock;

    private InetAddress inetAddress;

    public ForwardingSocketWrapper(Socket sock) {
        this.sock = sock;
        inetAddress = sock.getInetAddress();
    }

    public Socket getSocket() {
        return sock;
    }

    /**
     * Read an object from the socket.
     * @return the object read.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object readObject() throws IOException, ClassNotFoundException {
        if (!sock.isClosed()) {
            ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
            Object obj = in.readObject();
            //logger.debug("Read message from "+sock.getInetAddress()+" = "+obj);
            return obj;
        } else {
            throw new IOException("Socket closed : impossible to read object through it");
        }
    }

    /**
     * Write an object in the socket.
     * @param obj the object to write.
     * @throws IOException
     */
    public void writeObject(Object obj) throws IOException {
        if (!sock.isClosed()) {
            //logger.debug("Writing message to "+sock.getInetAddress()+" = "+obj);
            ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
            out.writeObject(obj);
            out.flush();
        } else {
            throw new IOException("Socket closed : impossible to write object through it");
        }
    }

    /**
     * Closing the socket.
     */
    public void close() {
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the {@link InetAddress} of the Socket.
     */
    public InetAddress getInetAddress() {
        return inetAddress;
    }

}
