package org.objectweb.proactive.extensions.webservices.exceptions;

import org.objectweb.proactive.core.ProActiveException;


public class WebServicesException extends ProActiveException {

    public WebServicesException() {
        super();
    }

    public WebServicesException(String message) {
        super(message);
    }

    public WebServicesException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebServicesException(Throwable cause) {
        super(cause);
    }
}
