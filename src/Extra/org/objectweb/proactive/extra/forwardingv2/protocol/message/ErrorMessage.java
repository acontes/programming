package org.objectweb.proactive.extra.forwardingv2.protocol.message;

import org.objectweb.proactive.core.remoteobject.http.util.HttpMarshaller;
import org.objectweb.proactive.extra.forwardingv2.protocol.AgentID;
import org.objectweb.proactive.extra.forwardingv2.protocol.TypeHelper;


public class ErrorMessage extends ForwardedMessage {

    public enum ErrorType {
        // Signals that a client disconnected from the router
        // This is a broadcast message, all clients are notified of this disconnection
        ERR_DISCONNECTED_RCPT_BROADCAST,
        // Used when a data request is received and the recipient is not connected
        // to the router
        ERR_NOT_CONNECTED_RCPT, ERR_UNKNOW_RCPT, // Signals that the router does not known the RCPT
        ERR_INVALID_AGENT_ID // a client advertised an unknow agent id on
        // reconnection
        ;

        public byte[] toByteArray() {
            byte[] buf = new byte[4];
            TypeHelper.intToByteArray(this.ordinal(), buf, 0);
            return buf;
        }
    }

    final private ErrorType error;

    public ErrorMessage(AgentID dstAgentID, long msgID, ErrorType error) {
        this(dstAgentID, null, msgID, error);
    }

    /**
     *
     * @param dstAgentID The recipient of this error message
     * @param srcAgentID The client which caused the error message (can be null)
     * @param msgID The message id which caused the error message (-1 if this error is not related to a specific message)
     * @param error The error
     */
    public ErrorMessage(AgentID dstAgentID, AgentID srcAgentID, long msgID, ErrorType error) {
        super(MessageType.ERR_, srcAgentID, dstAgentID, msgID, HttpMarshaller.marshallObject(error));
        this.error = error;
    }

    /**
     * Construct a message from the data contained in a formatted byte array.
     *
     * @param byteArray
     *            the byte array from which to read
     * @param offset
     *            the offset at which to find the message in the byte array
     * @throws InstantiationException
     */
    public ErrorMessage(byte[] byteArray, int offset) throws InstantiationException {
        super(byteArray, offset);

        if (this.getType() != MessageType.ERR_) {
            throw new InstantiationException("Invalid message type " + this.getType());
        }

        try {
            this.error = (ErrorType) HttpMarshaller.unmarshallObject(this.getData());
        } catch (ClassCastException e) {
            throw new InstantiationError("Invalid error type:" + e);
        }
    }

    public ErrorType getErrorType() {
        return this.error;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((error == null) ? 0 : error.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ErrorMessage other = (ErrorMessage) obj;
        if (error == null) {
            if (other.error != null)
                return false;
        } else if (!error.equals(other.error))
            return false;
        return true;
    }

}
