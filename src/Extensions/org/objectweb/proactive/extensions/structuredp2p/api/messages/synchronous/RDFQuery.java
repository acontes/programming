package org.objectweb.proactive.extensions.structuredp2p.api.messages.synchronous;

/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/25/2009
 */
@SuppressWarnings("serial")
public class RDFQuery implements Query {

    String SPARQLQuery;

    public RDFQuery(String SPARQLQuery) {

    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "RDFQuery";
    }
}
