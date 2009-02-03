package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.TypeHelper;


public abstract class RegistrationMessage extends Message {

    public enum Field {
        AGENT_ID(8, Long.class);

        private int length;
        private Class<?> type;

        private Field(int length, Class<?> type) {
            this.length = length;
            this.type = type;
        }

        public long getLength() {
            return this.length;
        }

        public int getOffset() {
            int offset = 0;
            // No way to avoid this iteration over ALL the field
            // There is no such method than Field.getOrdinal(x)
            for (Field field : values()) {
                if (field.ordinal() < this.ordinal()) {
                    offset += field.getLength();
                }
            }
            return offset;
        }

        public String getType() {
            return this.type.toString();
        }

        static public int getTotalOffset() {
            // OPTIM: Can be optimized with caching if needed
            int totalOffset = 0;
            for (Field field : values()) {
                totalOffset += field.getLength();
            }
            return totalOffset;
        }
    }

    // attribute
    final private AgentID agentID;

    public RegistrationMessage(MessageType type, long messageId, AgentID agentID) {
        super(type, messageId);

        this.agentID = agentID;
        super.setLength(this.getLength());
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    public RegistrationMessage(byte[] byteArray, int offset) {
        super(byteArray, offset);

        this.agentID = readAgentID(byteArray, offset);
    }

    public AgentID getAgentID() {
        return this.agentID;
    }

    @Override
    public byte[] toByteArray() {
        int length = getLength();
        byte[] buff = new byte[length];

        super.writeHeader(buff, 0);

        long id = -1;
        if (this.agentID != null) {
            id = this.agentID.getId();
        }
        TypeHelper.longToByteArray(id, buff, Message.Field.getTotalOffset() + Field.AGENT_ID.getOffset());
        return buff;
    }

    //TODO: set it as abstract in Message.java ?
    /**
     * @return the total length of the formatted message (header length + data length)
     */
    public int getLength() {
        return Message.Field.getTotalOffset() + Field.getTotalOffset();
    }

    /**
     * Reads the AgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the AgentID of the formatted message
     */
    public AgentID readAgentID(byte[] byteArray, int offset) {
        long id = TypeHelper.byteArrayToLong(byteArray, offset + Message.Field.getTotalOffset() +
            Field.AGENT_ID.getOffset());
        return (id >= 0) ? new AgentID(id) : null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((agentID == null) ? 0 : agentID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        RegistrationMessage other = (RegistrationMessage) obj;
        if (agentID == null) {
            if (other.agentID != null)
                return false;
        } else if (!agentID.equals(other.agentID))
            return false;
        return true;
    }

}
