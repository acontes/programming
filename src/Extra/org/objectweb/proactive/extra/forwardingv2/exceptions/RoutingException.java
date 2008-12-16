package org.objectweb.proactive.extra.forwardingv2.exceptions;

/** Signals that the Message Router is unable to route the message to the destination agent
 *
 * @since ProActive 4.1.0
 */
@SuppressWarnings("serial")
public class RoutingException extends MessageRoutingException {

	public RoutingException() {
		super();
	}

	public RoutingException(String message, Throwable cause) {
		super(message, cause);
	}

	public RoutingException(String message) {
		super(message);
	}

	public RoutingException(Throwable cause) {
		super(cause);
	}
}
