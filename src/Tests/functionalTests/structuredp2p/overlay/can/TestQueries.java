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
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.Query;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can.LookupQuery;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can.LookupQueryResponse;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can.RDFQueryResponse;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can.RDFTriplePatternQuery;
import org.openrdf.model.Statement;


/**
 * Test the oneway queries.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
public class TestQueries {

    private static Peer firstPeer;
    private static Peer secondPeer;
    private static Peer thirdPeer;
    private static Peer fourthPeer;

    private static Query query;

    @BeforeClass
    public static void setUp() throws Exception {
        TestQueries.firstPeer = Peer.newActivePeer(OverlayType.CAN);
        TestQueries.secondPeer = Peer.newActivePeer(OverlayType.CAN);
        TestQueries.thirdPeer = Peer.newActivePeer(OverlayType.CAN);
        TestQueries.fourthPeer = Peer.newActivePeer(OverlayType.CAN);

        for (int i = 0; i < 100; i++) {
            TestQueries.firstPeer.addData();
        }

        try {
            TestQueries.secondPeer.join(TestQueries.firstPeer);
            TestQueries.thirdPeer.join(TestQueries.secondPeer);
            TestQueries.fourthPeer.join(TestQueries.thirdPeer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Peer> peers = new ArrayList<Peer>();
        peers.add(TestQueries.firstPeer);
        peers.add(TestQueries.secondPeer);
        peers.add(TestQueries.thirdPeer);
        peers.add(TestQueries.fourthPeer);

        int i = 1;
        for (Peer peer : peers) {
            System.out.println(i + " " + peer.getStructuredOverlay());
            for (Peer neighbor : ((CANOverlay) peer.getStructuredOverlay()).getNeighborsDataStructure()) {
                System.out.println("n = " + neighbor.getStructuredOverlay());
            }
            System.out.println();
            i++;
        }

        TestQueries.query = new LookupQuery(TestQueries.firstPeer, ((CANOverlay) TestQueries.thirdPeer
                .getStructuredOverlay()).getZone().getCoordinatesMin());
    }

    @Test
    public void testLookupQuery() {
        LookupQueryResponse response = null;
        try {
            response = (LookupQueryResponse) PAFuture.getFutureValue(TestQueries.firstPeer
                    .search(TestQueries.query));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(response.getLatency() > 1);
        Assert.assertTrue(response.getNbSteps() > 0);
        Assert.assertTrue(response.getNbStepsForReceipt() > 0);
        Assert.assertTrue(response.getNbStepsForSend() > 0);
        Assert.assertEquals(TestQueries.thirdPeer, response.getPeerFound());
    }

    @Test
    public void testRDFTriplePatternQuery() {
        RDFQueryResponse response = null;

        Coordinate[] coordinates = ((CANOverlay) TestQueries.thirdPeer.getStructuredOverlay()).getZone()
                .getCoordinatesMin();

        try {
            response = (RDFQueryResponse) PAFuture.getFutureValue(TestQueries.firstPeer
                    .search(new RDFTriplePatternQuery(coordinates[0], null, coordinates[2])));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("latency = " + response.getLatency());
        System.out.println("nbStepForReceipt = " + response.getNbStepsForReceipt());
        System.out.println("nbStepsForSend = " + response.getNbStepsForSend());

        System.out.println("data = ");
        for (Statement stmt : (response).getRetrievedStatements()) {
            System.out.println("- <" + stmt.getSubject() + ", " + stmt.getPredicate() + "," +
                stmt.getObject());
        }

        // Assert.assertTrue(response.getLatency() > 1);
        Assert.assertTrue(response.getNbSteps() > 0);
        Assert.assertTrue(response.getNbStepsForReceipt() > 0);
        Assert.assertTrue(response.getNbStepsForSend() > 0);
    }

    @Test
    public void testLeave() {
        TestQueries.fourthPeer.leave();
        TestQueries.thirdPeer.leave();
        TestQueries.secondPeer.leave();
        TestQueries.firstPeer.leave();

        List<Peer> peers = new ArrayList<Peer>();
        peers.add(TestQueries.firstPeer);
        peers.add(TestQueries.secondPeer);
        peers.add(TestQueries.thirdPeer);
        peers.add(TestQueries.fourthPeer);

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
        TestQueries.query = null;
    }
}