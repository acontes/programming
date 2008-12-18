package org.objectweb.proactive.extra.forwardingv2.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.extra.forwardingv2.protocol.MessageInputStream;
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
	
	@Before
	public void setup() {
		reg = new ForwardingRegistry(++port);
		final ForwardingRegistry thisReg = reg;
		Thread t = new Thread(new Runnable() {
			public void run() { thisReg.start(); }
		});
		t.start();
	}
	
	@Test
	public void testConnection() throws Exception {
		Assert.assertNotNull(reg);
		System.out.println("Creating connection to registry on port "+port);
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
        Assert.assertNotNull(mess.getAgentID());
        System.out.println("AgentID received = "+ mess.getAgentID());
	}

}
