package org.objectweb.proactive.extra.forwardingv2.protocol;

public class RegistrationRequestMessage extends RegistrationMessage {

    //TODO: ajouter ce qu'il faut pour pouvoir fabriquer des requests sans agentID

    public RegistrationRequest(MessageType type, AgentID agentID) {
        super(type, agentID);
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    public RegistrationRequest(byte[] byteArray, int offset) {
        super(byteArray, offset);
    }

}
