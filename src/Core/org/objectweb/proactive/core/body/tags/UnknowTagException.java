package org.objectweb.proactive.core.body.tags;

import org.objectweb.proactive.core.ProActiveException;

/**
 * Throwed exception when trying to get an unkwon tag
 */
public class UnknowTagException extends ProActiveException {

    public UnknowTagException() {
        super();
    }

    public UnknowTagException(String s) {
        super(s);
    }

    public UnknowTagException(String s, Throwable t) {
        super(s, t);
    }

    public UnknowTagException(Throwable t) {
        super(t);
    }
    
}
