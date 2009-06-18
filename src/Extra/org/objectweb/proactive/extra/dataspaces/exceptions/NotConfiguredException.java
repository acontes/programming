/**
 *
 */
package org.objectweb.proactive.extra.dataspaces.exceptions;

// TODO: make it extend ConfigurationException?
/**
 *
 *
 */
public class NotConfiguredException extends DataSpacesException {

    /**
     *
     */
    public NotConfiguredException() {
    }

    /**
     * @param message
     */
    public NotConfiguredException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public NotConfiguredException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public NotConfiguredException(Throwable cause) {
        super(cause);
    }

}
