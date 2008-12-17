package org.objectweb.proactive.extra.forwardingv2.protocol;

public class RegistrationRequest extends RegistrationMessage {


	public RegistrationRequest() {
		super(MessageType.REGISTRATION_REQUEST, null);
	}

    public RegistrationRequest(AgentID agentID) {
        super(MessageType.REGISTRATION_REQUEST, agentID);
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
