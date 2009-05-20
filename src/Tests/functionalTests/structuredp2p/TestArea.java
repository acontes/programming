package functionalTests.structuredp2p;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.extensions.structuredp2p.core.can.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.can.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.exception.AreaException;


/**
 * Test the class {@link Area}.
 */
public class TestArea {

    private static Area area;

    @BeforeClass
    public static void setUp() throws Exception {
        TestArea.area = new Area();
    }

    @Test
    public void testArea() {
        Coordinate[] coords = new Coordinate[CANOverlay.NB_DIMENSIONS];

        for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            coords[i] = new Coordinate("" + Area.MIN_COORD);
        }
        Assert.assertArrayEquals(TestArea.area.getCoordinatesMin(), coords);

        for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            coords[i] = new Coordinate("" + Area.MAX_COORD);
        }
        Assert.assertArrayEquals(TestArea.area.getCoordinatesMax(), coords);
    }

    @Test
    public void testSplitAndMerge() throws AreaException {
        Area[] newAreas = TestArea.area.split(0);
        Coordinate[] coords = new Coordinate[CANOverlay.NB_DIMENSIONS];

        for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            coords[i] = new Coordinate("" + Area.MIN_COORD);
        }
        Assert.assertArrayEquals(newAreas[0].getCoordinatesMin(), coords);

        coords[0] = new Coordinate("" + 128);
        for (int i = 1; i < CANOverlay.NB_DIMENSIONS; i++) {
            coords[i] = new Coordinate("" + Area.MAX_COORD);
        }
        Assert.assertArrayEquals(newAreas[0].getCoordinatesMax(), coords);

        coords[0] = new Coordinate("" + 128);
        for (int i = 1; i < CANOverlay.NB_DIMENSIONS; i++) {
            coords[i] = new Coordinate("" + Area.MIN_COORD);
        }
        Assert.assertArrayEquals(newAreas[1].getCoordinatesMin(), coords);

        for (int i = 0; i < CANOverlay.NB_DIMENSIONS; i++) {
            coords[i] = new Coordinate("" + Area.MAX_COORD);
        }
        Assert.assertArrayEquals(newAreas[1].getCoordinatesMax(), coords);

        Assert.assertEquals(newAreas[0].getBorderedDimension(newAreas[1]), 0);
        Assert.assertTrue(newAreas[0].isBordered(newAreas[1], 0));

        Area mergedArea = newAreas[0].merge(newAreas[1]);
        Assert.assertEquals(mergedArea, TestArea.area);
    }

    @AfterClass
    public static void tearDown() {
        TestArea.area = null;
    }
}
