package org.objectweb.proactive.extensions.structuredp2p.data;

import info.aduna.iteration.CloseableIteration;

import java.io.Serializable;

import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryResult;


/**
 * @author Pellegrino Laurent
 * @version 0.1, 07/09/2009
 */
public interface DataStorage extends Serializable {
    public abstract void add(Statement stmt);

    public abstract void remove(Statement stmt);

    public abstract CloseableIteration<BindingSet, QueryEvaluationException> evaluateQuery(
            QueryLanguage language, String query);

    public abstract RepositoryResult<Statement> evaluateQuery(Statement stmt);

    public abstract void shutdown();
}
