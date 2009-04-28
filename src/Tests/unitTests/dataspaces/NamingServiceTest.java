/**
 * 
 */
package unitTests.dataspaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.extra.dataspaces.NamingService;
import org.objectweb.proactive.extra.dataspaces.SpaceInstanceInfo;
import org.objectweb.proactive.extra.dataspaces.SpacesDirectory;
import org.objectweb.proactive.extra.dataspaces.exceptions.ApplicationAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.WrongApplicationIdException;


/**
 * SpacesDirectoryAbstractBase impl and additional NamingService tests.
 */
public class NamingServiceTest extends SpacesDirectoryAbstractBase {

    private NamingService ns;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        ns = new NamingService();
    }

    /**
     * Normal case, two inputs, two outputs.
     */
    @Test
    public void testRegisterApplication1() throws ApplicationAlreadyRegisteredException,
            WrongApplicationIdException, SpaceAlreadyRegisteredException, IllegalArgumentException {

        Set<SpaceInstanceInfo> spaces = new HashSet<SpaceInstanceInfo>();

        spaces.add(spaceInstanceInput1);
        spaces.add(spaceInstanceInput2);
        spaces.add(spaceInstanceOutput1);
        spaces.add(spaceInstanceOutput2);

        ns.registerApplication(MAIN_APPID, spaces);

        // check if everything has been registered
        assertIsSpaceRegistered(spaceInstanceInput1);
        assertIsSpaceRegistered(spaceInstanceInput2);
        assertIsSpaceRegistered(spaceInstanceOutput1);
        assertIsSpaceRegistered(spaceInstanceOutput2);

        assertTrue(ns.unregister(spaceInstanceInput1.getMountingPoint()));
        assertTrue(ns.unregister(spaceInstanceOutput1.getMountingPoint()));
        ns.register(spaceInstanceInput1);
        ns.register(spaceInstanceOutput1);
    }

    /**
     * Normal case, no spaces
     */
    @Test
    public void testRegisterApplication2() throws ApplicationAlreadyRegisteredException,
            WrongApplicationIdException, SpaceAlreadyRegisteredException, IllegalArgumentException {

        ns.registerApplication(MAIN_APPID, null);
        ns.register(spaceInstanceInput1);
        ns.register(spaceInstanceOutput1);
        assertTrue(ns.unregister(spaceInstanceInput1.getMountingPoint()));
        assertTrue(ns.unregister(spaceInstanceOutput1.getMountingPoint()));
    }

    @Test
    public void testRegisterApplicationAlreadyRegistered() throws ApplicationAlreadyRegisteredException,
            WrongApplicationIdException, SpaceAlreadyRegisteredException, IllegalArgumentException {

        ns.registerApplication(MAIN_APPID, null);

        try {
            ns.registerApplication(MAIN_APPID, null);
            fail("Exception expected");
        } catch (ApplicationAlreadyRegisteredException e) {
        } catch (Exception e) {
            fail("Exception of different type expected");
        }
    }

    @Test
    public void testRegisterApplicationWrongAppid() {

        Set<SpaceInstanceInfo> spaces = new HashSet<SpaceInstanceInfo>();

        spaces.add(spaceInstanceInput1);
        spaces.add(spaceInstanceInput2);
        spaces.add(spaceInstanceOutput1);
        spaces.add(spaceInstanceOutput2);

        try {
            ns.registerApplication(ANOTHER_APPID1, spaces);
            fail("Exception expected");
        } catch (WrongApplicationIdException e) {
        } catch (Exception e) {
            fail("Exception of different type expected");
        }
    }

    @Test
    public void testUnregisterApplication() throws ApplicationAlreadyRegisteredException,
            WrongApplicationIdException {

        Set<SpaceInstanceInfo> spaces = new HashSet<SpaceInstanceInfo>();

        spaces.add(spaceInstanceInput1);
        spaces.add(spaceInstanceInput2);
        spaces.add(spaceInstanceOutput1);
        spaces.add(spaceInstanceOutput2);

        ns.registerApplication(MAIN_APPID, spaces);

        assertIsSpaceRegistered(spaceInstanceInput1);
        assertIsSpaceRegistered(spaceInstanceInput2);
        assertIsSpaceRegistered(spaceInstanceOutput1);
        assertIsSpaceRegistered(spaceInstanceOutput2);

        ns.unregisterApplication(MAIN_APPID);

        assertIsSpaceUnregistered(spaceInstanceInput1);
        assertIsSpaceUnregistered(spaceInstanceInput2);
        assertIsSpaceUnregistered(spaceInstanceOutput1);
        assertIsSpaceUnregistered(spaceInstanceOutput2);
    }

    @Test
    public void testUnregisterApplicationEmpty() throws ApplicationAlreadyRegisteredException,
            WrongApplicationIdException {

        Set<SpaceInstanceInfo> spaces = new HashSet<SpaceInstanceInfo>();

        ns.registerApplication(MAIN_APPID, spaces);
        ns.unregisterApplication(MAIN_APPID);
    }

    @Test
    public void testUnregisterApplicationWrongAppid() throws ApplicationAlreadyRegisteredException,
            WrongApplicationIdException {

        ns.registerApplication(MAIN_APPID, null);

        try {
            ns.unregisterApplication(ANOTHER_APPID1);
            fail("Exception expected");
        } catch (WrongApplicationIdException e) {
        } catch (Exception e) {
            fail("Exception of different type expected");
        }
    }

    /**
     * Register space of not registered application
     */
    @Test
    public void testNSRegister1() throws ApplicationAlreadyRegisteredException,
            SpaceAlreadyRegisteredException, IllegalArgumentException, WrongApplicationIdException {

        try {
            ns.register(spaceInstanceInput1b);
            fail();
        } catch (WrongApplicationIdException e) {
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Unregister space of not registered application
     */
    @Test
    public void testNSUnregister1() throws ApplicationAlreadyRegisteredException,
            SpaceAlreadyRegisteredException, IllegalArgumentException, WrongApplicationIdException {

        assertFalse(ns.unregister(spaceInstanceInput1b.getMountingPoint()));
    }

    private void assertIsSpaceRegistered(SpaceInstanceInfo expected) {
        SpaceInstanceInfo actual = ns.lookupFirst(expected.getMountingPoint());
        assertEquals(actual.getMountingPoint(), expected.getMountingPoint());
    }

    private void assertIsSpaceUnregistered(SpaceInstanceInfo expected) {
        assertNull(ns.lookupFirst(expected.getMountingPoint()));
    }

    @Override
    protected SpacesDirectory getSource() throws Exception {
        NamingService ns = new NamingService();

        ns.registerApplication(MAIN_APPID, null);
        ns.registerApplication(ANOTHER_APPID1, null);
        ns.registerApplication(ANOTHER_APPID2, null);

        return ns;
    }
}
