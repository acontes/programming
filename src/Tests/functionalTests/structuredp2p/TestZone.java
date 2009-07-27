package functionalTests.structuredp2p;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Zone;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.ZoneException;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.LexicographicCoordinate;


/**
 * Test the class {@link Zone}.
 */
public class TestZone {

    private static Zone zone;

    @BeforeClass
    public static void setUp() throws Exception {
        TestZone.zone = new Zone();
    }

    @Test
    public void testZone() {
        Coordinate[] coords = new Coordinate[CANOverlay.NB_DIMENSIONS];

        for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            coords[i] = new LexicographicCoordinate("" + Zone.COORDINATE_MIN);
        }
        Assert.assertArrayEquals(TestZone.zone.getCoordinatesMin(), coords);

        for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            coords[i] = new LexicographicCoordinate("" + Zone.COORDINATE_MAX);
        }
        Assert.assertArrayEquals(TestZone.zone.getCoordinatesMax(), coords);
    }

    @Test
    public void testSplitAndMerge() throws ZoneException {
        Zone[] newZones = TestZone.zone.split(0);
        Coordinate[] coords = new Coordinate[CANOverlay.NB_DIMENSIONS];

        for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            coords[i] = new LexicographicCoordinate(Zone.COORDINATE_MIN);
        }
        Assert.assertArrayEquals(newZones[0].getCoordinatesMin(), coords);

        coords[0] = (new LexicographicCoordinate(Zone.COORDINATE_MIN))
                .getMiddleWith(new LexicographicCoordinate(Zone.COORDINATE_MAX));
        for (int i = 1; i < CANOverlay.NB_DIMENSIONS; i++) {
            coords[i] = new LexicographicCoordinate(Zone.COORDINATE_MAX);
        }
        Assert.assertArrayEquals(newZones[0].getCoordinatesMax(), coords);

        coords[0] = (new LexicographicCoordinate(Zone.COORDINATE_MIN))
                .getMiddleWith(new LexicographicCoordinate(Zone.COORDINATE_MAX));
        for (int i = 1; i < CANOverlay.NB_DIMENSIONS; i++) {
            coords[i] = new LexicographicCoordinate(Zone.COORDINATE_MIN);
        }
        Assert.assertArrayEquals(newZones[1].getCoordinatesMin(), coords);

        for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            coords[i] = new LexicographicCoordinate(Zone.COORDINATE_MAX);
        }
        Assert.assertArrayEquals(newZones[1].getCoordinatesMax(), coords);

        Assert.assertEquals(newZones[0].getBorderedDimension(newZones[1]), 0);
        Assert.assertTrue(newZones[0].isBordered(newZones[1], 0));

        Zone mergedZone = newZones[0].merge(newZones[1]);
        Assert.assertEquals(mergedZone, TestZone.zone);
    }

    @AfterClass
    public static void tearDown() {
        TestZone.zone = null;
    }
}
