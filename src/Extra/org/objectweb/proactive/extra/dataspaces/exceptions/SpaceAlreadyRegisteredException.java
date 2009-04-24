/**
 *
 */
package org.objectweb.proactive.extra.dataspaces.exceptions;

/**
 *
 *
 */
public class SpaceAlreadyRegisteredException extends ConfigurationException {

    /**
     *
     */
    public SpaceAlreadyRegisteredException() {
    }

    /**
     * @param message
     */
    public SpaceAlreadyRegisteredException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public SpaceAlreadyRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public SpaceAlreadyRegisteredException(Throwable cause) {
        super(cause);
    }

}
