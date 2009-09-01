package functionalTests.structuredp2p.overlay.can;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.SynchronousMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can.LookupQueryMessage;
import org.objectweb.proactive.extensions.structuredp2p.messages.synchronous.can.RDFTriplePatternQueryMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.synchronous.can.LookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.responses.synchronous.can.RDFResponseMessage;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;


/**
 * Test the Synchronous queries.
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

    private static SynchronousMessage query;

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

            TestQueries.printNeighborsOf(peer);

            Set<Statement> results = peer.query(new StatementImpl(null, null, null));
            for (Statement stmt : results) {
                System.out.println(" - <" + stmt.getSubject() + "," + stmt.getPredicate() + "," +
                    stmt.getObject() + ">");
            }

            System.out.println();
            System.out.println();
            i++;
        }

        TestQueries.query = new LookupQueryMessage(TestQueries.firstPeer, ((CANOverlay) TestQueries.thirdPeer
                .getStructuredOverlay()).getZone().getCoordinatesMin());
    }

    @Test
    public void testLookupQuery() {
        LookupResponseMessage response = null;
        try {
            response = (LookupResponseMessage) PAFuture.getFutureValue(TestQueries.firstPeer
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
        RDFResponseMessage response = null;

        Coordinate[] coordinates = ((CANOverlay) TestQueries.thirdPeer.getStructuredOverlay()).getZone()
                .getCoordinatesMin();

        try {
            response = (RDFResponseMessage) PAFuture.getFutureValue(TestQueries.firstPeer
                    .search(new RDFTriplePatternQueryMessage(coordinates[0], null, coordinates[2])));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue(response.getLatency() > 1);
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

            TestQueries.printNeighborsOf(peer);

            System.out.println();
            i++;
        }
    }

    private static void printNeighborsOf(Peer peer) {
        for (int dim = 0; dim < CANOverlay.NB_DIMENSIONS; dim++) {
            for (int direction = 0; direction < 2; direction++) {
                for (Peer neighbor : ((CANOverlay) peer.getStructuredOverlay()).getNeighborsDataStructure()
                        .getNeighbors(dim, direction)) {
                    System.out.println("n = " + neighbor.getStructuredOverlay() + " (dim=" + dim + "," +
                        direction + ")");
                }
            }
        }
    }

    @AfterClass
    public static void tearDown() {
        TestQueries.query = null;
    }
}