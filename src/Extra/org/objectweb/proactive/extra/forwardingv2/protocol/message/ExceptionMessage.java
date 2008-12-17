package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import org.objectweb.proactive.core.remoteobject.http.util.HttpMarshaller;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;


public class ExceptionMessage extends ForwardedMessage {

    public ExceptionMessage(MessageType type, AgentID srcAgentID, AgentID dstAgentID, long msgID, Exception e) {
        super(type, srcAgentID, dstAgentID, msgID, null);
        byte[] data = HttpMarshaller.marshallObject(e);
        setData(data);
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     * @param byteArray the byte array from which to read
     * @param offset the offset at which to find the message in the byte array
     */
    public ExceptionMessage(byte[] byteArray, int offset) {
        super(byteArray, offset);
    }

    public Exception getException() {
        Exception e = null;
        if (data != null) {
            e = (Exception) HttpMarshaller.unmarshallObject(data);
        }
        return e;
    }

}
