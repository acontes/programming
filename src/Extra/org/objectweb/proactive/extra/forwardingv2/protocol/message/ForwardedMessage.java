package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.TypeHelper;

public abstract class ForwardedMessage extends Message {

    // 1 long for the length, 2 int for protoID and MsgType, 2 long for SrcAgent and DstAgent IDs, 1 long for MSG_ID 
    public static final int FORWARDED_MESSAGE_HEADER_LENGTH = GLOBAL_COMMON_OFFSET + 2 * 8 + 8; // = 36

    public enum Offsets {
        SRC_AGENT_ID_OFFSET(0), DST_AGENT_ID_OFFSET(8), MSG_ID_OFFSET(16);

        private final int value;

        private Offsets(int value) {
            this.value = GLOBAL_COMMON_OFFSET + value;
        }

        public int getValue() {
            return this.value;
        }
    }

    //attributes
    protected MessageType type;
    protected AgentID srcAgentID, dstAgentID;
    protected long msgID;
    protected byte[] data;

    public ForwardedMessage(MessageType type, AgentID srcAgentID, AgentID dstAgentID, long msgID, byte[] data) {
        this.type = type;
        this.srcAgentID = srcAgentID;
        this.dstAgentID = dstAgentID;
        this.msgID = msgID;
        this.data = data;
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    public ForwardedMessage(byte[] byteArray, int offset) {
        int datalength = readLength(byteArray, offset) - FORWARDED_MESSAGE_HEADER_LENGTH;
        data = new byte[datalength];

        type = readType(byteArray, offset);
        srcAgentID = readSrcAgentID(byteArray, offset);
        dstAgentID = readDstAgentID(byteArray, offset);
        msgID = readMessageID(byteArray, offset);

        for (int i = 0; i < datalength; i++) {
            data[i] = byteArray[FORWARDED_MESSAGE_HEADER_LENGTH + i];
        }
    }

    @Override
    public byte[] toByteArray() {
        int length = getLength();
        byte[] byteArray = new byte[length];

        TypeHelper.intToByteArray(length, byteArray, CommonOffsets.LENGTH_OFFSET.getValue());
        TypeHelper.intToByteArray(getProtoID(), byteArray, CommonOffsets.PROTO_ID_OFFSET.getValue());
        TypeHelper.intToByteArray(type.getValue(), byteArray, CommonOffsets.MSG_TYPE_OFFSET.getValue());
        if (srcAgentID != null) {
            TypeHelper.longToByteArray(srcAgentID.getId(), byteArray, Offsets.SRC_AGENT_ID_OFFSET.getValue());
        }
        if (dstAgentID != null) {
            TypeHelper.longToByteArray(dstAgentID.getId(), byteArray, Offsets.DST_AGENT_ID_OFFSET.getValue());
        }
        TypeHelper.longToByteArray(msgID, byteArray, Offsets.MSG_ID_OFFSET.getValue());

        if (data != null) {
            System.arraycopy(data, 0, byteArray, FORWARDED_MESSAGE_HEADER_LENGTH, data.length);
        }
        return byteArray;
    }

    /**
     * @return the total length of the formatted message (header length + data length)
     */
    public int getLength() {
        return FORWARDED_MESSAGE_HEADER_LENGTH + (data != null ? data.length : 0);
    }

    /**
     * Reads the srcAgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the srcAgentID of the formatted message
     */
    public static AgentID readSrcAgentID(byte[] byteArray, int offset) {
        return new AgentID(TypeHelper.byteArrayToLong(byteArray, offset +
            Offsets.SRC_AGENT_ID_OFFSET.getValue()));
    }

    /**
     * Reads the dstAgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the dstAgentID of the formatted message
     */
    public static AgentID readDstAgentID(byte[] byteArray, int offset) {
        return new AgentID(TypeHelper.byteArrayToLong(byteArray, offset +
            Offsets.DST_AGENT_ID_OFFSET.getValue()));
    }

    /**
     * Reads the MessageID of a formatted message beginning at a certain offset inside a buffer. 
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the MessageID of the formatted message
     */
    public static long readMessageID(byte[] byteArray, int offset) {
        return TypeHelper.byteArrayToLong(byteArray, offset + Offsets.MSG_ID_OFFSET.getValue());
    }

    public AgentID getSrcAgentID() {
        return srcAgentID;
    }

    public void setSrcAgentID(AgentID srcAgentID) {
        this.srcAgentID = srcAgentID;
    }

    public AgentID getDstAgentID() {
        return dstAgentID;
    }

    public void setDstAgentID(AgentID dstAgentID) {
        this.dstAgentID = dstAgentID;
    }

    public long getMsgID() {
        return msgID;
    }

    public void setMsgID(long msgID) {
        this.msgID = msgID;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
