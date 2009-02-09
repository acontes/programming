package functionalTests.messagerouting.message;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.core.util.ProActiveRandom;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DataReplyMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DataRequestMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DataMessage;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.Message.MessageType;

import unitTests.UnitTests;


public class TestMessageData extends UnitTests {
    static final int NB_CHECK = 100;

    /* Randomly construct valid data request then check all the fields.
     */
    @Test
    public void testDataRequest() throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        for (int i = 0; i < NB_CHECK; i++) {
            buildAndCheckDataRequest(DataRequestMessage.class, MessageType.DATA_REQUEST);
        }
    }

    /* Randomly construct valid data reply then check all the fields.
     */
    @Test
    public void testDataReply() throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        for (int i = 0; i < NB_CHECK; i++) {
            buildAndCheckDataRequest(DataReplyMessage.class, MessageType.DATA_REPLY);
        }
    }

    /* Construct a data request with a null payload
     */
    @Test
    public void testRequestNullData() throws InstantiationException {
        AgentID srcAgent = new AgentID(ProActiveRandom.nextPosLong());
        AgentID dstAgent = new AgentID(ProActiveRandom.nextPosLong());
        long msgId = ProActiveRandom.nextPosLong();

        DataRequestMessage rq = new DataRequestMessage(srcAgent, dstAgent, msgId, null);
        Assert.assertNull(rq.getData());
        byte[] buf = rq.toByteArray();
        rq = new DataRequestMessage(buf, 0);
        rq.getData();
    }

    /* Construct a data reply with a null payload
     */
    @Test
    public void testReplyNullData() throws InstantiationException {
        AgentID srcAgent = new AgentID(ProActiveRandom.nextPosLong());
        AgentID dstAgent = new AgentID(ProActiveRandom.nextPosLong());
        long msgId = ProActiveRandom.nextPosLong();

        DataReplyMessage rp = new DataReplyMessage(srcAgent, dstAgent, msgId, null);
        Assert.assertNull(rp.getData());
        byte[] buf = rp.toByteArray();
        rp = new DataReplyMessage(buf, 0);
        Assert.assertEquals(0, rp.getData().length);
    }

    private void buildAndCheckDataRequest(Class<? extends DataMessage> cl, MessageType type)
            throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        AgentID srcAgent = new AgentID(ProActiveRandom.nextPosLong());
        logger.info("srcAgent " + srcAgent);
        AgentID dstAgent = new AgentID(ProActiveRandom.nextPosLong());
        logger.info("dstAgent " + dstAgent);
        long msgId = ProActiveRandom.nextPosLong();
        logger.info("msgId " + msgId);
        byte[] data = new byte[ProActiveRandom.nextInt(100)];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) i;
        }
        logger.info("data.length " + data.length);

        Constructor<? extends DataMessage> constructor;
        constructor = cl.getConstructor(AgentID.class, AgentID.class, long.class, byte[].class);
        DataMessage m = (DataMessage) constructor.newInstance(srcAgent, dstAgent, msgId, data);

        Assert.assertEquals(Message.PROTOV1, m.getProtoID());
        Assert.assertEquals(type, m.getType());
        Assert.assertEquals(msgId, m.getMessageID());
        Assert.assertEquals(srcAgent, m.getSender());
        Assert.assertEquals(dstAgent, m.getRecipient());

        for (int i = 0; i < m.getData().length; i++) {
            Assert.assertEquals((byte) i, m.getData()[i]);
        }

        byte[] buf = m.toByteArray();
        Assert.assertEquals(buf.length, m.getLength());

        constructor = cl.getConstructor(byte[].class, int.class);
        DataMessage m2 = (DataMessage) constructor.newInstance(buf, 0);

        Assert.assertEquals(m.getLength(), m2.getLength());
        Assert.assertEquals(m.getProtoID(), m2.getProtoID());
        Assert.assertEquals(m.getType(), m2.getType());
        Assert.assertEquals(m.getMessageID(), m2.getMessageID());
        Assert.assertEquals(m.getSender(), m2.getSender());
        Assert.assertEquals(m.getRecipient(), m2.getRecipient());
        Assert.assertEquals(m.getData().length, m2.getData().length);

        for (int i = 0; i < m.getData().length; i++) {
            Assert.assertEquals(m.getData()[i], m2.getData()[i]);
        }
    }
}
