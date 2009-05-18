package functionalTests.structuredp2p;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.Tracker;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.OverlayType;


/**
 * Test the class {@link Tracker}.
 */
public class TestTracker {

    private static Tracker tracker;
    private static Peer peer;

    @BeforeClass
    public static void setUp() throws Exception {
        try {
            TestTracker.tracker = (Tracker) PAActiveObject.newActive(Tracker.class.getName(),
                    new Object[] { OverlayType.CAN });
            // Binds the tracker to a specific URL on the RMI registry
            PAActiveObject.registerByName(TestTracker.tracker, "TestTracker");
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testAddOnNetworkWithWrongPeerType() throws ActiveObjectCreationException, NodeException {
        Peer peer = (Peer) PAActiveObject.newActive(Peer.class.getName(), new Object[] { OverlayType.CHORD });
        TestTracker.tracker.addOnNetwork(peer.getStub());
    }

    @Test
    public void testGetRandomPeerWithEmptyTracker() {
        Assert.assertEquals(null, PAFuture.getFutureValue(TestTracker.tracker.getRandomPeer()));
        Assert.assertEquals(0, TestTracker.tracker.getNumberOfManagedPeers());
    }

    @Test
    public void testAddOnNetworkWithCorrectPeerType() throws ActiveObjectCreationException, NodeException {
        TestTracker.peer = (Peer) PAActiveObject.newActive(Peer.class.getName(),
                new Object[] { OverlayType.CAN });
        TestTracker.tracker.addOnNetwork(TestTracker.peer.getStub());

        Assert.assertEquals(1, TestTracker.tracker.getNumberOfManagedPeers());
    }

    @Test
    public void testGetRandomPeer() {
        Assert.assertEquals(TestTracker.peer, PAFuture.getFutureValue(TestTracker.tracker.getRandomPeer()));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        PAActiveObject.terminateActiveObject(TestTracker.peer, false);
        PAActiveObject.terminateActiveObject(TestTracker.tracker, false);
    }
}
