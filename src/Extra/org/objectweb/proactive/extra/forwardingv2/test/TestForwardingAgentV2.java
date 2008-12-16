package org.objectweb.proactive.extra.forwardingv2.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import junit.framework.Assert;

import org.bouncycastle.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.remoteobject.http.util.HttpMarshaller;
import org.objectweb.proactive.core.remoteobject.http.util.HttpMessage;
import org.objectweb.proactive.extra.forwarding.exceptions.ForwardingException;
import org.objectweb.proactive.extra.forwardingv2.client.AgentV2;
import org.objectweb.proactive.extra.forwardingv2.client.ForwardingAgentV2;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.MessageInputStream;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message.MessageType;


public class TestForwardingAgentV2 {
    static FakeRegistry reg = null;
    static int port = 9999;

    @Before
    public void setup() {
        reg = new FakeRegistry(++port);
    }

    @After
    public void tearDown() {
        // ?
    }

    @Test
    public void testMarshalling() {
        HttpMessage mess = new HelloHttpMessage();
        byte[] data = HttpMarshaller.marshallObject(mess);

        HttpMessage message = (HttpMessage) HttpMarshaller.unmarshallObject(data);
        Object result = null;
        if (message != null) {
            try {
                result = message.processMessage();
                Assert.assertEquals("Il est 18h", result);

                byte[] resultBytes = HttpMarshaller.marshallObject(result);
                Assert.assertNotNull(resultBytes);

                Assert.assertEquals("Il est 18h", HttpMarshaller.unmarshallObject(resultBytes));
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            }
        }

    }

    @Test
    public void testInitialize() {
        AgentV2 agent = new ForwardingAgentV2();
        Assert.assertNotNull(agent);
        try {
            agent.initialize(InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            Assert.fail(e.getMessage());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSendMsgWithReply() {
        AgentV2 agent = new ForwardingAgentV2();
        Assert.assertNotNull(agent);
        try {
            agent.initialize(InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            Assert.fail(e.getMessage());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        System.out.println("Sending Hello Message");

        try {
            byte[] result = agent.sendMsg(new AgentID(32l), "Hello".getBytes(), false);
            Assert.assertNotNull("Must have a result", result);
            Assert.assertTrue("Message should be strict echo", Arrays.areEqual("Hello".getBytes(), result));

        } catch (ForwardingException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());

        }

    }

    /**
     * Not Valid for now, because the Agent Should respond to a oneway message...
     */
    @Test
    public void testSendMsgWithoutReply() {
        Assert
                .fail("Not valid for now, because ForwardingAgent "
						+"is not able to handle oneWay messages for now "
						+"(it responds to oneWay messages and it shouldn't)");
        AgentV2 agent = new ForwardingAgentV2();
        Assert.assertNotNull(agent);
        try {
            agent.initialize(InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            Assert.fail(e.getMessage());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        System.out.println("Sending Hello Message");

        try {
            byte[] result = agent.sendMsg(new AgentID(32l), "Hello".getBytes(), true);
            Assert.assertNull("Result should be null", result);

        } catch (ForwardingException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());

        }
    }

    /**
     * Test the sending of a request containing real data to execute
     * You must send the message to the AgentID #9999
     */
    @Test
    public void testSendRealMessageToCraft() {
        AgentV2 agent = new ForwardingAgentV2();
        Assert.assertNotNull(agent);
        try {
            agent.initialize(InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            Assert.fail(e.getMessage());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        System.out.println("Sending Crafted Message");

        try {
            HttpMessage mess = new HelloHttpMessage();
            byte[] data = HttpMarshaller.marshallObject(mess);

            byte[] result = agent.sendMsg(new AgentID(9999l), data, false);
            Assert.assertEquals("Result should be 'Il est 18h'", "Il est 18h", HttpMarshaller
                    .unmarshallObject(result));
        } catch (ForwardingException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}

class FakeRegistry implements Runnable {
    private int port;
    private volatile boolean isRunning;

    public FakeRegistry(int port) {
        this.port = port;
        isRunning = true;

        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    public void run() {
        System.out.println("FakeReg: Starting fake registry");
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("FakeReg: Start listenning on port " + port);
            Socket sock = server.accept();
            System.out.println("FakeReg: Connection accepted, reading registration message");
            MessageInputStream input = new MessageInputStream(sock.getInputStream());
            Message m = new Message(input.readMessage(), 0);
            // Must be a registration message
            Assert.assertEquals(m.getType(), MessageType.REGISTRATION_REQUEST.getValue());

            // Put a registration reply
            Message resp = Message.registrationReplyMessage(new AgentID(1l));
            sock.getOutputStream().write(resp.toByteArray());
            sock.getOutputStream().flush();

            AgentID crafted = new AgentID(9999l);
            long craftedMessageID = 0;

            while (isRunning) {
                m = new Message(input.readMessage(), 0);
                System.out.println("FakeReg: Message Received: " + m);

                if (m.getDstAgentID().equals(crafted)) {
                    if (m.getMsgID() == 99999999l) {
                        m.setMsgID(craftedMessageID);
                    } else {
                        craftedMessageID = m.getMsgID();
                        m.setMsgID(99999999l);
                    }
                }
                AgentID tmp = m.getSrcAgentID();
                m.setSrcAgentID(m.getDstAgentID());
                m.setDstAgentID(tmp);
                System.out.println("FakeReg: Forwarding message: " + m);
                sock.getOutputStream().write(m.toByteArray());
                sock.getOutputStream().flush();
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

class HelloHttpMessage extends HttpMessage {
    public HelloHttpMessage() {
        super("Hello");
    }

    @Override
    public Object processMessage() throws Exception {
        return "Il est 18h";
    }

};
