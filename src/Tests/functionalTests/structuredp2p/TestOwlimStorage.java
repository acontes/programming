package functionalTests.structuredp2p;

import info.aduna.iteration.CloseableIteration;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.extensions.structuredp2p.data.DataStorage;
import org.objectweb.proactive.extensions.structuredp2p.data.OwlimStorage;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;


/**
 * Test the OwlimStorage wrappers.
 * 
 * @author Pellegrino Laurent
 * @version 0.1, 07/15/2009
 */
public class TestOwlimStorage {
    private static DataStorage owlimStorage;
    private static URI subject = new URIImpl("http://exa‹mple.org/owlim#Pilat");
    private static URI predicate = new URIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private static URI object = new URIImpl("http://example.org/owlim#Human");

    @BeforeClass
    public static void setUpBeforeClass() {
        TestOwlimStorage.owlimStorage = new OwlimStorage();
    }

    @Test
    public void testAdd() {
        TestOwlimStorage.owlimStorage.add(new StatementImpl(TestOwlimStorage.subject,
            TestOwlimStorage.predicate, TestOwlimStorage.object));

        List<Statement> results = null;
        try {
            results = TestOwlimStorage.owlimStorage.evaluateQuery(
                    new StatementImpl(TestOwlimStorage.subject, null, null)).asList();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(2, results.size());
        Assert.assertEquals(TestOwlimStorage.subject, results.get(0).getSubject());
        Assert.assertEquals(TestOwlimStorage.predicate, results.get(0).getPredicate());
        Assert.assertEquals(TestOwlimStorage.subject, results.get(1).getSubject());
        Assert.assertEquals(TestOwlimStorage.predicate, results.get(1).getPredicate());
        Assert.assertEquals(TestOwlimStorage.object, results.get(1).getObject());
    }

    @Test
    public void testSPARQLQueries() {
        String query = "";
        query += "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
        query += "PREFIX ex:   <http://example.org/owlim#>\n";
        query += "SELECT ?man WHERE {\n";
        query += "  ?man rdf:type ex:Human .\n";
        query += "}";

        CloseableIteration<BindingSet, QueryEvaluationException> results = TestOwlimStorage.owlimStorage
                .evaluateQuery(QueryLanguage.SPARQL, query);

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
        TestOwlimStorage.owlimStorage.remove(new StatementImpl(TestOwlimStorage.subject, null, null));

        try {
            Assert.assertEquals(0, TestOwlimStorage.owlimStorage.evaluateQuery(
                    new StatementImpl(TestOwlimStorage.subject, null, null)).asList().size());
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDownAfterClass() {
        TestOwlimStorage.owlimStorage.shutdown();
    }
}
