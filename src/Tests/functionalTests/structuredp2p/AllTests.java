package functionalTests.structuredp2p;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import functionalTests.structuredp2p.overlay.can.TestNeighborsDataStructure;
import functionalTests.structuredp2p.overlay.can.TestOverlay;
import functionalTests.structuredp2p.overlay.can.TestQueries;


@RunWith(Suite.class)
@SuiteClasses(value = { TestTracker.class, TestZone.class, TestCoordinate.class,
        TestNeighborsDataStructure.class, TestOverlay.class, TestQueries.class, TestOWLIMStorage.class })
public class AllTests {
}
