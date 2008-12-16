package org.objectweb.proactive.extra.forwardingv2.exceptions;

/**
 * Signals that user asked the agent to route some message but the agent is not
 * connected to the Message Router
 *
 * This exception can occurs if the agent is not yet connected (startup) or the
 * connection has been broken and not yet reestablished.
 *
 * @since ProActive 4.1.0
 */
@SuppressWarnings("serial")
public class AgentNotConnectedException extends RoutingException {

	public AgentNotConnectedException() {
		super();
	}

	public AgentNotConnectedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AgentNotConnectedException(String message) {
		super(message);
	}

	public AgentNotConnectedException(Throwable cause) {
		super(cause);
	}

}
