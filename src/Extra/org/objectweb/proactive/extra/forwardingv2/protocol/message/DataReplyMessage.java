package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public class DataReplyMessage extends ForwardedMessage {

    public DataReplyMessage(AgentID srcAgentID, AgentID dstAgentID, long msgID, byte[] data) {
        super(MessageType.DATA_REPLY, srcAgentID, dstAgentID, msgID, data);
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     * @throws InstantiationException
     */
    public DataReplyMessage(byte[] byteArray, int offset) throws InstantiationException {
        super(byteArray, offset);

        if (this.getType() != MessageType.DATA_REPLY) {
            throw new InstantiationException("Invalid message type " + this.getType());
        }
    }

}
