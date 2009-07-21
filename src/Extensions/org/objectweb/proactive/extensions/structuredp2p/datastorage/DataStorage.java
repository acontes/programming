package org.objectweb.proactive.extensions.structuredp2p.datastorage;

import java.io.Serializable;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResult;
import org.openrdf.repository.Repository;


/**
 * @author Pellegrino Laurent
 * @version 0.1, 07/21/2009
 */
public interface DataStorage extends Serializable {

    public abstract void add(Statement stmt);

    public abstract void remove(Statement stmt);

    // public abstract CloseableIteration<? extends BindingSet, QueryEvaluationException> query(
    // QueryLanguage language, String query);

    // public abstract Set<Statement> graphQuery(QueryLanguage language, String graphQuery);

    public abstract QueryResult<BindingSet> query(QueryLanguage language, String query);

    public abstract Set<Statement> query(Statement stmt);

    public abstract void startup();

    public abstract void shutdown();

    public abstract void shutdownWithRepositoriesRemoving();

    public abstract void shutdownWithRepositoryRemoving(String name);

    public abstract Repository getRepository();
}
