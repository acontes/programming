package org.objectweb.proactive.extra.forwardingv2.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
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
        new MessageSender(12345, l);

            Socket sock = new Socket("localhost", 12345);
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
    List<Message> msg;
    int port;

    public MessageSender(int port, List<Message> msg) {
        this.port = port;
        this.msg = msg;
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    public void run() {
        try {
            ServerSocket server = new ServerSocket(port);
            Socket sock = server.accept();
            for (Message m : msg) {
                sock.getOutputStream().write(m.toByteArray());
                sock.getOutputStream().flush();
            }
            sock.close();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}