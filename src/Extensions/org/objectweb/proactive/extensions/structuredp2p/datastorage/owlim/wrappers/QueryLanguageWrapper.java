package org.objectweb.proactive.extensions.structuredp2p.datastorage.owlim.wrappers;

import java.io.Serializable;

import org.openrdf.query.QueryLanguage;


@SuppressWarnings("serial")
public class QueryLanguageWrapper implements Serializable {
    private QueryLanguage object;

    public QueryLanguageWrapper() {

    }

    public QueryLanguageWrapper(QueryLanguage q) {
        this.object = q;
    }

    public QueryLanguage getValue() {
        return this.object;
    }
}
