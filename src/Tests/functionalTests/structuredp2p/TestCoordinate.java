package functionalTests.structuredp2p;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.LexicographicCoordinate;


/**
 * Test the class {@link Coordinate}.
 */
public class TestCoordinate {

    private Coordinate coordinate;
    private Coordinate firstCoordinate;
    private Coordinate secondCoordinate;

    @Before
    public void setUp() throws Exception {
        this.firstCoordinate = new LexicographicCoordinate("e");
        this.secondCoordinate = new LexicographicCoordinate("l");
    }

    @Test
    public void testCompare() {
        Assert.assertTrue(this.firstCoordinate.compareTo(this.secondCoordinate) < 0);
        Assert.assertTrue(this.secondCoordinate.compareTo(this.firstCoordinate) > 0);

        this.coordinate = new LexicographicCoordinate("e");
        Assert.assertTrue(this.firstCoordinate.compareTo(this.coordinate) == 0);
        Assert.assertTrue(this.coordinate.compareTo(this.firstCoordinate) == 0);

        this.coordinate = new LexicographicCoordinate("a");
        Assert.assertTrue(this.secondCoordinate.compareTo(this.coordinate) > 0);
        Assert.assertTrue(this.coordinate.compareTo(this.secondCoordinate) < 0);

        this.coordinate = new LexicographicCoordinate("l");
        Assert.assertTrue(this.secondCoordinate.compareTo(this.coordinate) == 0);
        Assert.assertTrue(this.coordinate.compareTo(this.secondCoordinate) == 0);
    }

    @Test
    public void testIsBetween() {
        this.coordinate = new LexicographicCoordinate("a");
        Assert
                .assertFalse(Coordinate.isBetween(this.coordinate, this.firstCoordinate,
                        this.secondCoordinate));

        this.coordinate = new LexicographicCoordinate("g");
        Assert.assertTrue(Coordinate.isBetween(this.coordinate, this.firstCoordinate, this.secondCoordinate));

        this.coordinate = new LexicographicCoordinate("eff");
        Assert.assertTrue(Coordinate.isBetween(this.coordinate, this.firstCoordinate, this.secondCoordinate));

        this.coordinate = new LexicographicCoordinate("lee");
        Assert
                .assertFalse(Coordinate.isBetween(this.coordinate, this.firstCoordinate,
                        this.secondCoordinate));
    }

    @Test
    public void testMiddle() {
        Assert.assertEquals(new LexicographicCoordinate("h\u0001"), this.firstCoordinate
                .getMiddleWith(this.secondCoordinate));

        Assert.assertNotSame(new LexicographicCoordinate("e"), this.firstCoordinate
                .getMiddleWith(this.secondCoordinate));
    }

    @Test
    public void testGetMiddle() {
        Coordinate middleCoordinate = this.firstCoordinate;
        for (int nbSplit = 0; nbSplit < 500; nbSplit++) {
            middleCoordinate = middleCoordinate.getMiddleWith(this.secondCoordinate);
            Assert.assertTrue(middleCoordinate.compareTo(this.secondCoordinate) < 0);
        }
    }

    @After
    public void tearDown() {
        this.coordinate = null;
        this.secondCoordinate = null;
        this.firstCoordinate = null;
    }
}
