package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public class DataRequestMessage extends ForwardedMessage {

    public DataRequestMessage(AgentID srcAgentID, AgentID dstAgentID, long msgID, byte[] data) {
        super(MessageType.DATA_REQUEST, srcAgentID, dstAgentID, msgID, data);
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     * @throws InstantiationException
     */
    public DataRequestMessage(byte[] byteArray, int offset) throws InstantiationException {
        super(byteArray, offset);

        if (this.getType() != MessageType.DATA_REQUEST) {
            throw new InstantiationException("Invalid message type " + this.getType());
        }
    }
}
