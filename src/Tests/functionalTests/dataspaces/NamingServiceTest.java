/**
 * 
 */
package functionalTests.dataspaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
 * SpacesDirectoryAbstractTest impl and additional NamingService tests.
 */
public class NamingServiceTest extends SpacesDirectoryAbstractTest {

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

	public void testRegisterApplicationAlreadyRegistered() throws ApplicationAlreadyRegisteredException,
			WrongApplicationIdException, SpaceAlreadyRegisteredException, IllegalArgumentException {

		ns.registerApplication(MAIN_APPID, null);

		try {
			ns.registerApplication(ANOTHER_APPID1, null);
			fail("Exception expected");
		} catch (ApplicationAlreadyRegisteredException e) {
		} catch (Exception e) {
			fail("Exception of different type expected");
		}
	}

	public void testRegisterApplicationWrongAppid() {

		Set<SpaceInstanceInfo> inputSpaces = new HashSet<SpaceInstanceInfo>();
		Set<SpaceInstanceInfo> outputSpaces = new HashSet<SpaceInstanceInfo>();

		inputSpaces.add(spaceInstanceInput1);
		inputSpaces.add(spaceInstanceInput2);
		inputSpaces.add(spaceInstanceOutput1);
		inputSpaces.add(spaceInstanceOutput2);

		try {
			ns.registerApplication(ANOTHER_APPID1, null);
			fail("Exception expected");
		} catch (WrongApplicationIdException e) {
		} catch (Exception e) {
			fail("Exception of different type expected");
		}
	}

	/**
	 * Register space of not registered application
	 */
	public void testNSRegister1() throws ApplicationAlreadyRegisteredException,
			SpaceAlreadyRegisteredException, IllegalArgumentException, WrongApplicationIdException {

		ns.registerApplication(MAIN_APPID, null);

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
	public void testNSUnregister1() throws ApplicationAlreadyRegisteredException,
			SpaceAlreadyRegisteredException, IllegalArgumentException, WrongApplicationIdException {

		ns.registerApplication(MAIN_APPID, null);
		assertFalse(ns.unregister(spaceInstanceInput1b.getMountingPoint()));
	}

	private void assertIsSpaceRegistered(SpaceInstanceInfo expected) {
		SpaceInstanceInfo actual = ns.lookupFirst(expected.getMountingPoint());
		assertEquals(actual.getMountingPoint(), expected.getMountingPoint());
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
