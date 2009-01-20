package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import java.nio.ByteBuffer;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.TypeHelper;


public abstract class RegistrationMessage extends Message {

    public static final int REGISTRATION_MESSAGE_LENGTH = GLOBAL_COMMON_OFFSET + 8;

    public enum Offsets {
        AGENT_ID_OFFSET(0);

        private final int value;

        private Offsets(int value) {
            this.value = GLOBAL_COMMON_OFFSET + value;
        }

        public int getValue() {
            return this.value;
        }
    }

    // attribute
    protected AgentID agentID;

    public RegistrationMessage(MessageType type, AgentID agentID) {
        this.type = type;
        this.agentID = agentID;
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    public RegistrationMessage(byte[] byteArray, int offset) {
        type = readType(byteArray, offset);
        agentID = readAgentID(byteArray, offset);
    }

    public AgentID getAgentID() {
        return this.agentID;
    }

    @Override
    public byte[] toByteArray() {
        int length = getLength();
        byte[] byteArray = new byte[length];

        TypeHelper.intToByteArray(length, byteArray, CommonOffsets.LENGTH_OFFSET.getValue());
        TypeHelper.intToByteArray(getProtoID(), byteArray, CommonOffsets.PROTO_ID_OFFSET.getValue());
        TypeHelper.intToByteArray(type.ordinal(), byteArray, CommonOffsets.MSG_TYPE_OFFSET.getValue());
        if (agentID != null) {
            TypeHelper.longToByteArray(agentID.getId(), byteArray, Offsets.AGENT_ID_OFFSET.getValue());
        }
        return byteArray;
    }

    @Override
    public ByteBuffer toByteBuffer() {
        int length = getLength();
        ByteBuffer buffer = ByteBuffer.allocate(length);

        buffer.putInt(length).putInt(getProtoID()).putInt(type.ordinal());

        if (agentID != null) {
            buffer.putLong(agentID.getId());
        }
        return buffer;
    }

    //TODO: set it as abstract in Message.java ?
    /**
     * @return the total length of the formatted message (header length + data length)
     */
    public int getLength() {
        return REGISTRATION_MESSAGE_LENGTH;
    }

    /**
     * Reads the AgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the AgentID of the formatted message
     */
    public static AgentID readAgentID(byte[] byteArray, int offset) {
        long id = TypeHelper.byteArrayToLong(byteArray, offset + Offsets.AGENT_ID_OFFSET.getValue());
        return (id != 0) ? new AgentID(id) : null;
    }

    /**
     * Reads the AgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param buffer the buffer in which to read 
     * @return the AgentID of the formatted message
     */
    public static AgentID readAgentID(ByteBuffer buffer) {
        long id = buffer.getLong(Offsets.AGENT_ID_OFFSET.getValue());
        return (id != 0) ? new AgentID(id) : null;
    }
}
