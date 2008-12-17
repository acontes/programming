package org.objectweb.proactive.extra.forwardingv2.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.Assert;

import org.bouncycastle.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.remoteobject.http.util.HttpMarshaller;
import org.objectweb.proactive.extra.forwardingv2.client.AgentV2;
import org.objectweb.proactive.extra.forwardingv2.client.ForwardingAgentV2;
import org.objectweb.proactive.extra.forwardingv2.client.ProActiveMessageHandler;
import org.objectweb.proactive.extra.forwardingv2.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.ForwardedMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.MessageInputStream;
import org.objectweb.proactive.extra.forwardingv2.protocol.RegistrationReplyMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.Message.MessageType;
import org.objectweb.proactive.extra.forwardingv2.remoteobject.message.MessageRoutingMessage;


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
    public void testMarshalling() throws Exception {
        HelloMessage mess = new HelloMessage();
        byte[] data = HttpMarshaller.marshallObject(mess);

        HelloMessage message = (HelloMessage) HttpMarshaller.unmarshallObject(data);
        Object result = null;
        if (message != null) {
            result = message.processMessage();
            Assert.assertEquals("Il est 18h", result);

            byte[] resultBytes = HttpMarshaller.marshallObject(result);
            Assert.assertNotNull(resultBytes);

            Assert.assertEquals("Il est 18h", HttpMarshaller.unmarshallObject(resultBytes));
        }

    }

    @Test
    public void testInitialize() throws Exception {
        AgentV2 agent = new ForwardingAgentV2(ProActiveMessageHandler.class);
        Assert.assertNotNull(agent);
        Assert.assertNull(agent.getAgentID());
        agent.initialize(InetAddress.getLocalHost(), port);
        Assert.assertNotNull(agent.getAgentID());
    }

    @Test
    public void testSendMsgWithReply() throws Exception {
        AgentV2 agent = new ForwardingAgentV2(ProActiveMessageHandler.class);
        Assert.assertNotNull(agent);
        agent.initialize(InetAddress.getLocalHost(), port);
        System.out.println("Sending Hello Message");

        byte[] result = agent.sendMsg(new AgentID(32l), "Hello".getBytes(), false);
        Assert.assertNotNull("Must have a result", result);
        Assert.assertTrue("Message should be strict echo", Arrays.areEqual("Hello".getBytes(), result));

    }

    /**
     * Not Valid for now, because the Agent Should respond to a oneway
     * message...
     */
    @Test
    public void testSendMsgWithoutReply() throws Exception {
        Assert.fail("Not valid for now, because ForwardingAgent "
            + "is not able to handle oneWay messages for now "
            + "(it responds to oneWay messages and it shouldn't)");
        AgentV2 agent = new ForwardingAgentV2(ProActiveMessageHandler.class);
        Assert.assertNotNull(agent);
        agent.initialize(InetAddress.getLocalHost(), port);
        System.out.println("Sending Hello Message");

        try {
            byte[] result = agent.sendMsg(new AgentID(32l), "Hello".getBytes(), true);
            Assert.assertNull("Result should be null", result);

        } catch (MessageRoutingException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());

        }
    }

    /**
     * Test the sending of a request containing real data to execute You must
     * send the message to the AgentID #9999
     */
    @Test
    public void testSendRealMessageToCraft() throws Exception {
        AgentV2 agent = new ForwardingAgentV2(ProActiveMessageHandler.class);
        Assert.assertNotNull(agent);
        agent.initialize(InetAddress.getLocalHost(), port);
        System.out.println("Sending Crafted Message");

        HelloMessage mess = new HelloMessage();
        byte[] data = HttpMarshaller.marshallObject(mess);

        byte[] result = agent.sendMsg(new AgentID(9999l), data, false);
        Assert.assertEquals("Result should be 'Il est 18h'", "Il est 18h", HttpMarshaller
                .unmarshallObject(result));
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
            Message m = Message.constructMessage(input.readMessage(), 0);
            // Must be a registration message
            Assert.assertEquals(m.getType(), MessageType.REGISTRATION_REQUEST);

            // Put a registration reply
            
            Message resp = new RegistrationReplyMessage(new AgentID(1l));
            sock.getOutputStream().write(resp.toByteArray());
            sock.getOutputStream().flush();

            AgentID crafted = new AgentID(9999l);
            long craftedMessageID = 0;

            while (isRunning) {
                m = Message.constructMessage(input.readMessage(), 0);
                System.out.println("FakeReg: Message Received: " + m);

                if(m instanceof ForwardedMessage) {
                	ForwardedMessage fm = (ForwardedMessage) m;
                	if (fm.getDstAgentID().equals(crafted)) {
                        if (fm.getMsgID() == 99999999l) {
                            fm.setMsgID(craftedMessageID);
                        } else {
                            craftedMessageID = fm.getMsgID();
                            fm.setMsgID(99999999l);
                        }
                    }
                    AgentID tmp = fm.getSrcAgentID();
                    fm.setSrcAgentID(fm.getDstAgentID());
                    fm.setDstAgentID(tmp);
                    System.out.println("FakeReg: Forwarding message: " + m);
                    sock.getOutputStream().write(m.toByteArray());
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
        return "Il est 18h";
    }

};
