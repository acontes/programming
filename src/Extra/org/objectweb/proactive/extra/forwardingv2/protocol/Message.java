package org.objectweb.proactive.extra.forwardingv2.protocol;

/**
 * An object representing a message 
 *
 * A message MUST have the following structure
 * 
 * MSG = LENGHT | PROTO-ID | MSG_TYPE | SRC_AGENT-ID | SRC_ENDPOINT-ID | DST_AGENT-ID | DST_ENDPOINT-ID | ID | DATA
 * 
 * LENGTH = int
 * PROTO-ID = int
 * MSG_TYPE = int
 * SRC_AGENT-ID = long
 * SRC_ENDPOINT-ID = long
 * DST_AGENT-ID = long
 * DST_ENDPOINT-ID = long
 * ID = long
 * DATA = byte[]
 * 
 */
public class Message {

    // 1 long for the length, 2 int for protoID and MsgType, 2 long for SrcAgent and SrcEndpoint IDs, 2 long for DstAgent and DstEndpoint IDs, 1 long for MSG_ID 
    public static final int HEADER_LENGTH = 3 * 4 + 2 * 8 + 2 * 8 + 8; // = 52

    public static final int LENGTH_OFFSET = 0;
    public static final int PROTO_ID_OFFSET = 4;
    public static final int MSG_TYPE_OFFSET = 8;
    public static final int SRC_AGENT_ID_OFFSET = 12;
    public static final int SRC_ENDPOINT_ID_OFFSET = 20;
    public static final int DST_AGENT_ID_OFFSET = 28;
    public static final int DST_ENDPOINT_ID_OFFSET = 36;
    public static final int MSG_ID_OFFSET = 44;

    public static final int PROTOV1 = 1;

    protected int type;
    protected AgentID srcAgentID, dstAgentID;
    protected EndpointID srcEndpointID, dstEndpointID;
    protected long msgID;
    protected byte[] data;

    Message() {
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    Message(byte[] byteArray, int offset) {
        int datalength = readLength(byteArray, offset) - HEADER_LENGTH;
        data = new byte[datalength];

        type = readType(byteArray, offset);
        srcAgentID = readSrcAgentID(byteArray, offset);
        srcEndpointID = readSrcEndpointID(byteArray, offset);
        dstAgentID = readDstAgentID(byteArray, offset);
        dstEndpointID = readDstEndpointID(byteArray, offset);
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

        TypeHelper.intToByteArray(length, byteArray, LENGTH_OFFSET);
        TypeHelper.intToByteArray(getProtoID(), byteArray, PROTO_ID_OFFSET);
        TypeHelper.intToByteArray(type, byteArray, MSG_TYPE_OFFSET);
        TypeHelper.longToByteArray(srcAgentID.getId(), byteArray, SRC_AGENT_ID_OFFSET);
        TypeHelper.longToByteArray(srcEndpointID.getId(), byteArray, SRC_ENDPOINT_ID_OFFSET);
        TypeHelper.longToByteArray(dstAgentID.getId(), byteArray, DST_AGENT_ID_OFFSET);
        TypeHelper.longToByteArray(dstEndpointID.getId(), byteArray, DST_ENDPOINT_ID_OFFSET);
        TypeHelper.longToByteArray(msgID, byteArray, MSG_ID_OFFSET);

        for (int i = 0; i < data.length; i++) {
            byteArray[HEADER_LENGTH + i] = data[i];
        }

        return byteArray;
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
        return TypeHelper.byteArrayToInt(byteArray, offset + LENGTH_OFFSET);
    }

    /**
     * Reads the type of a formatted message beginning at a certain offset inside a buffer. 
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the type of the formatted message
     */
    public int readType(byte[] byteArray, int offset) {
        return TypeHelper.byteArrayToInt(byteArray, offset + MSG_TYPE_OFFSET);
    }

    //TODO: see if we also do a function readSrc(/Dst)AgentIDAsLong which returns the ID as a long and does not encapsulate it inside an AgentID object
    /**
     * Reads the srcAgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the srcAgentID of the formatted message
     */
    public AgentID readSrcAgentID(byte[] byteArray, int offset) {
        return new AgentID(TypeHelper.byteArrayToLong(byteArray, offset + SRC_AGENT_ID_OFFSET));
    }

    /**
     * Reads the srcEndpointID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an Endpoint object. 
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the srcEndpointID of the formatted message
     */
    public EndpointID readSrcEndpointID(byte[] byteArray, int offset) {
        return new EndpointID(TypeHelper.byteArrayToLong(byteArray, offset + SRC_ENDPOINT_ID_OFFSET));
    }

    /**
     * Reads the dstAgentID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an AgentID object.
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the dstAgentID of the formatted message
     */
    public AgentID readDstAgentID(byte[] byteArray, int offset) {
        return new AgentID(TypeHelper.byteArrayToLong(byteArray, offset + DST_AGENT_ID_OFFSET));
    }

    /**
     * Reads the dstEndpointID of a formatted message beginning at a certain offset inside a buffer. Encapsulates it in an Endpoint object. 
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the dstEndpointID of the formatted message
     */
    public EndpointID readDstEndpointID(byte[] byteArray, int offset) {
        return new EndpointID(TypeHelper.byteArrayToLong(byteArray, offset + DST_ENDPOINT_ID_OFFSET));
    }

    /**
     * Reads the MessageID of a formatted message beginning at a certain offset inside a buffer. 
     * @param byteArray the buffer in which to read 
     * @param offset the offset at which to find the beginning of the message in the buffer
     * @return the MessageID of the formatted message
     */
    public long readMessageID(byte[] byteArray, int offset) {
        return TypeHelper.byteArrayToLong(byteArray, offset + MSG_ID_OFFSET);
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

    public EndpointID getSrcEndpointID() {
        return srcEndpointID;
    }

    public void setSrcEndpointID(EndpointID srcEndpointID) {
        this.srcEndpointID = srcEndpointID;
    }

    public EndpointID getDstEndpointID() {
        return dstEndpointID;
    }

    public void setDstEndpointID(EndpointID dstEndpointID) {
        this.dstEndpointID = dstEndpointID;
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
