package functionalTests.structuredp2p;

import java.util.Set;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.extensions.structuredp2p.datastorage.DataStorage;
import org.objectweb.proactive.extensions.structuredp2p.datastorage.owlim.OWLIMStorage;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResult;


/**
 * Test the OwlimStorage wrappers.
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/21/2009
 */
public class TestOWLIMStorage {
    private static DataStorage owlimStorage;
    private static ValueFactory owlimValueFactory;

    private static URI subject;
    private static URI predicate;
    private static URI object;

    @BeforeClass
    public static void setUpBeforeClass() {
        TestOWLIMStorage.owlimStorage = new OWLIMStorage();
        TestOWLIMStorage.owlimValueFactory = TestOWLIMStorage.owlimStorage.getRepository().getValueFactory();

        TestOWLIMStorage.subject = TestOWLIMStorage.owlimValueFactory
                .createURI("http://example.org/ontology#psychoquack");
        TestOWLIMStorage.predicate = TestOWLIMStorage.owlimValueFactory
                .createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        TestOWLIMStorage.object = TestOWLIMStorage.owlimValueFactory
                .createURI("http://example.org/ontology#animal");
    }

    @Test
    public void testAdd() {
        TestOWLIMStorage.owlimStorage.add(new StatementImpl(TestOWLIMStorage.subject,
            TestOWLIMStorage.predicate, TestOWLIMStorage.object));

        Set<Statement> results = null;
        results = TestOWLIMStorage.owlimStorage
                .query(new StatementImpl(TestOWLIMStorage.subject, null, null));

        Statement[] resultsAsArray = results.toArray(new Statement[] {});

        Assert.assertEquals(1, results.size());
        Assert.assertEquals(TestOWLIMStorage.subject, resultsAsArray[0].getSubject());
        Assert.assertEquals(TestOWLIMStorage.predicate, resultsAsArray[0].getPredicate());
    }

    @Test
    public void testSPARQL() {
        StringBuffer query = new StringBuffer();
        query.append("PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
        query.append("SELECT ?o ?p ?s WHERE {\n");
        query.append("  ?o ?p ?s .\n");
        query.append("  FILTER ( str(?o) < \"http://example.org/ontology#q\" ).\n");
        query.append("}");

        QueryResult<BindingSet> queryResults = TestOWLIMStorage.owlimStorage.query(QueryLanguage.SPARQL,
                query.toString());

        try {
            Assert.assertTrue(queryResults.hasNext());

            BindingSet bindingSet = queryResults.next();

            Assert.assertEquals("http://example.org/ontology#psychoquack", bindingSet.getValue("o")
                    .toString());
            Assert.assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", bindingSet.getValue("p")
                    .toString());
            Assert.assertEquals("http://example.org/ontology#animal", bindingSet.getValue("s").toString());

            Assert.assertFalse(queryResults.hasNext());
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        } finally {
            try {
                queryResults.close();
            } catch (QueryEvaluationException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSERQL() {
        StringBuffer query = new StringBuffer();
        query.append("SELECT X FROM {X} <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ");
        query.append("{<http://example.org/ontology#animal>}");

        QueryResult<BindingSet> queryResults = TestOWLIMStorage.owlimStorage.query(QueryLanguage.SERQL, query
                .toString());

        try {
            Assert.assertTrue(queryResults.hasNext());

            BindingSet bindingSet = queryResults.next();

            Assert.assertEquals("http://example.org/ontology#psychoquack", bindingSet.getValue("X")
                    .toString());

            Assert.assertFalse(queryResults.hasNext());
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        } finally {
            try {
                queryResults.close();
            } catch (QueryEvaluationException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSPARQLAsk() {
        Assert.assertEquals(true, TestOWLIMStorage.owlimStorage.queryB(QueryLanguage.SPARQL,
                "ASK { ?o ?p ?s }"));
        Assert.assertEquals(true, TestOWLIMStorage.owlimStorage.hasStatements());
    }

    @Test
    public void testRemove() {
        TestOWLIMStorage.owlimStorage.remove(TestOWLIMStorage.owlimValueFactory.createStatement(
                TestOWLIMStorage.subject, TestOWLIMStorage.predicate, TestOWLIMStorage.object));

        Assert.assertEquals(0, TestOWLIMStorage.owlimStorage.query(
                TestOWLIMStorage.owlimStorage.getRepository().getValueFactory().createStatement(
                        TestOWLIMStorage.subject, null, null)).size());

        Assert.assertEquals(false, TestOWLIMStorage.owlimStorage.hasStatements());
    }

    @AfterClass
    public static void tearDownAfterClass() {
        TestOWLIMStorage.owlimStorage.shutdownByRemovingCurrentRepository();
    }
}
