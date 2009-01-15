package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import org.objectweb.proactive.core.remoteobject.http.util.HttpMarshaller;
import org.objectweb.proactive.extra.forwardingv2.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public class ErrorMessage extends ForwardedMessage {

    public ErrorMessage(MessageType type, AgentID srcAgentID, AgentID dstAgentID, long msgID, Exception e) {
        super(type, srcAgentID, dstAgentID, msgID, HttpMarshaller.marshallObject(e));
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    public ErrorMessage(byte[] byteArray, int offset) {
        super(byteArray, offset);
    }

    public MessageRoutingException getException() {
        MessageRoutingException e = null;
        if (data != null) {
            e = (MessageRoutingException) HttpMarshaller.unmarshallObject(data);
        }
        return e;
    }

}
