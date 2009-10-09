package org.objectweb.proactive.core.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Session;


class SshProxySession extends Socket {

    private Session session;
    private boolean unused;

    private InputStream is;
    private OutputStream os;

    SshProxySession(Session sess) {
        this.session = sess;
        this.is = sess.getStdout();
        this.os = sess.getStdin();
    }

    public Socket getSocket() throws IOException {
        this.unused = false;
        return this;
    }

    public void close() throws IOException {
        this.os.flush();
        this.session.waitForCondition(ChannelCondition.EOF, 1);
        /* Now its hopefully safe to close the session */
        this.session.close();
        // Socket::close() 
        this.unused = true;
    }

    public boolean isUnused() {
        return this.unused;
    }

    // Socket methods
    @Override
    public InputStream getInputStream() throws IOException {
        return this.is;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.os;
    }

    public void flush() throws IOException {
        this.os.flush();
    }

    @Override
    public String toString() {
        return "Socket[ProxyCommand fake socket connected]";
    }
}
