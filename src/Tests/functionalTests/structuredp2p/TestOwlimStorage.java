package functionalTests.structuredp2p;

import info.aduna.iteration.CloseableIteration;

import java.util.ArrayList;
import java.util.List;
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
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;


/**
 * Test the OwlimStorage wrappers.
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/15/2009
 */
public class TestOwlimStorage {
    private static DataStorage owlimStorage;
    private static ValueFactory owlimValueFactory;

    private static URI subject;
    private static URI predicate;
    private static URI object;

    @BeforeClass
    public static void setUpBeforeClass() {
        TestOwlimStorage.owlimStorage = new OWLIMStorage();
        TestOwlimStorage.owlimValueFactory = TestOwlimStorage.owlimStorage.getRepository().getValueFactory();

        TestOwlimStorage.subject = TestOwlimStorage.owlimValueFactory
                .createURI("http://example.org/owlim#Pilat");
        TestOwlimStorage.predicate = TestOwlimStorage.owlimValueFactory
                .createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        TestOwlimStorage.object = TestOwlimStorage.owlimValueFactory
                .createURI("http://example.org/owlim#Human");
    }

    @Test
    public void testAdd() {
        TestOwlimStorage.owlimStorage.add(new StatementImpl(TestOwlimStorage.subject,
            TestOwlimStorage.predicate, TestOwlimStorage.object));

        Set<Statement> results = null;
        results = TestOwlimStorage.owlimStorage
                .query(new StatementImpl(TestOwlimStorage.subject, null, null));

        Statement[] resultsAsArray = results.toArray(new Statement[] {});

        Assert.assertEquals(1, results.size());
        Assert.assertEquals(TestOwlimStorage.subject, resultsAsArray[0].getSubject());
        Assert.assertEquals(TestOwlimStorage.predicate, resultsAsArray[0].getPredicate());
    }

    @Test
    public void testSPARQLQueries() {
        String query = "";
        // query += "PREFIX fn\n";
        // query += "PREFIX op\n";
        query += "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
        query += "PREFIX ex:   <http://example.org/owlim#>\n";
        query += "SELECT ?man WHERE {\n";
        query += "  ?man rdf:type ex:Human .\n} ";
        // query +=
        // "  FILTER op:numeric-equal(fn:compare(STR(?man), STR(<http://example.org/owlim#Q>)), -1)}";

        CloseableIteration<? extends BindingSet, QueryEvaluationException> results = TestOwlimStorage.owlimStorage
                .query(QueryLanguage.SPARQL, query);

        List<String> subjects = new ArrayList<String>();

        try {
            while (results.hasNext()) {
                for (Binding binding : results.next()) {
                    try {
                        subjects.add(binding.getValue().stringValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                results.close();
            } catch (QueryEvaluationException e) {
                e.printStackTrace();
            }
        }

        Assert.assertTrue(subjects.contains("http://example.org/owlim#Pilat"));

    }

    @Test
    public void testSPARQLTupleQuery() {
        String query = "";
        query += "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
        query += "PREFIX ex:   <http://example.org/owlim#>\n";
        query += "SELECT ?man WHERE {\n";
        query += "  ?man rdf:type ex:Human .\n} ";

        TupleQueryResult result = TestOwlimStorage.owlimStorage.tupleQuery(QueryLanguage.SPARQL, query);

        System.out.println("TestOwlimStorage.testSPARQLTupleQuery()");

        List<String> bindingNames = result.getBindingNames();
        try {
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();

                for (String bindingName : bindingNames) {
                    System.out.println(bindingSet.getValue(bindingName));
                }
            }
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        }
    }

    @Ignore
    public void testSERQLQueries() {
        String query = "";

        query += "SELECT X  FROM {X} WHERE X ";

        CloseableIteration<? extends BindingSet, QueryEvaluationException> results = TestOwlimStorage.owlimStorage
                .query(QueryLanguage.SERQL, query);

        List<String> subjects = new ArrayList<String>();

        try {
            while (results.hasNext()) {
                for (Binding binding : results.next()) {
                    try {
                        subjects.add(binding.getValue().stringValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                results.close();
            } catch (QueryEvaluationException e) {
                e.printStackTrace();
            }
        }

        Assert.assertTrue(subjects.contains("http://example.org/owlim#Pilat"));

    }

    @Test
    public void testRemove() {
        TestOwlimStorage.owlimStorage.remove(TestOwlimStorage.owlimValueFactory.createStatement(
                TestOwlimStorage.subject, TestOwlimStorage.predicate, TestOwlimStorage.object));

        Assert.assertEquals(0, TestOwlimStorage.owlimStorage.query(
                new StatementImpl(TestOwlimStorage.subject, null, null)).size());
    }

    @AfterClass
    public static void tearDownAfterClass() {
        TestOwlimStorage.owlimStorage.shutdown();
    }
}
