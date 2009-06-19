package functionalTests.structuredp2p;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import functionalTests.structuredp2p.can.TestQuery;
import functionalTests.structuredp2p.can.TestNeighborsDataStructure;
import functionalTests.structuredp2p.can.TestOverlay;


@RunWith(Suite.class)
@SuiteClasses(value = { TestTracker.class, TestZone.class, TestCoordinate.class,
        TestNeighborsDataStructure.class, TestOverlay.class, TestQuery.class })
public class AllTests {
}
