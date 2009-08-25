package org.objectweb.proactive.extensions.structuredp2p.messages.synchronous;

import java.io.Serializable;


/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/14/2009
 */
@SuppressWarnings("serial")
public class SynchronousMessageEntry implements Serializable {

    private SynchronousMessage response;

    private int nbResponses = 0;

    public SynchronousMessageEntry() {

    }

    public SynchronousMessageEntry(SynchronousMessage response) {
        this(response, 1);
    }

    public SynchronousMessageEntry(SynchronousMessage response, int nbResponses) {
        this.response = response;
        this.nbResponses = nbResponses;
    }

    public int getNbResponses() {
        return this.nbResponses;
    }

    public SynchronousMessage getSynchronousMessage() {
        return this.response;
    }

    public void incrementNbResponses(int increment) {
        this.nbResponses += increment;
    }

    public void setSynchronousMessage(SynchronousMessage response) {
        this.response = response;
    }

}
