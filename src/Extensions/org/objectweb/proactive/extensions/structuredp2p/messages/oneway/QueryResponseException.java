package org.objectweb.proactive.extensions.structuredp2p.messages.oneway;

@SuppressWarnings("serial")
public class QueryResponseException extends QueryResponse {
    String message;

    public QueryResponseException() {
        super();
    }

    public QueryResponseException(String mess) {
        this.message = mess;
    }

    public String getMessage() {
        return this.message;
    }

}
