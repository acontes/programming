package org.objectweb.proactive.extra.forwardingv2.protocol;

/**
 * An object representing a message 
 *
 * A message MUST have the following structure
 * 
 * MSG = LENGHT PROTO-ID  MSG_TYPE SRC_AGENT-ID SRC_ENDPOINT-ID DST_AGENT-ID DST_ENDPOINT-ID ID DATA
 * 
 * LENGTH = long
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
	public static final int HEADER_LENGTH = 8 + 2 * 4 + 2 * 8 + 2 * 8 + 8;
	
	public static final int PROTOV1 = 1;
	
    protected int type;
    protected AgentID srcAgentID, dstAgentID;
    protected EndpointID srcEndpointID, dstEndpointID;
    protected long ID;
    protected byte[] data;

    Message() {}

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

	public long getID() {
		return ID;
	}

	public void setID(long id) {
		ID = id;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
    
}
