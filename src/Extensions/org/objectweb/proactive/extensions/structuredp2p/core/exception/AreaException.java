package org.objectweb.proactive.extensions.structuredp2p.core.exception;

/**
 * An {@code AreaException} is thrown by certain methods of the {@link Area} class.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class AreaException extends Exception {

    /**
     * Constructs an {@code AreaException} with no detail message.
     */
    public AreaException() {
        super();
    }

    /**
     * Constructs an {@code AreaException} with the specified detail message. A detail message is a
     * {@link String} that describes this particular exception.
     * 
     * @param message
     *            the detail message.
     */
    public AreaException(String message) {
        super(message);
    }

    /**
     * Constructs an {@code AreaException} with the specified cause.
     * 
     * @param cause
     *            the cause.
     */
    public AreaException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an {@code AreaException} with the specified detail message and cause.
     * 
     * @param message
     *            the detail message.
     * @param cause
     *            the cause.
     */
    public AreaException(String message, Throwable cause) {
        super(message, cause);
    }

}
