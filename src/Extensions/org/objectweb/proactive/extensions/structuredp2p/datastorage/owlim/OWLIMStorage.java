package org.objectweb.proactive.extensions.structuredp2p.datastorage.owlim;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.proactive.extensions.structuredp2p.datastorage.DataStorage;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResult;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.impl.MutableTupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;


/**
 * OWLIM data access implementation.
 * 
 * @author Pellegrino Laurent
 * 
 * @version 0.1, 07/21/2009
 */
@SuppressWarnings("serial")
public class OWLIMStorage extends DataStorage {

    private Repository repository;

    public static final String ROOT_PATH = System.getProperty("user.home");

    public static final String REPOSITORIES_PATH = OWLIMStorage.ROOT_PATH + "/BigOWLIM/repositories";

    /**
     * Constructor.
     */
    public OWLIMStorage() {
        this.startup();
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        File repositoriesPath = new File(OWLIMStorage.REPOSITORIES_PATH);

        if (!repositoriesPath.exists()) {
            repositoriesPath.mkdirs();
        }

        File currentObjectRepository = new File(OWLIMStorage.REPOSITORIES_PATH, "" + this.hashCode());
        currentObjectRepository.mkdir();

        this.repository = new SailRepository(new NativeStore(currentObjectRepository, "spoc,posc"));

        try {
            this.repository.initialize();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void add(Statement stmt) {
        RepositoryConnection conn = null;
        try {
            conn = this.repository.getConnection();
            conn.add(stmt);
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove(Statement stmt) {
        RepositoryConnection conn = null;
        try {
            conn = this.repository.getConnection();
            conn.remove(stmt);
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public QueryResult<BindingSet> query(QueryLanguage language, String query) {
        RepositoryConnection conn = null;
        QueryResult<BindingSet> queryResults = null;

        try {
            conn = this.repository.getConnection();

            try {
                TupleQuery tupleQuery = conn.prepareTupleQuery(language, query);
                queryResults = new MutableTupleQueryResult(tupleQuery.evaluate());
            } finally {
                conn.close();
            }
        } catch (OpenRDFException e) {
            e.printStackTrace();
        }

        return queryResults;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Statement> query(Statement stmt) {
        RepositoryConnection conn = null;
        Set<Statement> results = new HashSet<Statement>();

        try {
            conn = this.repository.getConnection();
            RepositoryResult<Statement> queryResults = conn.getStatements(stmt.getSubject(), stmt
                    .getPredicate(), stmt.getPredicate(), true);

            while (queryResults.hasNext()) {
                results.add(queryResults.next());
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    /**
     * {@inheritDoc}
     */
    public boolean queryB(QueryLanguage language, String query) {
        RepositoryConnection conn = null;
        boolean result = false;

        try {
            conn = this.repository.getConnection();

            try {
                result = conn.prepareBooleanQuery(language, query).evaluate();
            } finally {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Boolean(result);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Statement> queryV(QueryLanguage language, String graphQuery) {
        RepositoryConnection conn = null;
        Set<Statement> results = new HashSet<Statement>();

        try {
            conn = this.repository.getConnection();
            try {
                GraphQueryResult graphResult = conn.prepareGraphQuery(language, graphQuery).evaluate();

                while (graphResult.hasNext()) {
                    results.add(graphResult.next());
                }
            } catch (QueryEvaluationException e) {
                e.printStackTrace();
            }

        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasStatements() {
        return this.queryB(QueryLanguage.SPARQL, "ASK { ?s ?p ?o }");
    }

    /**
     * Recursively delete a given directory.
     * 
     * @param path
     *            the path to the directory to remove.
     * @return <code>true</code> is the delete has succeeded, <code>false</code> otherwise.
     */
    private boolean deleteDirectory(File path) {
        boolean resultat = true;

        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    resultat &= this.deleteDirectory(file);
                } else {
                    resultat &= file.delete();
                }
            }
        }
        resultat &= path.delete();
        return (resultat);
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        try {
            if (this.repository.getConnection().isOpen()) {
                this.repository.getConnection().close();
            }

            this.repository.shutDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void shutdownByRemovingCurrentRepository() {
        this.shutdownWithRepositoryRemoving("" + this.hashCode());
    }

    /**
     * {@inheritDoc}
     */
    public void shutdownWithRepositoriesRemoving() {
        this.shutdown();
        this.deleteDirectory(new File(OWLIMStorage.REPOSITORIES_PATH));
    }

    /**
     * {@inheritDoc}
     */
    public void shutdownWithRepositoryRemoving(String repositoryName) {
        this.shutdown();
        this.deleteDirectory(new File(OWLIMStorage.REPOSITORIES_PATH + "/" + repositoryName));
    }

    /**
     * {@inheritDoc}
     */
    public Repository getRepository() {
        return this.repository;
    }
}