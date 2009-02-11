package org.objectweb.proactive.extra.messagerouting.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.remoteobject.http.util.HttpMarshaller;
import org.objectweb.proactive.core.util.ProActiveRandom;
import org.objectweb.proactive.extra.messagerouting.client.Agent;
import org.objectweb.proactive.extra.messagerouting.client.AgentImpl;
import org.objectweb.proactive.extra.messagerouting.client.ProActiveMessageHandler;
import org.objectweb.proactive.extra.messagerouting.client.Tunnel;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DataReplyMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.DataMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;
import org.objectweb.proactive.extra.messagerouting.protocol.message.RegistrationReplyMessage;
import org.objectweb.proactive.extra.messagerouting.protocol.message.Message.MessageType;
import org.objectweb.proactive.extra.messagerouting.remoteobject.message.MessageRoutingMessage;


public class TestForwardingAgentV2 {
    FakeRegistry reg = null;
    static final String PAYLOAD = "Il est 18h";

    @Before
    public void setup() throws IOException {
        reg = new FakeRegistry();
    }

    @After
    public void tearDown() {
        // ?
    }

    @Test
    public void testMarshalling() throws Exception {
        HelloMessage mess = new HelloMessage();
        byte[] data = HttpMarshaller.marshallObject(mess);

        HelloMessage message = (HelloMessage) HttpMarshaller.unmarshallObject(data);
        Object result = null;
        if (message != null) {
            result = message.processMessage();
            Assert.assertEquals(PAYLOAD, result);

            byte[] resultBytes = HttpMarshaller.marshallObject(result);
            Assert.assertNotNull(resultBytes);

            Assert.assertEquals(PAYLOAD, HttpMarshaller.unmarshallObject(resultBytes));
        }

    }

    @Test
    public void testInitialize() throws Exception {
        Agent agent = new AgentImpl(InetAddress.getLocalHost(), reg.getPort(), ProActiveMessageHandler.class);
        Assert.assertNotNull(agent);
    }

    /**
     * Test the sending of a request containing real data to execute You must
     * send the message to the AgentID #9999
     */
    @Test
    public void testSendMessageWithReply() throws Exception {
        Agent agent = new AgentImpl(InetAddress.getLocalHost(), reg.getPort(), ProActiveMessageHandler.class);
        Assert.assertNotNull(agent);
        System.out.println("Sending Crafted Message");

        HelloMessage mess = new HelloMessage();
        byte[] data = HttpMarshaller.marshallObject(mess);

        byte[] result = agent.sendMsg(new AgentID(9999l), data, false);
        Assert
                .assertEquals("Result should be 'Il est 18h'", PAYLOAD, HttpMarshaller
                        .unmarshallObject(result));
    }

    /**
     * Not Valid for now, because the Agent Should respond to a oneway
     * message...
     */
    @Test
    public void testSendMsgWithoutReply() throws Exception {
        Agent agent = new AgentImpl(InetAddress.getLocalHost(), reg.getPort(), ProActiveMessageHandler.class);
        Assert.assertNotNull(agent);
        System.out.println("Sending Crafted Message");

        HelloMessage mess = new HelloMessage();
        byte[] data = HttpMarshaller.marshallObject(mess);

    }

}

class FakeRegistry implements Runnable {
    private volatile boolean isRunning;
    final private ServerSocket server;

    public FakeRegistry() throws IOException {
        this.server = new ServerSocket(0);
        System.out.println("FakeReg: Start listenning on port " + server.getLocalPort());

        isRunning = true;

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
            System.out.println("FakeReg: Connection accepted, reading registration message");
            Tunnel tunnel = new Tunnel(sock);
            Message m = Message.constructMessage(tunnel.readMessage(), 0);
            // Must be a registration message
            Assert.assertEquals(m.getType(), MessageType.REGISTRATION_REQUEST);

            // Put a registration reply

            Message resp = new RegistrationReplyMessage(new AgentID(1l), ProActiveRandom.nextPosLong());
            sock.getOutputStream().write(resp.toByteArray());
            sock.getOutputStream().flush();

            while (isRunning) {
                m = Message.constructMessage(tunnel.readMessage(), 0);
                System.out.println("FakeReg: Message Received: " + m);

                if (m instanceof DataMessage) {
                    DataMessage fm = (DataMessage) m;
                    resp = new DataReplyMessage(fm.getRecipient(), fm.getSender(), fm.getMessageID(),
                        HttpMarshaller.marshallObject(TestForwardingAgentV2.PAYLOAD));

                    System.out.println("FakeReg: Forwarding message: " + resp);
                    sock.getOutputStream().write(resp.toByteArray());
                    sock.getOutputStream().flush();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Exception due to socket closed : " + e.getLocalizedMessage());

        }
    }

    public void stop() {
        isRunning = false;
    }
}

@SuppressWarnings("serial")
class HelloMessage extends MessageRoutingMessage {
    public HelloMessage() {
        super(null, null);
    }

    @Override
    public Object processMessage() throws Exception {
        return TestForwardingAgentV2.PAYLOAD;
    }

};
