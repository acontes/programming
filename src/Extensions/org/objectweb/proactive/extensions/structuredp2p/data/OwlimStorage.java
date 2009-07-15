package org.objectweb.proactive.extensions.structuredp2p.data;

import info.aduna.iteration.CloseableIteration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.repository.sail.SailGraphQuery;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.repository.sail.SailTupleQuery;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.sail.SailException;

import com.ontotext.trree.SailConnectionImpl;


/**
 * OWLIM data access implementation.
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/15/2009
 */
@SuppressWarnings("serial")
public class OwlimStorage implements DataStorage {
    private Repository repository;
    private RepositoryConfig repositoryConfig;
    private RepositoryManager repositoryManager;

    public static String DEFAULT_CONFIG = "./owlim.ttl";
    public static String DEFAULT_REPOSITORY = "owlim";

    public static String OWLIM_PATH = "owlim";
    public static String OWLIM_PROPERTIES_FILE = "owlim.properties";

    /**
     * Constructor.
     */
    public OwlimStorage() {
        this.startup();
    }

    /**
     * Shutdown the repository.
     */
    public void shutdown() {
        synchronized (this.repositoryManager) {
            try {
                if (this.repository.getConnection() != null) {
                    this.repository.getConnection().commit();
                    this.repository.getConnection().close();
                }

                this.repositoryManager.shutDown();
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }

    private Properties loadProperties() {
        File propertiesFile = new File(OwlimStorage.OWLIM_PATH + "/" + OwlimStorage.OWLIM_PROPERTIES_FILE);
        Properties properties = null;

        if (propertiesFile.exists()) {
            properties = new Properties();
            try {
                properties.load(new FileInputStream(propertiesFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalStateException("The properties file '" + OwlimStorage.OWLIM_PATH + "/" +
                OwlimStorage.OWLIM_PROPERTIES_FILE + "' doesn't exist.");
        }

        return properties;
    }

    /**
     * Initialize the OWLIM store.
     */
    public void startup() {
        /* Load properties */
        Properties properties = this.loadProperties();

        /* Initialize the repository */
        this.repositoryManager = new LocalRepositoryManager(new File(OwlimStorage.OWLIM_PATH));

        try {
            this.repositoryManager.initialize();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        final Graph graph = new GraphImpl();
        RDFParser parser = Rio.createParser(RDFFormat.TURTLE);
        RDFHandler handler = new RDFHandler() {
            public void endRDF() throws RDFHandlerException {
            }

            public void handleComment(String arg0) throws RDFHandlerException {
            }

            public void handleNamespace(String arg0, String arg1) throws RDFHandlerException {
            }

            public void handleStatement(Statement stmt) throws RDFHandlerException {
                graph.add(stmt);
            }

            public void startRDF() throws RDFHandlerException {
            }

        };
        parser.setRDFHandler(handler);
        try {
            parser.parse(new FileReader(OwlimStorage.OWLIM_PATH + "/" +
                properties.getProperty("config", OwlimStorage.DEFAULT_CONFIG)), "http://example.org#");
        } catch (RDFParseException e) {
            e.printStackTrace();
        } catch (RDFHandlerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<Statement> iterator = graph.match(null, RDF.TYPE, new URIImpl(
            "http://www.openrdf.org/config/repository#Repository"));
        Resource repNode = null;
        if (iterator.hasNext()) {
            Statement st = iterator.next();
            repNode = st.getSubject();
        }

        try {
            this.repositoryConfig = RepositoryConfig.create(graph, repNode);
            this.repositoryManager.addRepositoryConfig(this.repositoryConfig);
            this.repository = this.repositoryManager.getRepository(properties.getProperty("repository",
                    OwlimStorage.DEFAULT_REPOSITORY));
        } catch (RepositoryConfigException e) {
            e.printStackTrace();
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
    public CloseableIteration<BindingSet, QueryEvaluationException> evaluateQuery(QueryLanguage language,
            String query) {
        Query q = null;
        RepositoryConnection conn = null;

        try {
            conn = this.repository.getConnection();
            q = conn.prepareQuery(language, query);
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        }

        ParsedQuery pq = (q instanceof SailGraphQuery ? (SailGraphQuery) q : (SailTupleQuery) q)
                .getParsedQuery();
        CloseableIteration<BindingSet, QueryEvaluationException> results = null;
        try {
            results = ((SailConnectionImpl) ((SailRepositoryConnection) conn).getSailConnection()).evaluate(
                    pq.getTupleExpr(), pq.getDataset(), q.getBindings(), true);
        } catch (SailException e) {
            e.printStackTrace();
        }

        return results;
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
    public RepositoryResult<Statement> evaluateQuery(Statement stmt) {
        RepositoryConnection conn = null;
        RepositoryResult<Statement> results = null;

        try {
            conn = this.repository.getConnection();
            results = conn.getStatements(stmt.getSubject(), stmt.getPredicate(), stmt.getPredicate(), true);
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
}