package functionalTests.structuredp2p.overlay.can;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.can.LookupQuery;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.can.LookupQueryResponse;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;


/**
 * Test {@link PingMessage}.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class TestQuery {

    private static Peer firstPeer;
    private static Peer secondPeer;
    private static Peer thirdPeer;
    private static Peer fourthPeer;

    private static Query query;

    @BeforeClass
    public static void setUp() throws Exception {
        TestQuery.firstPeer = Peer.newActivePeer(OverlayType.CAN);
        TestQuery.secondPeer = Peer.newActivePeer(OverlayType.CAN);
        TestQuery.thirdPeer = Peer.newActivePeer(OverlayType.CAN);
        TestQuery.fourthPeer = Peer.newActivePeer(OverlayType.CAN);

        for (int i = 0; i < 100; i++) {
            TestQuery.firstPeer.addData();
        }

        try {
            TestQuery.secondPeer.join(TestQuery.firstPeer);
            TestQuery.thirdPeer.join(TestQuery.secondPeer);
            TestQuery.fourthPeer.join(TestQuery.thirdPeer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Peer> peers = new ArrayList<Peer>();
        peers.add(TestQuery.firstPeer);
        peers.add(TestQuery.secondPeer);
        peers.add(TestQuery.thirdPeer);
        peers.add(TestQuery.fourthPeer);

        int i = 1;
        for (Peer peer : peers) {
            System.out.println(i + " " + peer.getStructuredOverlay());
            for (Peer neighbor : ((CANOverlay) peer.getStructuredOverlay()).getNeighborsDataStructure()) {
                System.out.println("n = " + neighbor.getStructuredOverlay());
            }
            System.out.println();
            i++;
        }

        TestQuery.query = new LookupQuery(TestQuery.firstPeer, ((CANOverlay) TestQuery.thirdPeer
                .getStructuredOverlay()).getZone().getCoordinatesMin());
    }

    @Test
    public void testQueryParsing() {
        SPARQLParser parser = new SPARQLParser();
        ParsedQuery pq = null;

        try {
            pq = parser.parseQuery(
                    "SELECT ?o ?p ?s WHERE { ?o ?p ?s. FILTER ( str(?o) > \"http://toto\"). }", null);
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBasicSearch() {
        LookupQueryResponse response = null;
        try {
            response = (LookupQueryResponse) PAFuture.getFutureValue(TestQuery.firstPeer
                    .search(TestQuery.query));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(response.getLatency() > 1);
        Assert.assertTrue(response.getNbSteps() > 0);
        Assert.assertTrue(response.getNbStepsForSend() > 0);
        Assert.assertEquals(TestQuery.thirdPeer, response.getPeerFound());
    }

    @Test
    public void testLeave() {
        TestQuery.fourthPeer.leave();
        // TestQuery.thirdPeer.leave();
        // TestQuery.secondPeer.leave();
        // TestQuery.firstPeer.leave();

        List<Peer> peers = new ArrayList<Peer>();
        peers.add(TestQuery.firstPeer);
        peers.add(TestQuery.secondPeer);
        peers.add(TestQuery.thirdPeer);
        peers.add(TestQuery.fourthPeer);

        int i = 1;
        for (Peer peer : peers) {
            System.out.println(i + " " + peer.getStructuredOverlay());

            for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
                for (int direction = 0; direction < 2; direction++) {
                    for (Peer neighbor : ((CANOverlay) peer.getStructuredOverlay())
                            .getNeighborsDataStructure().getNeighbors(dim, direction)) {
                        System.out.println("n = " + neighbor.getStructuredOverlay() + " (dim=" + dim + "," +
                            direction + ")");
                    }
                }
            }

            System.out.println();
            i++;
        }
    }

    @AfterClass
    public static void tearDown() {
        TestQuery.query = null;
    }

}