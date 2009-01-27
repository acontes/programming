package org.objectweb.proactive.extra.forwardingv2.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.remoteobject.http.util.HttpMarshaller;
import org.objectweb.proactive.extra.forwardingv2.client.AgentV2;
import org.objectweb.proactive.extra.forwardingv2.client.ForwardingAgentV2;
import org.objectweb.proactive.extra.forwardingv2.client.ProActiveMessageHandler;
import org.objectweb.proactive.extra.forwardingv2.exceptions.RoutingException;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.MessageInputStream;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DataReplyMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DataRequestMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ErrorMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationReplyMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationRequestMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message.MessageType;
import org.objectweb.proactive.extra.forwardingv2.registry.nio.Router;


public class TestForwardingRegistryNIO implements Runnable {

    static int port = 10000;
    Router reg = null;
    MessageInputStream input = null;
    Socket tunnel = null;
    AgentID localID = null;

    @Before
    public void setup() throws IOException {
        System.out.println("----------------------- SETUP -------------------------");
        this.reg = new Router(++port);
        new Thread(this).start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws IOException {
        //reg.stop();
    }

    public void run() {
        reg.start();
    }

    @Test
    public void testConnection() throws Exception {
        System.out.println("------------------------ TEST -------------------------");
        Assert.assertNotNull(reg);
        System.out.println("Creating connection to registry on port " + port);
        tunnel = new Socket(InetAddress.getLocalHost(), port);
        Assert.assertNotNull(tunnel);
        input = new MessageInputStream(tunnel.getInputStream());
        Assert.assertNotNull(input);
        System.out.println("Tunnel created. Sending Registration Request.");

        RegistrationRequestMessage reg = new RegistrationRequestMessage();
        tunnel.getOutputStream().write(reg.toByteArray());
        tunnel.getOutputStream().flush();
        System.out.println("Registration sent.");

        Message resp = Message.constructMessage(input.readMessage(), 0);
        Assert.assertNotNull(resp);
        Assert.assertEquals(MessageType.REGISTRATION_REPLY, resp.getType());
        System.out.println("Registration reply received.");

        Assert.assertTrue("Reply type is OK", resp instanceof RegistrationReplyMessage);
        RegistrationReplyMessage mess = (RegistrationReplyMessage) resp;
        localID = mess.getAgentID();
        Assert.assertNotNull(localID);
        System.out.println("AgentID received = " + localID);
    }

    @Test
    public void testDataRequestMessage() throws Exception {
        testConnection();
        Assert.assertNotNull(tunnel);
        Assert.assertNotNull(input);

        // Connect Agent
        AgentV2 agent = new ForwardingAgentV2(InetAddress.getLocalHost(), port, ProActiveMessageHandler.class);

        AgentID targetID = agent.getAgentID();
        Assert.assertNotNull(targetID);

        // Send message
        HelloMessage mess = new HelloMessage();
        byte[] data = HttpMarshaller.marshallObject(mess);

        DataRequestMessage req = new DataRequestMessage(localID, targetID, 1l, data, false);
        
        System.out.println("Sending Hello Message to Router.");

        tunnel.getOutputStream().write(req.toByteArray());
        tunnel.getOutputStream().flush();

        System.out.println("Hello Message sent to Router. Waiting for reply.");

        Message result = Message.constructMessage(input.readMessage(), 0);
        Assert.assertNotNull(result);
        Assert.assertEquals(DataReplyMessage.class, result.getClass());

        DataReplyMessage reply = (DataReplyMessage) result;
        Assert.assertEquals("Result should be 'Il est 18h'", "Il est 18h", HttpMarshaller
                .unmarshallObject(reply.getData()));

        System.out.println("Reply arrived and OK.");
    }

    @Test
    public void testExceptionMessage() throws Exception {
        testConnection();
        Assert.assertNotNull(tunnel);
        Assert.assertNotNull(input);

        // Connect Agent
        AgentV2 agent = new ForwardingAgentV2(InetAddress.getLocalHost(), port, ProActiveMessageHandler.class);

        AgentID targetID = agent.getAgentID();
        Assert.assertNotNull(targetID);

        // Send message
        HelloMessage mess = new HelloMessage();
        byte[] data = HttpMarshaller.marshallObject(mess);

        DataRequestMessage req = new DataRequestMessage(localID, new AgentID(9999l), 1l, data, false);

        tunnel.getOutputStream().write(req.toByteArray());
        tunnel.getOutputStream().flush();

        Message result = Message.constructMessage(input.readMessage(), 0);
        Assert.assertNotNull(result);
        Assert.assertFalse("Result should not be a DataReplyMessage", DataReplyMessage.class
                .isAssignableFrom(result.getClass()));
        Assert.assertTrue("Message should be an exception", ErrorMessage.class.isAssignableFrom(result
                .getClass()));
        Assert.assertEquals(MessageType.ERR_UNKNOW_RCPT, result.getType());

        ErrorMessage reply = (ErrorMessage) result;
        Assert.assertTrue("Exception should be a subclass of RoutingException", RoutingException.class
                .isAssignableFrom(reply.getException().getClass()));
    }

}
