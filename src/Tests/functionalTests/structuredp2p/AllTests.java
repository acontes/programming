package functionalTests.structuredp2p;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import functionalTests.structuredp2p.can.TestOverlay2D;


@RunWith(Suite.class)
@SuiteClasses(value = { TestTracker.class, TestOverlay2D.class })
public class AllTests {
}
