package org.objectweb.proactive.extra.forwardingv2.protocol;

public class DataReply extends ForwardedMessage {

    public DataReply(MessageType type, AgentID srcAgentID, AgentID dstAgentID, long msgID, byte[] data) {
        super(type, srcAgentID, dstAgentID, msgID, data);
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    public DataReply(byte[] byteArray, int offset) {
        super(byteArray, offset);
    }

}
