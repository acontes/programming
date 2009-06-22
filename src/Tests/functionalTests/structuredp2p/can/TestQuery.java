package functionalTests.structuredp2p.can;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.Query;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.QueryResponse;
import org.objectweb.proactive.extensions.structuredp2p.messages.oneway.can.RDFQuery;


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
        TestQuery.firstPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestQuery.secondPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestQuery.thirdPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestQuery.fourthPeer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });

        try {
            TestQuery.secondPeer.join(TestQuery.firstPeer);
            TestQuery.thirdPeer.join(TestQuery.secondPeer);
            TestQuery.fourthPeer.join(TestQuery.thirdPeer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TestQuery.query = new RDFQuery(((CANOverlay) TestQuery.thirdPeer.getStructuredOverlay()).getZone()
                .getCoordinatesMin(), ((CANOverlay) TestQuery.firstPeer.getStructuredOverlay()).getZone()
                .getCoordinatesMin());
    }

    @Test
    public void testMySearch() {
        QueryResponse response = TestQuery.firstPeer.search(TestQuery.query);

        Assert.assertTrue(response.getLatency() > 1);
        Assert.assertTrue(response.getNbSteps() > 0);
        Assert.assertTrue(response.getNbStepsForSend() > 0);
    }

    @AfterClass
    public static void tearDown() {
        TestQuery.query = null;
    }

}