package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.proactive.extra.forwardingv2.protocol.TypeHelper;


public abstract class Message {
    public static final int PROTOV1 = 1;
    public static final int GLOBAL_COMMON_OFFSET = 12; // 3*4 for length, protoID and type

    // enumerations
    public enum MessageType {
        REGISTRATION_REQUEST, // Registration request to the registry
        REGISTRATION_REPLY, // Registration reply from the registry indicating the attributed localid
        DATA_REQUEST, // Request from a client to a server
        DATA_REPLY, // Reply from a server to a client
        ERR_DISCONNECTED_RCPT, // Signals that the RCPT disconnected from the router
        ERR_UNKNOW_RCPT // Signals that the router does not known the RCPT
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

    public enum CommonOffsets {
        LENGTH_OFFSET(0), PROTO_ID_OFFSET(4), MSG_TYPE_OFFSET(8);

        private final int value;

        private CommonOffsets(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    // attributes
    protected MessageType type;

    // methods
    public static Message constructMessage(byte[] byteArray, int offset) {
        // depending on the type of message, call a different constructor
        MessageType type = MessageType.getMessageType(TypeHelper.byteArrayToInt(byteArray, offset +
            CommonOffsets.MSG_TYPE_OFFSET.getValue()));
        switch (type) {
            case REGISTRATION_REQUEST:
                return new RegistrationRequestMessage(byteArray, offset);
            case REGISTRATION_REPLY:
                return new RegistrationReplyMessage(byteArray, offset);
            case DATA_REQUEST:
                return new DataRequestMessage(byteArray, offset);
            case DATA_REPLY:
                return new DataReplyMessage(byteArray, offset);
            case ERR_DISCONNECTED_RCPT:
            case ERR_UNKNOW_RCPT:
                return new ErrorMessage(byteArray, offset);
            default:
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
        return TypeHelper.byteArrayToInt(byteArray, offset + CommonOffsets.LENGTH_OFFSET.getValue());
    }

    /**
     * Reads the type of a formatted message beginning at a certain offset inside a buffer. 
     * @param byteArray the array in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the type of the formatted message
     */
    public static MessageType readType(byte[] byteArray, int offset) {
        return MessageType.getMessageType(TypeHelper.byteArrayToInt(byteArray, offset +
            CommonOffsets.MSG_TYPE_OFFSET.getValue()));
    }

    /**
     * Reads the type of a formatted message beginning at a certain offset inside a buffer. 
     * @param buffer the {@link ByteBuffer} in which to read 
     * @return the type of the formatted message
     */
    public static MessageType readType(ByteBuffer buffer) {
        return MessageType.getMessageType(buffer.getInt(CommonOffsets.MSG_TYPE_OFFSET.getValue()));
    }

    /**
     * @return the type of the message
     */
    public MessageType getType() {
        return type;
    }

    public int getProtoID() {
        return PROTOV1;
    }

    public abstract byte[] toByteArray();

    public abstract ByteBuffer toByteBuffer();

    public abstract int getLength();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Message) {
            Message m = (Message) obj;
            return Arrays.equals(this.toByteArray(), m.toByteArray());
        }
        return false;
    }
}