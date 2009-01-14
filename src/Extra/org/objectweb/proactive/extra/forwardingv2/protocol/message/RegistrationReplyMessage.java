package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public class RegistrationReplyMessage extends RegistrationMessage {

    public RegistrationReplyMessage(AgentID agentID) {
        super(MessageType.REGISTRATION_REPLY, agentID);
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    public RegistrationReplyMessage(byte[] byteArray, int offset) {
        super(byteArray, offset);
    }
}