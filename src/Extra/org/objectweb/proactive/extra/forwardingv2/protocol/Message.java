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

    Message(byte[] byteArray) {
        int datalength = TypeHelper.byteArrayToInt(byteArray, LENGTH_OFFSET) - Message.HEADER_LENGTH;
        data = new byte[datalength];

        type = TypeHelper.byteArrayToInt(byteArray, MSG_TYPE_OFFSET);
        srcAgentID = new AgentID(TypeHelper.byteArrayToLong(byteArray, SRC_AGENT_ID_OFFSET));
        srcEndpointID = new EndpointID(TypeHelper.byteArrayToLong(byteArray, SRC_ENDPOINT_ID_OFFSET));
        dstAgentID = new AgentID(TypeHelper.byteArrayToLong(byteArray, DST_AGENT_ID_OFFSET));
        dstEndpointID = new EndpointID(TypeHelper.byteArrayToLong(byteArray, DST_ENDPOINT_ID_OFFSET));
        msgID = TypeHelper.byteArrayToLong(byteArray, MSG_ID_OFFSET);

        for (int i = 0; i < datalength; i++) {
            data[i] = byteArray[Message.HEADER_LENGTH + i];
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
            byteArray[Message.HEADER_LENGTH + i] = data[i];
        }

        return byteArray;
    }

    /**
     * @return the total length of the formatted message
     */
    public int getLength() {
        return HEADER_LENGTH + data.length;
    }

    public int getProtoID() {
        return PROTOV1;
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
