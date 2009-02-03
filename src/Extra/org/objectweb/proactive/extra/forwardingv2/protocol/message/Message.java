package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.TypeHelper;


public abstract class Message {
    static final private Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_MESSAGE);

    public static final int PROTOV1 = 1;

    // enumerations
    public enum MessageType {
        REGISTRATION_REQUEST, // Registration request to the registry
        REGISTRATION_REPLY, // Registration reply from the registry indicating the attributed localid
        DATA_REQUEST, // Request from a client to a server
        DATA_REPLY, // Reply from a server to a client
        ERR_, DEBUG_,
        //        ERR_DISCONNECTED_RCPT, // Signals that the RCPT disconnected from the router
        //        ERR_UNKNOW_RCPT, // Signals that the router does not known the RCPT
        //        ERR_INVALID_AGENT_ID // a client advertised an unknow agent id on reconnection
        ;

        final static Map<Integer, MessageType> idToMessageType;
        static {
            // Can't populate idToMessageType from constructor since enums are initialized before 
            // any static initializers are run. It is safe to do it from this static block
            idToMessageType = new HashMap<Integer, MessageType>();
            for (MessageType messageType : values()) {
                idToMessageType.put(messageType.ordinal(), messageType);
            }
        }

        public static MessageType getMessageType(int value) {
            return idToMessageType.get(value);
        }
    }

    public enum Field {
        LENGTH(4, Integer.class), PROTO_ID(4, Integer.class), MSG_TYPE(4, Integer.class), MSG_ID(8,
                Long.class);

        private int length;
        private Class<?> type;

        private Field(int length, Class<?> type) {
            this.length = length;
            this.type = type;
        }

        public int getLength() {
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

    // methods
    public static Message constructMessage(byte[] byteArray, int offset) {
        // depending on the type of message, call a different constructor
        MessageType type = MessageType.getMessageType(TypeHelper.byteArrayToInt(byteArray, offset +
            Field.MSG_TYPE.getOffset()));
        try {
            switch (type) {
                case REGISTRATION_REQUEST:
                    return new RegistrationRequestMessage(byteArray, offset);
                case REGISTRATION_REPLY:
                    return new RegistrationReplyMessage(byteArray, offset);
                case DATA_REQUEST:
                    return new DataRequestMessage(byteArray, offset);
                case DATA_REPLY:
                    return new DataReplyMessage(byteArray, offset);
                case ERR_:
                    return new ErrorMessage(byteArray, offset);
                case DEBUG_:
                    return new DebugMessage(byteArray, offset);
                default:
                    logger.error("Construct message failed: unknown message type " + type);
                    return null;
            }
        } catch (InstantiationException e) {
            logger.error("Construct message failed: wrong message type", e);
            return null;
        }
    }

    /**
     * Reads the length of a formatted message beginning at a certain offset inside a buffer. 
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the total length of the formatted message
     */
    public static int readLength(byte[] byteArray, int offset) {
        return TypeHelper.byteArrayToInt(byteArray, offset + Field.LENGTH.getOffset());
    }

    public static int readProtoID(byte[] byteArray, int offset) {
        return TypeHelper.byteArrayToInt(byteArray, offset + Field.PROTO_ID.getOffset());
    }

    public static long readMessageID(byte[] byteArray, int offset) {
        return TypeHelper.byteArrayToLong(byteArray, offset + Field.MSG_ID.getOffset());
    }

    public static int readLength(ByteBuffer buffer) {
        return buffer.getInt(Field.LENGTH.getOffset());
    }

    /**
     * Reads the type of a formatted message beginning at a certain offset inside a buffer. 
     * @param byteArray the array in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the type of the formatted message
     */
    public static MessageType readType(byte[] byteArray, int offset) {
        return MessageType.getMessageType(TypeHelper.byteArrayToInt(byteArray, offset +
            Field.MSG_TYPE.getOffset()));
    }

    /**
     * Reads the type of a formatted message beginning at a certain offset inside a buffer. 
     * @param buffer the {@link ByteBuffer} in which to read 
     * @return the type of the formatted message
     */
    public static MessageType readType(ByteBuffer buffer) {
        return MessageType.getMessageType(buffer.getInt(Field.MSG_TYPE.getOffset()));
    }

    // attributes
    private int length;
    final private int protoId;
    final private MessageType type;
    final private long messageId;

    public Message(MessageType type, long messageId) {
        this.type = type;
        this.protoId = PROTOV1;
        this.messageId = messageId;
    }

    public Message(byte[] buf, int offset) {
        this.length = readLength(buf, offset);
        this.protoId = readProtoID(buf, offset);
        this.type = readType(buf, offset);
        this.messageId = readMessageID(buf, offset);

        // Should probably throw an exception...
        if (this.protoId != PROTOV1) {
            logger.fatal("Invalid protocol id. Expected: " + PROTOV1 + " Received: " + this.protoId);
        }
    }

    public int getLength() {
        return length;
    }

    protected void setLength(int length) {
        this.length = length;
    }

    public MessageType getType() {
        return this.type;
    }

    public long getMessageID() {
        return this.messageId;
    }

    public int getProtoID() {
        return this.protoId;
    }

    public abstract byte[] toByteArray();

    protected void writeHeader(byte[] buf, int offset) {
        TypeHelper.intToByteArray(this.length, buf, offset + Field.LENGTH.getOffset());
        TypeHelper.intToByteArray(this.protoId, buf, offset + Field.PROTO_ID.getOffset());
        TypeHelper.intToByteArray(this.type.ordinal(), buf, offset + Field.MSG_TYPE.getOffset());
        TypeHelper.longToByteArray(this.messageId, buf, offset + Field.MSG_ID.getOffset());
    }

    @Override
    public String toString() {
        return "length=" + this.length + " protoId=" + this.protoId + " type=" + this.type + " msgId=" +
            messageId + " ";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + length;
        result = prime * result + (int) (messageId ^ (messageId >>> 32));
        result = prime * result + protoId;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Message other = (Message) obj;
        if (length != other.length)
            return false;
        if (messageId != other.messageId)
            return false;
        if (protoId != other.protoId)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}