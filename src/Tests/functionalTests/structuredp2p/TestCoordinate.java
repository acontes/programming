package functionalTests.structuredp2p;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Coordinate;


/**
 * Test the class {@link Coordinate}.
 */
public class TestCoordinate {

    private Coordinate coord;
    private Coordinate coordMin;
    private Coordinate coordMax;

    @Before
    public void setUp() throws Exception {
        this.coordMin = new Coordinate("0");
        this.coordMax = new Coordinate("100");
    }

    @Test
    public void testCompare() {
        Assert.assertTrue(this.coordMin.compareTo(this.coordMax) < 0);
        Assert.assertTrue(this.coordMax.compareTo(this.coordMin) > 0);

        this.coord = new Coordinate("0");
        Assert.assertTrue(this.coordMin.compareTo(this.coord) == 0);
        Assert.assertTrue(this.coord.compareTo(this.coordMin) == 0);

        this.coord = new Coordinate("50");
        Assert.assertTrue(this.coordMax.compareTo(this.coord) > 0);
        Assert.assertTrue(this.coord.compareTo(this.coordMax) < 0);

        this.coord = new Coordinate("100");
        Assert.assertTrue(this.coordMax.compareTo(this.coord) == 0);
        Assert.assertTrue(this.coord.compareTo(this.coordMax) == 0);
    }

    @Test
    public void testIsBetween() {
        this.coord = new Coordinate("-10");
        Assert.assertFalse(this.coord.isBetween(this.coordMin, this.coordMax));

        this.coord = new Coordinate("0");
        Assert.assertTrue(this.coord.isBetween(this.coordMin, this.coordMax));

        this.coord = new Coordinate("50");
        Assert.assertTrue(this.coord.isBetween(this.coordMin, this.coordMax));

        this.coord = new Coordinate("100");
        Assert.assertFalse(this.coord.isBetween(this.coordMin, this.coordMax));

        this.coord = new Coordinate("200");
        Assert.assertFalse(this.coord.isBetween(this.coordMin, this.coordMax));
    }

    @Test
    public void testMiddle() {
        this.coord = new Coordinate("50");
        Assert.assertEquals(this.coord, Coordinate.getMiddle(this.coordMin, this.coordMax));

        this.coord = new Coordinate("0");
        Assert.assertNotSame(this.coord, Coordinate.getMiddle(this.coordMin, this.coordMax));
    }

    @After
    public void tearDown() {
        this.coord = null;
        this.coordMax = null;
        this.coordMin = null;
    }
}
