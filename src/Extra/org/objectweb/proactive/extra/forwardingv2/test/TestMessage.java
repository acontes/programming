package org.objectweb.proactive.extra.forwardingv2.test;

import junit.framework.Assert;

import org.bouncycastle.util.Arrays;
import org.junit.Test;
import org.objectweb.proactive.extra.forwardingv2.exceptions.ExecutionException;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DataReplyMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DataRequestMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.ErrorMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationReplyMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.RegistrationRequestMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message.MessageType;


public class TestMessage {
    AgentID srcID = new AgentID(2222l);
    AgentID dstID = new AgentID(1111l);
    long msgID = 1l;

    @Test
    public void testRegistrationRequestMessageWithoutID() {
        // SETUP PART
        RegistrationRequestMessage r = new RegistrationRequestMessage();
        Assert.assertNull(r.getAgentID());

        // GENERIC PART
        Assert.assertNotNull(r);

        byte[] b = r.toByteArray();
        Assert.assertNotNull(b);

        Message r2 = Message.constructMessage(b, 0);
        Assert.assertNotNull(r2);

        Assert.assertEquals(r, r2);
        Assert.assertTrue(Arrays.areEqual(b, r2.toByteArray()));

        // CUSTOM PART
        Assert.assertTrue(r2 instanceof RegistrationRequestMessage);
        RegistrationRequestMessage res = (RegistrationRequestMessage) r2;
        Assert.assertNull(res.getAgentID());
    }

    @Test
    public void testRegistrationRequestMessageWithID() {
        // SETUP PART
        RegistrationRequestMessage r = new RegistrationRequestMessage(srcID);
        Assert.assertNotNull(r.getAgentID());
        Assert.assertEquals(srcID, r.getAgentID());

        // GENERIC PART
        Assert.assertNotNull(r);

        byte[] b = r.toByteArray();
        Assert.assertNotNull(b);

        Message r2 = Message.constructMessage(b, 0);
        Assert.assertNotNull(r2);

        Assert.assertEquals(r, r2);
        Assert.assertTrue(Arrays.areEqual(b, r2.toByteArray()));

        // CUSTOM PART
        Assert.assertTrue(r2 instanceof RegistrationRequestMessage);
        RegistrationRequestMessage res = (RegistrationRequestMessage) r2;
        Assert.assertNotNull(res.getAgentID());
        Assert.assertEquals(srcID, res.getAgentID());
    }

    @Test
    public void testRegistrationReplyMessage() {
        // SETUP PART
        RegistrationReplyMessage r = new RegistrationReplyMessage(srcID);
        Assert.assertNotNull(r.getAgentID());
        Assert.assertEquals(srcID, r.getAgentID());

        // GENERIC PART
        Assert.assertNotNull(r);

        byte[] b = r.toByteArray();
        Assert.assertNotNull(b);

        Message r2 = Message.constructMessage(b, 0);
        Assert.assertNotNull(r2);

        Assert.assertEquals(r, r2);
        Assert.assertTrue(Arrays.areEqual(b, r2.toByteArray()));

        // CUSTOM PART
        Assert.assertTrue(r2 instanceof RegistrationReplyMessage);
        RegistrationReplyMessage res = (RegistrationReplyMessage) r2;
        Assert.assertNotNull(res.getAgentID());
        Assert.assertEquals(srcID, res.getAgentID());
    }

    @Test
    public void testDataRequestMessage() {
        // SETUP PART
        byte[] msg = "Hello".getBytes();
        DataRequestMessage r = new DataRequestMessage(srcID, dstID, msgID, msg, false);
        Assert.assertEquals(srcID, r.getSrcAgentID());
        Assert.assertEquals(dstID, r.getDstAgentID());
        Assert.assertEquals(msgID, r.getMsgID());
        Assert.assertTrue("Data is OK", Arrays.areEqual(msg, r.getData()));

        // GENERIC PART
        Assert.assertNotNull(r);

        byte[] b = r.toByteArray();
        Assert.assertNotNull(b);

        Message r2 = Message.constructMessage(b, 0);
        Assert.assertNotNull(r2);

        Assert.assertEquals(r, r2);
        Assert.assertTrue(Arrays.areEqual(b, r2.toByteArray()));

        // CUSTOM PART
        Assert.assertTrue(r2 instanceof DataRequestMessage);
        DataRequestMessage res = (DataRequestMessage) r2;
        Assert.assertEquals(srcID, res.getSrcAgentID());
        Assert.assertEquals(dstID, res.getDstAgentID());
        Assert.assertEquals(msgID, res.getMsgID());
        Assert.assertTrue("Data is OK", Arrays.areEqual(msg, res.getData()));
    }

    @Test
    public void testDataReplyMessage() {
        // SETUP PART
        byte[] msg = "Hello".getBytes();
        DataReplyMessage r = new DataReplyMessage(srcID, dstID, msgID, msg);
        Assert.assertEquals(srcID, r.getSrcAgentID());
        Assert.assertEquals(dstID, r.getDstAgentID());
        Assert.assertEquals(msgID, r.getMsgID());
        Assert.assertTrue("Data is OK", Arrays.areEqual(msg, r.getData()));

        // GENERIC PART
        Assert.assertNotNull(r);

        byte[] b = r.toByteArray();
        Assert.assertNotNull(b);

        Message r2 = Message.constructMessage(b, 0);
        Assert.assertNotNull(r2);

        Assert.assertEquals(r, r2);
        Assert.assertTrue(Arrays.areEqual(b, r2.toByteArray()));

        // CUSTOM PART
        Assert.assertTrue(r2 instanceof DataReplyMessage);
        DataReplyMessage res = (DataReplyMessage) r2;
        Assert.assertEquals(srcID, res.getSrcAgentID());
        Assert.assertEquals(dstID, res.getDstAgentID());
        Assert.assertEquals(msgID, res.getMsgID());
        Assert.assertTrue("Data is OK", Arrays.areEqual(msg, res.getData()));
    }

    @Test
    public void testExceptionMessage() {
        // SETUP PART
        ExecutionException ex = new ExecutionException("Blablabla");
        ErrorMessage r = new ErrorMessage(MessageType.ERR_DISCONNECTED_RCPT, srcID, dstID, msgID, ex);
        Assert.assertEquals(srcID, r.getSrcAgentID());
        Assert.assertEquals(dstID, r.getDstAgentID());
        Assert.assertEquals(msgID, r.getMsgID());
        Assert.assertEquals(ex.getClass(), r.getException().getClass());
        Assert.assertEquals(ex.getMessage(), r.getException().getMessage());

        // GENERIC PART
        Assert.assertNotNull(r);

        byte[] b = r.toByteArray();
        Assert.assertNotNull(b);

        Message r2 = Message.constructMessage(b, 0);
        Assert.assertNotNull(r2);

        Assert.assertEquals(r, r2);
        Assert.assertTrue(Arrays.areEqual(b, r2.toByteArray()));

        // CUSTOM PART
        Assert.assertTrue(r2 instanceof ErrorMessage);
        ErrorMessage res = (ErrorMessage) r2;
        Assert.assertEquals(srcID, res.getSrcAgentID());
        Assert.assertEquals(dstID, res.getDstAgentID());
        Assert.assertEquals(msgID, res.getMsgID());
        Assert.assertEquals(ex.getClass(), res.getException().getClass());
        Assert.assertEquals(ex.getMessage(), res.getException().getMessage());
    }

}
