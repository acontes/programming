package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import java.nio.ByteBuffer;

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
    final protected AgentID srcAgentID, dstAgentID;
    final protected long msgID;
    final protected byte[] data; // Since data is not used by the router should be lazily created to avoid data duplication
    final protected byte[] toByteArray;

    public ForwardedMessage(MessageType type, AgentID srcAgentID, AgentID dstAgentID, long msgID, byte[] data) {
        this.type = type;
        this.srcAgentID = srcAgentID;
        this.dstAgentID = dstAgentID;
        this.msgID = msgID;
        this.data = data;
        this.toByteArray = null;
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    public ForwardedMessage(byte[] byteArray, int offset) {
        int datalength = readLength(byteArray, offset) - FORWARDED_MESSAGE_HEADER_LENGTH;

        this.type = readType(byteArray, offset);
        this.srcAgentID = readSrcAgentID(byteArray, offset);
        this.dstAgentID = readDstAgentID(byteArray, offset);
        this.msgID = readMessageID(byteArray, offset);

        this.data = new byte[datalength];
        System.arraycopy(byteArray, offset + FORWARDED_MESSAGE_HEADER_LENGTH, this.data, 0, datalength);

        this.toByteArray = byteArray;
    }

    public String toString() {
        return "dest=" + this.dstAgentID + " src=" + this.srcAgentID + " msgID=" + this.msgID + " type=" +
            this.type;
    }

    @Override
    public byte[] toByteArray() {
        int length = getLength();
        byte[] byteArray = new byte[length];

        TypeHelper.intToByteArray(length, byteArray, CommonOffsets.LENGTH_OFFSET.getValue());
        TypeHelper.intToByteArray(getProtoID(), byteArray, CommonOffsets.PROTO_ID_OFFSET.getValue());
        TypeHelper.intToByteArray(type.ordinal(), byteArray, CommonOffsets.MSG_TYPE_OFFSET.getValue());
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

    @Override
    public ByteBuffer toByteBuffer() {
        int length = getLength();
        ByteBuffer buffer = ByteBuffer.allocate(length);

        buffer.putInt(length).putInt(getProtoID()).putInt(type.ordinal());
        if (srcAgentID != null) {
            buffer.putLong(srcAgentID.getId());
        }
        if (dstAgentID != null) {
            buffer.putLong(Offsets.DST_AGENT_ID_OFFSET.getValue(), dstAgentID.getId());
        }
        buffer.putLong(Offsets.MSG_ID_OFFSET.getValue(), msgID);

        buffer.position(FORWARDED_MESSAGE_HEADER_LENGTH);
        if (data != null) {
            buffer.put(data);
        }
        buffer.flip();
        return buffer;
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
     * @param buffer the ByteBuffer in which to read 
     * @return the dstAgentID of the formatted message
     */
    public static AgentID readSrcAgentID(ByteBuffer buffer) {
        return new AgentID(buffer.getLong(Offsets.SRC_AGENT_ID_OFFSET.getValue()));
    }

    /**
     * Reads the dstAgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param byteArray the array in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the dstAgentID of the formatted message
     */
    public static AgentID readDstAgentID(byte[] byteArray, int offset) {
        return new AgentID(TypeHelper.byteArrayToLong(byteArray, offset +
            Offsets.DST_AGENT_ID_OFFSET.getValue()));
    }

    /**
     * Reads the dstAgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param buffer the ByteBuffer in which to read 
     * @return the dstAgentID of the formatted message
     */
    public static AgentID readDstAgentID(ByteBuffer buffer) {
        return new AgentID(buffer.getLong(Offsets.DST_AGENT_ID_OFFSET.getValue()));
    }

    /**
     * Reads the MessageID of a formatted message beginning at a certain offset inside a buffer. 
     * @param byteArray the array in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the MessageID of the formatted message
     */
    public static long readMessageID(byte[] byteArray, int offset) {
        return TypeHelper.byteArrayToLong(byteArray, offset + Offsets.MSG_ID_OFFSET.getValue());
    }

    /**
     * Reads the MessageID of a formatted message beginning at a certain offset inside a buffer. 
     * @param buffer the {@link ByteBuffer} in which to read 
     * @return the MessageID of the formatted message
     */
    public static long readMessageID(ByteBuffer buffer) {
        return buffer.getLong(Offsets.MSG_ID_OFFSET.getValue());
    }

    public AgentID getSrcAgentID() {
        return srcAgentID;
    }

    public AgentID getDstAgentID() {
        return dstAgentID;
    }

    public long getMsgID() {
        return msgID;
    }

    public byte[] getData() {
        return data;
    }
}
