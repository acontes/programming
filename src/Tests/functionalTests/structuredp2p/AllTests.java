package functionalTests.structuredp2p;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import functionalTests.structuredp2p.can.TestBasicOperations;
import functionalTests.structuredp2p.can.TestMessages;
import functionalTests.structuredp2p.can.TestOverlay2D;


@RunWith(Suite.class)
@SuiteClasses(value = { TestTracker.class, TestArea.class, TestCoordinate.class, TestOverlay2D.class,
        TestMessages.class, TestBasicOperations.class })
public class AllTests {
}
