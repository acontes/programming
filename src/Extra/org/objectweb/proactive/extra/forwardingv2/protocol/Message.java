package org.objectweb.proactive.extra.forwardingv2.protocol;

/**
 * An object representing a message 
 *
 * A message MUST have the following structure
 * 
 * MSG = LENGHT | PROTO-ID | MSG_TYPE | SRC_AGENT-ID | DST_AGENT-ID | ID | DATA
 * 
 * LENGTH = int
 * PROTO-ID = int
 * MSG_TYPE = int
 * SRC_AGENT-ID = long
 * DST_AGENT-ID = long
 * ID = long
 * DATA = byte[]
 * 
 */
public class Message {

    // 1 long for the length, 2 int for protoID and MsgType, 2 long for SrcAgent and DstAgent IDs, 1 long for MSG_ID 
    public static final int HEADER_LENGTH = 3 * 4 + 2 * 8 + 8; // = 36

    public static final int PROTOV1 = 1;

    //offsets in the byte array representation message
    public enum Offsets {
        LENGTH_OFFSET(0), PROTO_ID_OFFSET(4), MSG_TYPE_OFFSET(8), SRC_AGENT_ID_OFFSET(12), DST_AGENT_ID_OFFSET(
                20), MSG_ID_OFFSET(28);

        private final int value;

        private Offsets(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

    }

    public enum MessageType {
        REGISTRATION_REQUEST(0), // Registration request to the registry
        REGISTRATION_REPLY(1), // Registration reply from the registry indicating the attributed localid
        CONNECTION_REQUEST(2), // Connection request from a client to a server
        CONNECTION_ACCEPTED(3), // response to a connection request
        CONNECTION_ABORTED(4), // response to a connection request
        AGENT_DISCONNECTED(5), // help closing properly the connections
        DATA(6); // data message

        private final int value;

        private MessageType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    //attributes
    protected int type;
    protected AgentID srcAgentID, dstAgentID;
    protected long msgID;
    protected byte[] data;

    public Message(int type, AgentID srcAgentID, AgentID dstAgentID, long msgID, byte[] data) {
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
    public Message(byte[] byteArray, int offset) {
        int datalength = readLength(byteArray, offset) - HEADER_LENGTH;
        data = new byte[datalength];

        type = readType(byteArray, offset);
        srcAgentID = readSrcAgentID(byteArray, offset);
        dstAgentID = readDstAgentID(byteArray, offset);
        msgID = readMessageID(byteArray, offset);

        for (int i = 0; i < datalength; i++) {
            data[i] = byteArray[HEADER_LENGTH + i];
        }
    }

    /**
     * serializes the Message in a byte array
     * @return the array representing the formatted message
     */
    public byte[] toByteArray() {
        int length = getLength();
        byte[] byteArray = new byte[length];

        TypeHelper.intToByteArray(length, byteArray, Offsets.LENGTH_OFFSET.getValue());
        TypeHelper.intToByteArray(getProtoID(), byteArray, Offsets.PROTO_ID_OFFSET.getValue());
        TypeHelper.intToByteArray(type, byteArray, Offsets.MSG_TYPE_OFFSET.getValue());
        TypeHelper.longToByteArray(srcAgentID.getId(), byteArray, Offsets.SRC_AGENT_ID_OFFSET.getValue());
        TypeHelper.longToByteArray(dstAgentID.getId(), byteArray, Offsets.DST_AGENT_ID_OFFSET.getValue());
        TypeHelper.longToByteArray(msgID, byteArray, Offsets.MSG_ID_OFFSET.getValue());

        for (int i = 0; i < data.length; i++) {
            byteArray[HEADER_LENGTH + i] = data[i];
        }

        return byteArray;
    }


    public static Message registrationRequestMessage() {
        return new Message(MessageType.REGISTRATION_REQUEST.getValue(), null, null, 0, null);
    }

    public static Message registrationReplyMessage(AgentID dstAgentID) {
        return new Message(MessageType.REGISTRATION_REPLY.getValue(), null, dstAgentID, 0, null);
    }

    public static Message connectionRequestMessage(AgentID srcAgentID, AgentID dstAgentID, long msgID) {
        return new Message(MessageType.CONNECTION_REQUEST.getValue(), srcAgentID, dstAgentID, msgID, null);
    }

    public static Message connectionAcceptedMessage(AgentID srcAgentID, AgentID dstAgentID, long msgID) {
        return new Message(MessageType.CONNECTION_ACCEPTED.getValue(), srcAgentID, dstAgentID, msgID, null);
    }

    public static Message connectionAbortedMessage(AgentID srcAgentID, AgentID dstAgentID, long msgID,
            byte[] cause) {
        return new Message(MessageType.CONNECTION_ABORTED.getValue(), srcAgentID, dstAgentID, msgID, cause);
    }

    public static Message dataMessage(AgentID srcAgentID, AgentID dstAgentID, long msgID, byte[] data) {
        return new Message(MessageType.DATA.getValue(), srcAgentID, dstAgentID, msgID, data);
    }
    
    /**
     * @return the total length of the formatted message (header length + data length)
     */
    public int getLength() {
        return HEADER_LENGTH + data.length;
    }

    public int getProtoID() {
        return PROTOV1;
    }

    /**
     * Reads the length of a formatted message beginning at a certain offset inside a buffer. 
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the total length of the formatted message
     */
    public int readLength(byte[] byteArray, int offset) {
        return TypeHelper.byteArrayToInt(byteArray, offset + Offsets.LENGTH_OFFSET.getValue());
    }

    /**
     * Reads the type of a formatted message beginning at a certain offset inside a buffer. 
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the type of the formatted message
     */
    public int readType(byte[] byteArray, int offset) {
        return TypeHelper.byteArrayToInt(byteArray, offset + Offsets.MSG_TYPE_OFFSET.getValue());
    }

    //TODO: see if we also do a function readSrc(or Dst)AgentIDAsLong which returns the ID as a long and does not encapsulate it inside an AgentID object
    /**
     * Reads the srcAgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the srcAgentID of the formatted message
     */
    public AgentID readSrcAgentID(byte[] byteArray, int offset) {
        return new AgentID(TypeHelper.byteArrayToLong(byteArray, offset +
            Offsets.SRC_AGENT_ID_OFFSET.getValue()));
    }

    /**
     * Reads the dstAgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the dstAgentID of the formatted message
     */
    public AgentID readDstAgentID(byte[] byteArray, int offset) {
        return new AgentID(TypeHelper.byteArrayToLong(byteArray, offset +
            Offsets.DST_AGENT_ID_OFFSET.getValue()));
    }

    /**
     * Reads the MessageID of a formatted message beginning at a certain offset inside a buffer. 
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the MessageID of the formatted message
     */
    public long readMessageID(byte[] byteArray, int offset) {
        return TypeHelper.byteArrayToLong(byteArray, offset + Offsets.MSG_ID_OFFSET.getValue());
    }

    //traditional getters and setters
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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