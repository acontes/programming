package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import java.util.Arrays;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.TypeHelper;


/** A data message
 * 
 * @since ProActive 4.1.0
 */
public abstract class DataMessage extends Message {
    /** The offset of the payload */
    private static final int DATA_MESSAGE_HEADER_LENGTH = Message.Field.getTotalOffset() +
        Field.getTotalOffset();

    private static final long UNKNOWN_AGENT_ID = -1;

    /**
     * Fields of the {@link DataMessage} header.
     * 
     * These fields are put after the {@link Message} header.
     */
    public enum Field {
        /** The {@link AgentID} of the sender of this message */
        SRC_AGENT_ID(8, Long.class),
        /** The {@link AgentID} of the recipient of this message */
        DST_AGENT_ID(8, Long.class);

        private int length;

        /* type is only informative */
        private Field(int length, Class<?> type) {
            this.length = length;
        }

        /** Length of the field in bytes */
        public long getLength() {
            return this.length;
        }

        /** Offset of the field in bytes */
        public int getOffset() {
            int offset = 0;
            /*
             * TODO OPTIM: Cache the response. This function is called at least
             * 3 times for each message received on by the router. Could be a
             * bottleneck.
             */

            // No way to avoid this iteration over ALL the field
            // There is no such method than Field.getOrdinal(x)
            for (Field field : values()) {
                if (field.ordinal() < this.ordinal()) {
                    offset += field.getLength();
                }
            }
            return offset;
        }

        /** Length of the fields defined by {@link DataMessage} */
        static public int getTotalOffset() {
            /* TODO OPTIM: Cache the response. */
            int totalOffset = 0;
            for (Field field : values()) {
                totalOffset += field.getLength();
            }
            return totalOffset;
        }
    }

    /* @@@@@@@@@@@@@@@@@@@@ Static methods @@@@@@@@@@@@@@@@@@@@@@ */

    /** Reads the sender of a message
     * 
     * @param buf
     *            a buffer which contains a message
     * @param offset
     *            the offset at which the message begins
     * @return The value of the length field of the message contained in buf at
     *         the given offset or null if unknown
     */
    static public AgentID readSender(byte[] byteArray, int offset) {
        long id = TypeHelper.byteArrayToLong(byteArray, offset + Message.Field.getTotalOffset() +
            Field.SRC_AGENT_ID.getOffset());

        return id < 0 ? null : new AgentID(id);
    }

    /** Reads the recipient of a message
     * 
     * @param buf
     *            a buffer which contains a message
     * @param offset
     *            the offset at which the message begins
     * @return The value of the length field of the message contained in buf at
     *         the given offset or null if unknown
     */

    public static AgentID readRecipient(byte[] byteArray, int offset) {
        long id = TypeHelper.byteArrayToLong(byteArray, offset + Message.Field.getTotalOffset() +
            Field.DST_AGENT_ID.getOffset());

        return id < 0 ? null : new AgentID(id);
    }

    /** Sender of this message */
    final protected AgentID sender;
    /** Recipient of this message */
    final protected AgentID recipient;
    /** Payload of this message */
    /*
     * Since data is not used by the router could be lazily created to avoid
     * data duplication
     */
    final protected byte[] data;
    /**
     * This message as a byte array
     * 
     * Cached for the sake of speed. But memory can become a bottleneck before
     * CPU or network. Remove this field if router or agent consumes too much
     * memory.
     */
    protected byte[] toByteArray;

    /**
     * Create a {@link DataMessage}
     * 
     * All the parameters must be non null
     * 
     * @param type
     *            Type of the message. Only {@link MessageType#DATA_REQUEST} and
     *            {@link MessageType#DATA_REPLY} are valid types.
     * 
     * @param src
     *            Sender
     * @param dst
     *            Recipient
     * @param msgID
     *            Message ID
     * @param data
     *            Payload
     */
    protected DataMessage(MessageType type, AgentID src, AgentID dst, long msgID, byte[] data) {
        super(type, msgID);
        this.sender = src;
        this.recipient = dst;
        this.data = data;
        this.toByteArray = null;
        super.setLength(this.getLength());
    }

    /**
     * Create a {@link DataMessage} from a byte array
     * 
     * @param buf
     *            a buffer which contains a message
     * @param offset
     *            the offset at which the message begins
     * @throws IllegalArgumentException
     *             If the buffer does not match message requirements (proto ID,
     *             length etc.)
     */
    protected DataMessage(byte[] byteArray, int offset) throws IllegalArgumentException {
        super(byteArray, offset);

        try {
            this.sender = readSender(byteArray, offset);
            this.recipient = readRecipient(byteArray, offset);

            int datalength = super.getLength() - DATA_MESSAGE_HEADER_LENGTH;
            this.data = new byte[datalength];
            System.arraycopy(byteArray, offset + DATA_MESSAGE_HEADER_LENGTH, this.data, 0, datalength);

            this.toByteArray = byteArray;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Message length is invalid: " + super.getLength(), e);
        }

        if (this.sender == null) {
            throw new IllegalArgumentException("SRC_AGENT_ID field must be set");
        }

        if (this.recipient == null) {
            throw new IllegalArgumentException("DST_AGENT_ID field must be set");
        }
    }

    public String toString() {
        return super.toString() + " src=" + this.sender + " dst=" + this.recipient;
    }

    @Override
    public byte[] toByteArray() {
        if (this.toByteArray != null) {
            return this.toByteArray;
        }

        int length = getLength();
        byte[] buf = new byte[length];

        super.writeHeader(buf, 0);

        long srcId = UNKNOWN_AGENT_ID;
        if (sender != null) {
            srcId = sender.getId();
        }
        TypeHelper.longToByteArray(srcId, buf, Message.Field.getTotalOffset() +
            Field.SRC_AGENT_ID.getOffset());

        long dstId = UNKNOWN_AGENT_ID;
        if (recipient != null) {
            dstId = recipient.getId();
        }
        TypeHelper.longToByteArray(dstId, buf, Message.Field.getTotalOffset() +
            Field.DST_AGENT_ID.getOffset());

        if (data != null) {
            System.arraycopy(data, 0, buf, DATA_MESSAGE_HEADER_LENGTH, data.length);
        }

        this.toByteArray = buf;

        return buf;
    }

    /** Return the sender of this message */
    public AgentID getSender() {
        return sender;
    }

    /** Return the recipient of this message */
    public AgentID getRecipient() {
        return recipient;
    }

    /** Return the payload of this message */
    public byte[] getData() {
        return data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode(data);
        result = prime * result + ((recipient == null) ? 0 : recipient.hashCode());
        result = prime * result + ((sender == null) ? 0 : sender.hashCode());
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
        DataMessage other = (DataMessage) obj;
        if (!Arrays.equals(data, other.data))
            return false;
        if (recipient == null) {
            if (other.recipient != null)
                return false;
        } else if (!recipient.equals(other.recipient))
            return false;
        if (sender == null) {
            if (other.sender != null)
                return false;
        } else if (!sender.equals(other.sender))
            return false;
        return true;
    }
}
