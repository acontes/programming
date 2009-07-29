package org.objectweb.proactive.extensions.structuredp2p.datastorage;

import java.io.Serializable;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResult;
import org.openrdf.repository.Repository;


/**
 * @author Pellegrino Laurent
 * @version 0.1.1, 07/29/2009
 */
public interface DataStorage extends Serializable {

    /**
     * Add a {@link Statement} to the current repository.
     * 
     * @param stmt
     *            the statement to add.
     */
    public abstract void add(Statement stmt);

    /**
     * Remove the specified {@link Statement} from the current repository.
     * 
     * @param stmt
     *            the statement to remove.
     */
    public abstract void remove(Statement stmt);

    // public abstract CloseableIteration<? extends BindingSet, QueryEvaluationException> query(
    // QueryLanguage language, String query);

    /**
     * Evaluates a query from the specified language and returns the results found.
     * 
     * @param language
     *            the language used for the query.
     * @param query
     *            the query to evaluate.
     * @return the results found.
     */
    public abstract QueryResult<BindingSet> query(QueryLanguage language, String query);

    /**
     * Evaluates a query from the specified language and returns the results found.
     * 
     * @param language
     *            the language used for the query.
     * @param query
     *            the query to evaluate (for example a ASK query in SPARQL).
     * @return the results found.
     */
    public abstract boolean queryB(QueryLanguage language, String query);

    /**
     * Evaluates a query from the specified language and returns the results found.
     * 
     * @param language
     *            the language used for the query.
     * @param query
     *            the query to evaluate.
     * @return the results found (a graph).
     */
    public abstract Set<Statement> queryV(QueryLanguage language, String graphQuery);

    /**
     * Debrief the datastore in order to found the statements that seems to the specified
     * {@link Statement}.
     * 
     * @param stmt
     *            the statement used. This can contains null {@link URI} in order to indicate a
     *            wildcard.
     * 
     * @return the results found.
     */
    public abstract Set<Statement> query(Statement stmt);

    /**
     * Initialize the OWLIM store.
     */
    public abstract void startup();

    /**
     * Shutdown the repository.
     */
    public abstract void shutdown();

    /**
     * Shutdown the repository by removing the current repository used by this datastore.
     */
    public abstract void shutdownByRemovingCurrentRepository();

    /**
     * Shutdown the repository by removing all the repositories that are on the computer performing
     * this operation.
     */
    public abstract void shutdownWithRepositoriesRemoving();

    /**
     * Shutdown the repository by removing the specified repository.
     * 
     * @param repositoryName
     *            the name of the repository to remove.
     */
    public abstract void shutdownWithRepositoryRemoving(String name);

    /**
     * Returns the repository instance.
     * 
     * @return the repository instance.
     */
    public abstract Repository getRepository();

    /**
     * Indicates if the datastore contains statements.
     * 
     * @return <code>true</code> if the datastore contains statements. <code>false</code> otherwise.
     */
    public abstract boolean hasStatements();

}
