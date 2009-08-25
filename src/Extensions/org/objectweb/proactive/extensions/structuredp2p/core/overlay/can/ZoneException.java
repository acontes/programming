package org.objectweb.proactive.extensions.structuredp2p.core.overlay.can;

/**
 * An {@code ZoneException} is thrown by certain methods of the {@link Zone} class.
 * 
 * @author Alexandre Trovato
 * @author Fanny Kilanga
 * @author Laurent Pellegrino
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class ZoneException extends Exception {

    /**
     * Constructs an {@code ZoneException} with no detail message.
     */
    public ZoneException() {
        super();
    }

    /**
     * Constructs an {@code ZoneException} with the specified detail message. A detail message is a
     * {@link String} that describes this particular exception.
     * 
     * @param message
     *            the detail message.
     */
    public ZoneException(String message) {
        super(message);
    }

    /**
     * Constructs an {@code ZoneException} with the specified detail message and cause.
     * 
     * @param message
     *            the detail message.
     * @param cause
     *            the cause.
     */
    public ZoneException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an {@code ZoneException} with the specified cause.
     * 
     * @param cause
     *            the cause.
     */
    public ZoneException(Throwable cause) {
        super(cause);
    }

}
