package org.objectweb.proactive.extra.forwardingv2.protocol;

public class DataReplyMessage extends ForwardedMessage {

    public DataReplyMessage(MessageType type, AgentID srcAgentID, AgentID dstAgentID, long msgID, byte[] data) {
        super(type, srcAgentID, dstAgentID, msgID, data);
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    public DataReplyMessage(byte[] byteArray, int offset) {
        super(byteArray, offset);
    }

}
