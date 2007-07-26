package unitTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.objectweb.proactive.core.util.CircularArrayList.UnitTestCircularArrayList;
import org.objectweb.proactive.extra.gcmdeployment.process.hostinfo.HostInfoImpl.UnitTestHostInfoImpl;


@RunWith(Suite.class)
/**
 * All in-place Unit tests must be declared here otherwise they will not
 * be run.
 *
 * Please use the following convention:
 * <ul>
 *         <li>Add a static inner class to the class. Use <b>UnitTest</b> a prefix the for classname</li>
 *  <li>Add this class to the following <b>SuiteClasses</b> annotation</li>
 * </ul>
 */
@SuiteClasses({UnitTestCircularArrayList.class,
    UnitTestHostInfoImpl.class
})
public class UnitTests {
}
