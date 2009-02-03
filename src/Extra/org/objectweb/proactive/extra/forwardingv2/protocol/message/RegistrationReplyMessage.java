package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public class RegistrationReplyMessage extends RegistrationMessage {

    public RegistrationReplyMessage(AgentID agentID, long messageId) {
        super(MessageType.REGISTRATION_REPLY, messageId, agentID);
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    public RegistrationReplyMessage(byte[] byteArray, int offset) throws InstantiationException {
        super(byteArray, offset);

        if (this.getType() != MessageType.REGISTRATION_REPLY) {
            throw new InstantiationException("Invalid message type " + this.getType());
        }
    }
}
