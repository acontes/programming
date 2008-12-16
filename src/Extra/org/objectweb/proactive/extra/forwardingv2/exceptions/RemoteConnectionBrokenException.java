package org.objectweb.proactive.extra.forwardingv2.exceptions;

/** Signals that the destination agent is not connected to the Message Router
 *
 * @since Proactive 4.1.0
 *
 */
@SuppressWarnings("serial")
public class RemoteConnectionBrokenException extends RoutingException {

    public RemoteConnectionBrokenException() {
        super();
    }

    public RemoteConnectionBrokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteConnectionBrokenException(String message) {
        super(message);
    }

    public RemoteConnectionBrokenException(Throwable cause) {
        super(cause);
    }

}
