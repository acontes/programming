package org.objectweb.proactive.extra.forwardingv2.protocol;

public class RegistrationReply extends RegistrationMessage {

    public RegistrationReply(AgentID agentID) {
        super(MessageType.REGISTRATION_REPLY, agentID);
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    public RegistrationReply(byte[] byteArray, int offset) {
        super(byteArray, offset);
    }
}
