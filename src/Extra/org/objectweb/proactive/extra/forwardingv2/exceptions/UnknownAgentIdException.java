package org.objectweb.proactive.extra.forwardingv2.exceptions;

@SuppressWarnings("serial")
public class UnknownAgentIdException extends RoutingException {

	public UnknownAgentIdException() {
		super();
	}

	public UnknownAgentIdException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownAgentIdException(String message) {
		super(message);
	}

	public UnknownAgentIdException(Throwable cause) {
		super(cause);
	}

}
