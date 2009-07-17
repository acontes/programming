package org.objectweb.proactive.extensions.structuredp2p.datastorage.owlim;

import info.aduna.iteration.CloseableIteration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.objectweb.proactive.extensions.structuredp2p.datastorage.DataStorage;
import org.openrdf.model.Statement;
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
import org.openrdf.repository.sail.SailGraphQuery;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.repository.sail.SailTupleQuery;
import org.openrdf.sail.SailException;
import org.openrdf.sail.nativerdf.NativeStore;


/**
 * OWLIM data access implementation.
 * 
 * @author Pellegrino Laurent
 * 
 * @version 0.1, 07/15/2009
 */
@SuppressWarnings("serial")
public class OWLIMStorage implements DataStorage {

    private Repository repository;

    private Properties properties;

    public static String OWLIM_PROPERTIES_FILE = "owlim.properties";

    /**
     * Constructor.
     */
    public OWLIMStorage() {
        this.startup();
    }

    /**
     * 
     * @return
     */
    private Properties loadProperties() {
        File propertiesFile = new File("owlim/" + OWLIMStorage.OWLIM_PROPERTIES_FILE);

        if (propertiesFile.exists()) {
            this.properties = new Properties();
            try {
                this.properties.load(new FileInputStream(propertiesFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalStateException("The properties file '" + "owlim/" +
                OWLIMStorage.OWLIM_PROPERTIES_FILE + "' doesn't exist.");
        }

        return this.properties;
    }

    /**
     * Initialize the OWLIM store.
     */
    public void startup() {
        /* Loads properties */
        Properties properties = this.loadProperties();

        File repositoriesPath = new File(properties.getProperty("repositories-path"));

        if (!repositoriesPath.exists()) {
            repositoriesPath.mkdirs();
        }

        File currentObjectRepository = new File(properties.getProperty("repositories-path"), "" +
            this.hashCode());
        currentObjectRepository.mkdir();

        this.repository = new SailRepository(new NativeStore(currentObjectRepository));

        try {
            this.repository.initialize();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        /* Initialize the repository */
        /*
         * this.repositoryManager = new LocalRepositoryManager(new File(OwlimStorage.OWLIM_PATH));
         * 
         * try { this.repositoryManager.initialize(); } catch (RepositoryException e) {
         * e.printStackTrace(); }
         * 
         * final Graph graph = new GraphImpl(); RDFParser parser =
         * Rio.createParser(RDFFormat.TURTLE); RDFHandler handler = new RDFHandler() { public void
         * endRDF() throws RDFHandlerException { }
         * 
         * public void handleComment(String arg0) throws RDFHandlerException { }
         * 
         * public void handleNamespace(String arg0, String arg1) throws RDFHandlerException { }
         * 
         * public void handleStatement(Statement stmt) throws RDFHandlerException { graph.add(stmt);
         * }
         * 
         * public void startRDF() throws RDFHandlerException { }
         * 
         * }; parser.setRDFHandler(handler); try { parser.parse(new
         * FileReader(OwlimStorage.OWLIM_PATH + "/" + properties.getProperty("config",
         * OwlimStorage.DEFAULT_CONFIG)), "http://example.org#"); } catch (RDFParseException e) {
         * e.printStackTrace(); } catch (RDFHandlerException e) { e.printStackTrace(); } catch
         * (FileNotFoundException e) { e.printStackTrace(); } catch (IOException e) {
         * e.printStackTrace(); }
         * 
         * Iterator<Statement> iterator = graph.match(null, RDF.TYPE, new URIImpl(
         * "http://www.openrdf.org/config/repository#Repository")); Resource repNode = null; if
         * (iterator.hasNext()) { Statement st = iterator.next(); repNode = st.getSubject(); }
         * 
         * try { this.repositoryConfig = RepositoryConfig.create(graph, repNode);
         * this.repositoryManager.addRepositoryConfig(this.repositoryConfig); this.repository =
         * this.repositoryManager.getRepository(properties.getProperty("repository",
         * OwlimStorage.DEFAULT_REPOSITORY)); } catch (RepositoryConfigException e) {
         * e.printStackTrace(); } catch (RepositoryException e) { e.printStackTrace(); }
         */
    }

    /**
     * Shutdown the repository.
     */
    public void shutdown() {
        try {
            if (this.repository.getConnection().isOpen()) {
                this.repository.getConnection().close();
            }

            this.repository.shutDown();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

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
     * 
     */
    public void shutdownWithRepositoriesRemoving() {
        this.deleteDirectory(new File(this.properties.getProperty("repositories-path")));
        this.shutdown();
    }

    /**
     * 
     * @param repositoryName
     *            the name of the repository to remove.
     */
    public void shutdownWithRepositoryRemoving(String repositoryName) {
        this
                .deleteDirectory(new File(this.properties.getProperty("repositories-path") + "/" +
                    repositoryName));
        this.shutdown();
    }

    /**
     * {@inheritDoc}
     */
    public void add(Statement stmt) {
        try {
            RepositoryConnection con = this.repository.getConnection();
            con.add(stmt);
            con.close();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public CloseableIteration<? extends BindingSet, QueryEvaluationException> query(QueryLanguage language,
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
        CloseableIteration<? extends BindingSet, QueryEvaluationException> results = null;
        try {
            results = ((SailRepositoryConnection) conn).getSailConnection().evaluate(pq.getTupleExpr(),
                    pq.getDataset(), q.getBindings(), true);
        } catch (SailException e) {
            e.printStackTrace();
        } finally {
            /*
             * try { conn.close(); } catch (RepositoryException e) { e.printStackTrace(); }
             */
        }

        return results;

        //

        /*
         * Set<Statement> results = new HashSet<Statement>();
         * 
         * try { conn = this.repository.getConnection(); try { GraphQueryResult graphResult =
         * conn.prepareGraphQuery(language, query).evaluate(); while (graphResult.hasNext()) {
         * results.add(graphResult.next()); } } catch (QueryEvaluationException e) {
         * e.printStackTrace(); }
         * 
         * } catch (RepositoryException e) { e.printStackTrace(); } catch (MalformedQueryException
         * e) { e.printStackTrace(); } finally { try { conn.close(); } catch (RepositoryException e)
         * { e.printStackTrace(); } }
         * 
         * return results;
         */
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

    public Repository getRepository() {
        return this.repository;
    }
}