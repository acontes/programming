package org.objectweb.proactive.extra.forwardingv2.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

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
import org.objectweb.proactive.extra.forwardingv2.registry.ForwardingRegistry;
import org.objectweb.proactive.extra.forwardingv2.registry.nio.Router;
import org.objectweb.proactive.extra.forwardingv2.remoteobject.message.MessageRoutingMessage;


public class TestForwardingRegistryNIO {

	static int port = 10000;
	Router reg = null;
	//ForwardingRegistry reg = null;
	MessageInputStream input = null;
	Socket tunnel = null;
	AgentID localID = null;

	@Before
	public void setup() throws IOException {
		System.out.println("----------------------- SETUP -------------------------");
		//this.reg = new ForwardingRegistry(++port, true);
		this.reg = new Router(++port, true);
	}

	@After
	public void tearDown() throws IOException {
		//reg.stop();
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

	@Test
	public void testMultipleMessage() throws Exception {
		int nb_tries = 10000;
		testConnection();
		Assert.assertNotNull(tunnel);
		Assert.assertNotNull(input);

		// Connect Agent
		AgentV2 agent = new ForwardingAgentV2(InetAddress.getLocalHost(), port, ProActiveMessageHandler.class);
		Assert.assertNotNull(agent);

		AgentID targetID = agent.getAgentID();
		Assert.assertNotNull(targetID);

		long current = 0l;
		int step = nb_tries / 50;
		Random rand = new Random();
		// Send messages
		System.out.println("Start sending "+nb_tries+" messages.");
		while(current++ < nb_tries) {
			if(current > step) {
				System.out.println("Currently done: "+step);
				step += nb_tries / 50;
			}
			String res = Long.toString(rand.nextLong());
			StringMessage msg = new StringMessage(res);
			byte[] data = HttpMarshaller.marshallObject(msg);
			DataRequestMessage req = new DataRequestMessage(localID, targetID, current, data, false);
			tunnel.getOutputStream().write(req.toByteArray());
			tunnel.getOutputStream().flush();
			Message result = Message.constructMessage(input.readMessage(), 0);
			Assert.assertNotNull(result);
			Assert.assertEquals(DataReplyMessage.class, result.getClass());

			DataReplyMessage reply = (DataReplyMessage) result;
			Assert.assertEquals(res, HttpMarshaller.unmarshallObject(reply.getData()));
		}

		System.out.println("Fully Done.");

	}

	@Test
	public void testCharge() throws Exception{
		AgentV2[] agents = new AgentV2[Runner.RECEIVERS];
		for(int i = 0; i < Runner.RECEIVERS; i++) {
			agents[i] = new ForwardingAgentV2(InetAddress.getLocalHost(), port, ProActiveMessageHandler.class);
		}

		CountDownLatch latch = new CountDownLatch(Runner.SENDERS);

		for(int i = 0; i < Runner.SENDERS; i++) {
			// Create thread
			Thread t = new Thread(new Runner(i, port, agents, latch));
			t.setName("Test Thread #"+i);
			t.start();
		}
		latch.await();
	}

}


@SuppressWarnings("serial")
class StringMessage extends MessageRoutingMessage {
	String payload;

	public StringMessage(String s) {
		super(null, null);
		this.payload = s;
	}

	@Override
	public Object processMessage() throws Exception {
		return payload;
	}

};

class Runner implements Runnable {
	public static final int MSG_NUMBER = 150;
	public static final int SENDERS = 1000;
	public static final int RECEIVERS = 150;
	private int index;
	private int port;
	private AgentV2[] targets;
	private CountDownLatch latch;

	public Runner(int i, int port, AgentV2[] targets, CountDownLatch latch) {
		this.index = i;
		this.port = port;
		this.targets = targets;
		this.latch = latch;
	}

	public void run() {
		try {
			Socket tunnel = new Socket(InetAddress.getLocalHost(), port);
			Assert.assertNotNull(tunnel);
			MessageInputStream input = new MessageInputStream(tunnel.getInputStream());
			Assert.assertNotNull(input);
			System.out.println("Tunnel #"+index+" created. Sending Registration Request.");

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
			AgentID localID = mess.getAgentID();
			Assert.assertNotNull(localID);
			System.out.println("AgentID received = " + localID);

			System.out.println("Thread #"+index+" sleeping 4 seconds");
			Thread.sleep(4000);

			System.out.println("Thread #"+index+" start sending");
			Random rand = new Random();
			for(int i=0; i < MSG_NUMBER; i++) {
				String res = Long.toString(rand.nextLong());
				StringMessage msg = new StringMessage(res);
				byte[] data = HttpMarshaller.marshallObject(msg);
				int rec = rand.nextInt(Runner.RECEIVERS+1);
				if(rec == Runner.RECEIVERS) {
					DataRequestMessage req = new DataRequestMessage(localID, new AgentID(9999999l), i, data, false);
					//System.out.println("Thread #"+index+" send request "+i+" to agentID "+9999999l);
					tunnel.getOutputStream().write(req.toByteArray());
					tunnel.getOutputStream().flush();
					Message result = Message.constructMessage(input.readMessage(), 0);
					//System.out.println("Thread #"+index+" got reply "+i+" from agentID "+9999999l);
					//Assert.assertNotNull(result);
					//Assert.assertTrue("Message should be an exception", ErrorMessage.class.isAssignableFrom(result
					//		.getClass()));
					
				} else {
					AgentID id = targets[rec].getAgentID();
					DataRequestMessage req = new DataRequestMessage(localID, id, i, data, false);
					//System.out.println("Thread #"+index+" send request "+i+" to agentID "+id);
					tunnel.getOutputStream().write(req.toByteArray());
					tunnel.getOutputStream().flush();
					Message result = Message.constructMessage(input.readMessage(), 0);
					//System.out.println("Thread #"+index+" got reply "+i+" from agentID "+id);
					//Assert.assertNotNull(result);
					//Assert.assertEquals(DataReplyMessage.class, result.getClass());

					DataReplyMessage reply = (DataReplyMessage) result;
					//Assert.assertEquals(res, HttpMarshaller.unmarshallObject(reply.getData()));
				}

			}
			latch.countDown();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}