package org.objectweb.proactive.extra.vfsprovider.exceptions;

public class StreamNotFoundException extends Exception {

    public StreamNotFoundException() {
        super("Stream with specified id is unknown or aleady closed");
    }

    /**
     * 
     */
    private static final long serialVersionUID = 3393596002049816170L;

}
