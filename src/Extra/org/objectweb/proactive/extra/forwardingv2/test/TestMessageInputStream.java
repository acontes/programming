package org.objectweb.proactive.extra.forwardingv2.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.MessageInputStream;


public class TestMessageInputStream {

    @Test
    public void testMessageReading() throws IOException {
        AgentID srcID = new AgentID(42l);
        AgentID dstID = new AgentID(12l);
        // create list
        ArrayList<Message> l = new ArrayList<Message>();
        Message m1 = Message.registrationRequestMessage();
        l.add(m1);
        Message m2 = Message.registrationReplyMessage(srcID);
        l.add(m2);
        Message m3 = Message.dataMessage(srcID, dstID, 100, "Hello".getBytes());
        l.add(m3);

        // Creating Server
        MessageSender messageSender = new MessageSender(l);

        Socket sock = new Socket("localhost", messageSender.getPort());
        Assert.assertNotNull(sock);

        MessageInputStream mis = new MessageInputStream(sock.getInputStream());
        Assert.assertNotNull(mis);

        // Read registration request
        Message m = new Message(mis.readMessage(), 0);
        Assert.assertEquals(m1, m);

        m = new Message(mis.readMessage(), 0);
        Assert.assertEquals(m2, m);

        m = new Message(mis.readMessage(), 0);
        Assert.assertEquals(m3, m);
    }

}

class MessageSender implements Runnable {
    private List<Message> messages;
    private ServerSocket server;

    public MessageSender(List<Message> msg) throws IOException {
        this.server = new ServerSocket(0);
        this.messages = msg;

        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    public int getPort() {
        return server.getLocalPort();
    }

    public void run() {
        try {
            Socket sock = server.accept();
            for (Message m : messages) {
                sock.getOutputStream().write(m.toByteArray());
                sock.getOutputStream().flush();
            }
            sock.close();
        } catch (Exception e) {
            Assert.fail(ProActiveLogger.getStackTraceAsString(e));
        }
    }
}