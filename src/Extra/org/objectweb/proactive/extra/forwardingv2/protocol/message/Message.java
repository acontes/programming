package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import java.util.Arrays;

import org.objectweb.proactive.extra.forwardingv2.protocol.TypeHelper;


public abstract class Message {
    public static final int PROTOV1 = 1;
    public static final int GLOBAL_COMMON_OFFSET = 12; // 3*4 for length, protoID and type

    // enumerations
    public enum MessageType {
        REGISTRATION_REQUEST(0), // Registration request to the registry
        REGISTRATION_REPLY(1), // Registration reply from the registry indicating the attributed localid
        DATA_REQUEST(2), // Request from a client to a server
        DATA_REPLY(3), // Reply from a server to a client
        ROUTING_EXCEPTION_MSG(4), // Message signaling a routing error
        EXECUTION_EXCEPTION_MSG(5); // Message signaling an exception error

        private final int value;

        private MessageType(int value) {
            this.value = value;
        }

        public static MessageType getMessageType(int value) {
            switch (value) {
                case 0:
                    return REGISTRATION_REQUEST;
                case 1:
                    return REGISTRATION_REPLY;
                case 2:
                    return DATA_REQUEST;
                case 3:
                    return DATA_REPLY;
                case 4:
                    return ROUTING_EXCEPTION_MSG;
                case 5:
                    return EXECUTION_EXCEPTION_MSG;
                default: //should not occur but TODO: check that this is ok
                    return null;
            }
        }

        public int getValue() {
            return this.value;
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
        // TODO depending on the type of message, call a different constructor
        // exemple : new RegistrationRequestMessage(data, offset);
        // exemple : new RegistrationReplyMessage(data, offset);
        // exemple : new RoutingExceptionMessage(data, offset);
        // exemple : new ExecutionExceptionMessage(data, offset);
        // exemple : new DataRequestMessage(data, offset);
        // exemple : new DataReplyMessage(data, offset);
        return null;
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
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the type of the formatted message
     */
    public static MessageType readType(byte[] byteArray, int offset) {
        return MessageType.getMessageType(TypeHelper.byteArrayToInt(byteArray, offset +
            CommonOffsets.MSG_TYPE_OFFSET.getValue()));
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