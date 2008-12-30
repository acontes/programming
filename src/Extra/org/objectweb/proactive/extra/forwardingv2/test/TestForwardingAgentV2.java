package org.objectweb.proactive.extra.forwardingv2.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.remoteobject.http.util.HttpMarshaller;
import org.objectweb.proactive.extra.forwardingv2.client.AgentV2;
import org.objectweb.proactive.extra.forwardingv2.client.ForwardingAgentV2;
import org.objectweb.proactive.extra.forwardingv2.client.ProActiveMessageHandler;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.MessageInputStream;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DataReplyMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ForwardedMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationReplyMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message.MessageType;
import org.objectweb.proactive.extra.forwardingv2.remoteobject.message.MessageRoutingMessage;

import functionalTests.FunctionalTest;


public class TestForwardingAgentV2 extends FunctionalTest {
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
        AgentV2 agent = new ForwardingAgentV2(ProActiveMessageHandler.class);
        Assert.assertNotNull(agent);
        Assert.assertNull(agent.getAgentID());
        agent.initialize(InetAddress.getLocalHost(), reg.getPort());
        Assert.assertNotNull(agent.getAgentID());
    }

    /**
     * Test the sending of a request containing real data to execute You must
     * send the message to the AgentID #9999
     */
    @Test
    public void testSendMessageWithReply() throws Exception {
        AgentV2 agent = new ForwardingAgentV2(ProActiveMessageHandler.class);
        Assert.assertNotNull(agent);
        agent.initialize(InetAddress.getLocalHost(), reg.getPort());
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
        AgentV2 agent = new ForwardingAgentV2(ProActiveMessageHandler.class);
        Assert.assertNotNull(agent);
        agent.initialize(InetAddress.getLocalHost(), reg.getPort());
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
            MessageInputStream input = new MessageInputStream(sock.getInputStream());
            Message m = Message.constructMessage(input.readMessage(), 0);
            // Must be a registration message
            Assert.assertEquals(m.getType(), MessageType.REGISTRATION_REQUEST);

            // Put a registration reply

            Message resp = new RegistrationReplyMessage(new AgentID(1l));
            sock.getOutputStream().write(resp.toByteArray());
            sock.getOutputStream().flush();

            while (isRunning) {
                m = Message.constructMessage(input.readMessage(), 0);
                System.out.println("FakeReg: Message Received: " + m);

                if (m instanceof ForwardedMessage) {
                    ForwardedMessage fm = (ForwardedMessage) m;
                    resp = new DataReplyMessage(fm.getDstAgentID(), fm.getSrcAgentID(), fm.getMsgID(),
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
