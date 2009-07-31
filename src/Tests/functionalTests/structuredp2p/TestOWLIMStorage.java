package functionalTests.structuredp2p;

import java.util.Set;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.proactive.extensions.structuredp2p.datastorage.DataStorage;
import org.objectweb.proactive.extensions.structuredp2p.datastorage.owlim.OWLIMStorage;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResult;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;


/**
 * Test the OwlimStorage Wrappers.
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/25/2009
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

    @Ignore
    public void testParseQuery() {
        SPARQLParser parser = new SPARQLParser();
        ParsedQuery pq = null;
        try {
            pq = parser.parseQuery(
                    "SELECT ?o ?p ?s WHERE { ?o ?p ?s. FILTER ( str(?o) > \"http://toto\"). }", null);
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        }

        System.out.println(pq.getTupleExpr());
        System.out.println("Signature = " + pq.getTupleExpr().getSignature());
    }

    @Test
    public void testAdd() {

        TestOWLIMStorage.owlimStorage.add(TestOWLIMStorage.owlimValueFactory.createStatement(
                TestOWLIMStorage.subject, TestOWLIMStorage.predicate, TestOWLIMStorage.object));

        Set<Statement> results = null;
        results = TestOWLIMStorage.owlimStorage.query(TestOWLIMStorage.owlimValueFactory.createStatement(
                TestOWLIMStorage.subject, null, null));

        Statement[] resultsAsArray = results.toArray(new Statement[] {});

        Assert.assertEquals(1, results.size());
        Assert.assertEquals(TestOWLIMStorage.subject, resultsAsArray[0].getSubject());
        Assert.assertEquals(TestOWLIMStorage.predicate, resultsAsArray[0].getPredicate());
    }

    @Test
    public void testSPARQLQuery() {
        StringBuffer query = new StringBuffer();
        query.append("PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
        query.append("SELECT ?s ?p ?o WHERE {\n");
        query.append("  ?s ?p ?o .\n");
        // query.append("  FILTER ( str(?s) < \"http://example.org/ontology#q\" ).\n");
        query.append("}");

        QueryResult<BindingSet> queryResults = TestOWLIMStorage.owlimStorage.query(QueryLanguage.SPARQL,
                query.toString());

        try {
            Assert.assertTrue(queryResults.hasNext());

            BindingSet bindingSet = queryResults.next();

            Assert.assertEquals("http://example.org/ontology#psychoquack", bindingSet.getValue("s")
                    .toString());
            Assert.assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", bindingSet.getValue("p")
                    .toString());
            Assert.assertEquals("http://example.org/ontology#animal", bindingSet.getValue("o").toString());

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
    public void testSERQLQuery() {
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
    public void testSPARQLAskQuery() {
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

    @Test
    public void testSPARQLQueryWithFilter() {
        ValueFactory vf = TestOWLIMStorage.owlimStorage.getRepository().getValueFactory();

        String[][] data = new String[][] { { "a", "a", "a" }, { "b", "b", "b" }, { "b", "c", "d" },
                { "e", "b", "e" }, { "a", "e", "o" } };

        for (String[] element : data) {
            TestOWLIMStorage.owlimStorage.add(vf.createStatement(vf.createURI("http://" + element[0]), vf
                    .createURI("http://" + element[1]), vf.createURI("http://" + element[2])));
        }

        StringBuffer queryString = new StringBuffer();
        queryString.append("SELECT ?s ?p ?o WHERE { ?s ?p ?o . \n");
        queryString.append("  FILTER ( ");
        queryString.append("    str(?s) > \"http://a\" && str(?s) < \"http://h\" &&  \n");
        queryString.append("    str(?p) > \"http://a\" && str(?p) < \"http://h\" && \n");
        queryString.append("    str(?o) > \"http://a\" && str(?o) < \"http://h\" \n");
        queryString.append("  ).}");

        QueryResult<BindingSet> result = TestOWLIMStorage.owlimStorage.query(QueryLanguage.SPARQL,
                queryString.toString());

        try {
            String prefix = "http://";
            int index = 1;

            while (result.hasNext()) {
                BindingSet bs = result.next();

                Assert.assertEquals(prefix + data[index][0], bs.getValue("s").stringValue());
                Assert.assertEquals(prefix + data[index][1], bs.getValue("p").stringValue());
                Assert.assertEquals(prefix + data[index][2], bs.getValue("o").stringValue());

                index++;
            }
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDownAfterClass() {
        TestOWLIMStorage.owlimStorage.shutdown();
        TestOWLIMStorage.owlimStorage = null;

        TestOWLIMStorage.subject = null;
        TestOWLIMStorage.predicate = null;
        TestOWLIMStorage.object = null;
    }
}
