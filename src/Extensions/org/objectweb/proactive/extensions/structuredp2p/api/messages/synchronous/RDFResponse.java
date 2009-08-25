package org.objectweb.proactive.extensions.structuredp2p.api.messages.synchronous;

import java.util.Set;

import org.objectweb.proactive.extensions.structuredp2p.responses.synchronous.can.RDFResponseMessage;
import org.openrdf.model.Statement;


/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/25/2009
 */
@SuppressWarnings("serial")
public class RDFResponse implements Response {

    private RDFResponseMessage response;

    public RDFResponse(RDFResponseMessage msg) {
        this.response = msg;
    }

    public Set<Statement> getRetrievedStatements() {
        return this.response.getRetrievedStatements();
    }

}
