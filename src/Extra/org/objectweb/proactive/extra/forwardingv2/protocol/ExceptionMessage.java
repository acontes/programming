package org.objectweb.proactive.extra.forwardingv2.protocol;

import org.objectweb.proactive.extra.forwardingv2.exceptions.MessageRoutingException;

public class ExceptionMessage extends ForwardedMessage {

	// There are two type of exception, so it remains a parameter to be given to the constructor
    public ExceptionMessage(MessageType type, AgentID srcAgentID, AgentID dstAgentID, long msgID, MessageRoutingException e) {
        super(type, srcAgentID, dstAgentID, msgID, null);
        //TODO: serialize the exception
        //TODO: set the data of the message with the serialized exception
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    public ExceptionMessage(byte[] byteArray, int offset) {
        super(byteArray, offset);
    }

}
