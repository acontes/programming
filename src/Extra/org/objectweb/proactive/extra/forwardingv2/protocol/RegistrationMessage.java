package org.objectweb.proactive.extra.forwardingv2.protocol;

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
        int length = REGISTRATION_MESSAGE_LENGTH;
        byte[] byteArray = new byte[length];

        TypeHelper.intToByteArray(length, byteArray, CommonOffsets.LENGTH_OFFSET.getValue());
        TypeHelper.intToByteArray(getProtoID(), byteArray, CommonOffsets.PROTO_ID_OFFSET.getValue());
        TypeHelper.intToByteArray(type.getValue(), byteArray, CommonOffsets.MSG_TYPE_OFFSET.getValue());
        if (agentID != null) {
            TypeHelper.longToByteArray(agentID.getId(), byteArray, Offsets.AGENT_ID_OFFSET.getValue());
        }
        return byteArray;
    }

    //TODO: set it as abstract in Message.kava ?
    /**
     * @return the total length of the formatted message (header length + data length)
     */
    public int getLength() {
        return REGISTRATION_MESSAGE_LENGTH;
    }

    /**
     * Reads the srcAgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the srcAgentID of the formatted message
     */
    public static AgentID readAgentID(byte[] byteArray, int offset) {
        return new AgentID(TypeHelper.byteArrayToLong(byteArray, offset + Offsets.AGENT_ID_OFFSET.getValue()));
    }

}