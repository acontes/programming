package org.objectweb.proactive.extensions.structuredp2p.messages.synchronous;

import java.io.Serializable;


/**
 * @author Laurent Pellegrino
 * @version 0.1, Aug 14, 2009
 */
@SuppressWarnings("serial")
public class QueryResponseEntry implements Serializable {

    private int nbResponses = 0;
    private QueryResponse response;

    public QueryResponseEntry() {

    }

    public QueryResponseEntry(QueryResponse response) {
        this(response, 1);
    }

    public QueryResponseEntry(QueryResponse response, int nbResponses) {
        this.response = response;
        this.nbResponses = nbResponses;
    }

    public int getNbResponses() {
        return this.nbResponses;
    }

    public QueryResponse getQueryResponse() {
        return this.response;
    }

    public void incrementNbResponses(int increment) {
        this.nbResponses += increment;
    }

    public void setQueryResponse(QueryResponse response) {
        this.response = response;
    }
}
