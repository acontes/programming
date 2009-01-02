package org.objectweb.proactive.extra.forwardingv2.test;

import java.net.InetAddress;
import java.net.Socket;

import junit.framework.Assert;

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
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ExceptionMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationReplyMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationRequestMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message.MessageType;
import org.objectweb.proactive.extra.forwardingv2.registry.ForwardingRegistry;


public class TestForwardingRegistry {

    static int port = 10000;
    ForwardingRegistry reg = null;
    MessageInputStream input = null;
    Socket tunnel = null;
    AgentID localID = null;

    @Before
    public void setup() {
        this.reg = new ForwardingRegistry(++port, true);
    }

    @Test
    public void testConnection() throws Exception {
        Assert.assertNotNull(reg);
        System.out.println("Creating connection to registry on port " + port);
        tunnel = new Socket(InetAddress.getLocalHost(), port);
        Assert.assertNotNull(tunnel);
        input = new MessageInputStream(tunnel.getInputStream());
        Assert.assertNotNull(input);

        RegistrationRequestMessage reg = new RegistrationRequestMessage();
        tunnel.getOutputStream().write(reg.toByteArray());
        tunnel.getOutputStream().flush();

        Message resp = Message.constructMessage(input.readMessage(), 0);
        Assert.assertNotNull(resp);
        Assert.assertEquals(MessageType.REGISTRATION_REPLY, resp.getType());

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

        tunnel.getOutputStream().write(req.toByteArray());
        tunnel.getOutputStream().flush();

        Message result = Message.constructMessage(input.readMessage(), 0);
        Assert.assertNotNull(result);
        Assert.assertEquals(DataReplyMessage.class, result.getClass());

        DataReplyMessage reply = (DataReplyMessage) result;
        Assert.assertEquals("Result should be 'Il est 18h'", "Il est 18h", HttpMarshaller
                .unmarshallObject(reply.getData()));
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
        Assert.assertTrue("Message should be an exception", ExceptionMessage.class.isAssignableFrom(result
                .getClass()));
        Assert.assertEquals(MessageType.ROUTING_EXCEPTION_MSG, result.getType());

        ExceptionMessage reply = (ExceptionMessage) result;
        Assert.assertTrue("Exception should be a subclass of RoutingException", RoutingException.class
                .isAssignableFrom(reply.getException().getClass()));
    }

}
