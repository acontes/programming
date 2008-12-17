package org.objectweb.proactive.extra.forwardingv2.exceptions;

/** Signals that an error of some sort has occurred.
 *
 * This class is the general class of exceptions produced by failed message sending.
 */
@SuppressWarnings("serial")
public class MessageRoutingException extends Exception {

    public MessageRoutingException() {
        super();
    }

    public MessageRoutingException(String message) {
        super(message);
    }

    public MessageRoutingException(Throwable cause) {
        super(cause);
    }

    public MessageRoutingException(String message, Throwable cause) {
        super(message, cause);
    }

}