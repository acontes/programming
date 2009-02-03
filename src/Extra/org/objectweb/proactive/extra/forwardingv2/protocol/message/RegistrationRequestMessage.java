package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public class RegistrationRequestMessage extends RegistrationMessage {

    public RegistrationRequestMessage(AgentID agentID, long messageId) {
        super(MessageType.REGISTRATION_REQUEST, messageId, agentID);
    }

    public RegistrationRequestMessage(long messageId) {
        this(null, messageId);
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     * @throws InstantiationException
     */
    public RegistrationRequestMessage(byte[] byteArray, int offset) throws InstantiationException {
        super(byteArray, offset);

        if (this.getType() != MessageType.REGISTRATION_REQUEST) {
            throw new InstantiationException("Invalid message type " + this.getType());
        }
    }

}
