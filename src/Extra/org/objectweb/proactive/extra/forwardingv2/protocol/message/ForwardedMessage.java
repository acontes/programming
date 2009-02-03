package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import java.util.Arrays;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.TypeHelper;


public abstract class ForwardedMessage extends Message {

    public static final int FORWARDED_MESSAGE_HEADER_LENGTH = Message.Field.getTotalOffset() +
        Field.getTotalOffset(); // = 36

    public enum Field {
        SRC_AGENT_ID(8, Long.class), DST_AGENT_ID(8, Integer.class);

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

    //attributes
    final protected AgentID srcAgentID, dstAgentID;
    final protected byte[] data; // Since data is not used by the router should be lazily created to avoid data duplication
    final protected byte[] toByteArray;

    public ForwardedMessage(MessageType type, AgentID srcAgentID, AgentID dstAgentID, long msgID, byte[] data) {
        super(type, msgID);
        this.srcAgentID = srcAgentID;
        this.dstAgentID = dstAgentID;
        this.data = data;
        this.toByteArray = null;
        super.setLength(this.getLength());
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    public ForwardedMessage(byte[] byteArray, int offset) {
        super(byteArray, offset);

        this.srcAgentID = readSrcAgentID(byteArray, offset);
        this.dstAgentID = readDstAgentID(byteArray, offset);

        int datalength = readLength(byteArray, offset) - FORWARDED_MESSAGE_HEADER_LENGTH;
        this.data = new byte[datalength];
        System.arraycopy(byteArray, offset + FORWARDED_MESSAGE_HEADER_LENGTH, this.data, 0, datalength);

        this.toByteArray = byteArray;
    }

    public String toString() {
        return super.toString() + " src=" + this.srcAgentID + " dst=" + this.dstAgentID;
    }

    @Override
    public byte[] toByteArray() {
        int length = getLength();
        byte[] buf = new byte[length];

        super.writeHeader(buf, 0);

        long srcId = -1;
        if (srcAgentID != null) {
            srcId = srcAgentID.getId();
        }
        TypeHelper.longToByteArray(srcId, buf, Message.Field.getTotalOffset() +
            Field.SRC_AGENT_ID.getOffset());

        long dstId = -1;
        if (dstAgentID != null) {
            dstId = dstAgentID.getId();
        }
        TypeHelper.longToByteArray(dstId, buf, Message.Field.getTotalOffset() +
            Field.DST_AGENT_ID.getOffset());

        if (data != null) {
            System.arraycopy(data, 0, buf, FORWARDED_MESSAGE_HEADER_LENGTH, data.length);
        }
        return buf;
    }

    /**
     * @return the total length of the formatted message (header length + data length)
     */
    public int getLength() {
        return FORWARDED_MESSAGE_HEADER_LENGTH + (data != null ? data.length : 0);
    }

    public AgentID getSrcAgentID() {
        return srcAgentID;
    }

    public AgentID getDstAgentID() {
        return dstAgentID;
    }

    public byte[] getData() {
        return data;
    }

    /**
     * Reads the srcAgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the srcAgentID of the formatted message
     */
    static public AgentID readSrcAgentID(byte[] byteArray, int offset) {
        long id = TypeHelper.byteArrayToLong(byteArray, offset + Message.Field.getTotalOffset() +
            Field.SRC_AGENT_ID.getOffset());

        return id < 0 ? null : new AgentID(id);
    }

    /**
     * Reads the dstAgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param byteArray the array in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the dstAgentID of the formatted message
     */
    public static AgentID readDstAgentID(byte[] byteArray, int offset) {
        long id = TypeHelper.byteArrayToLong(byteArray, offset + Message.Field.getTotalOffset() +
            Field.DST_AGENT_ID.getOffset());

        return id < 0 ? null : new AgentID(id);
    }

    /**
     * Reads the dstAgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param byteArray the array in which to read 
     * @return the dstAgentID of the formatted message
     */
    public static AgentID readDstAgentID(byte[] byteArray) {
        return readDstAgentID(byteArray, 0);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode(data);
        result = prime * result + ((dstAgentID == null) ? 0 : dstAgentID.hashCode());
        result = prime * result + ((srcAgentID == null) ? 0 : srcAgentID.hashCode());
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
        ForwardedMessage other = (ForwardedMessage) obj;
        if (!Arrays.equals(data, other.data))
            return false;
        if (dstAgentID == null) {
            if (other.dstAgentID != null)
                return false;
        } else if (!dstAgentID.equals(other.dstAgentID))
            return false;
        if (srcAgentID == null) {
            if (other.srcAgentID != null)
                return false;
        } else if (!srcAgentID.equals(other.srcAgentID))
            return false;
        return true;
    }
}
