package org.objectweb.proactive.extra.forwardingv2.exceptions;

@SuppressWarnings("serial")
public class ExecutionException extends MessageRoutingException {

    public ExecutionException() {
        super();
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(Throwable cause) {
        super(cause);
    }

}
