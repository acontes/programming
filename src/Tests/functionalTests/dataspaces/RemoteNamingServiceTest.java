/**
 * Remember to set: <code>-Dproactive.home= -Djava.security.policy=</code>
 */
package functionalTests.dataspaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.naming.ConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.api.PARemoteObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.remoteobject.RemoteObjectExposer;
import org.objectweb.proactive.extra.dataspaces.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.NamingService;
import org.objectweb.proactive.extra.dataspaces.SpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.SpaceInstanceInfo;
import org.objectweb.proactive.extra.dataspaces.SpaceType;
import org.objectweb.proactive.extra.dataspaces.Utils;
import org.objectweb.proactive.extra.dataspaces.exceptions.ApplicationAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceAlreadyRegisteredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.WrongApplicationIdException;

import functionalTests.GCMFunctionalTestDefaultNodes;

/**
 * Test NamingService on the local default node.
 */
public class RemoteNamingServiceTest extends GCMFunctionalTestDefaultNodes {

	private static final String NAME = "DSnamingservice";

	private NamingService stub;

	private RemoteObjectExposer<NamingService> roe;

	protected static final int MAIN_APPID = 1;

	protected static final int ANOTHER_APPID1 = 0;

	protected static final int ANOTHER_APPID2 = 2;

	protected SpaceInstanceInfo spaceInstanceInput1;

	protected SpaceInstanceInfo spaceInstanceInput1b;

	protected SpaceInstanceInfo spaceInstanceInput1c;

	protected SpaceInstanceInfo spaceInstanceInput2;

	protected SpaceInstanceInfo spaceInstanceOutput1;

	protected SpaceInstanceInfo spaceInstanceOutput1b;

	protected SpaceInstanceInfo spaceInstanceOutput2;

	protected SpaceInstanceInfo spaceInstanceScratch;

	public RemoteNamingServiceTest() throws ConfigurationException {
		super(1, 1);

		SpaceConfiguration configInput1 = new SpaceConfiguration("http://hostA", "/tmp", "h1",
				SpaceType.INPUT, "input1");
		SpaceConfiguration configInput2 = new SpaceConfiguration("http://hostB", "/tmp", "h1",
				SpaceType.INPUT, "input2");
		SpaceConfiguration configOutput1 = new SpaceConfiguration("http://hostC", "/tmp", "h1",
				SpaceType.OUTPUT, "output1");
		SpaceConfiguration configScratch = new SpaceConfiguration("http://hostD", "/tmp", "h1",
				SpaceType.SCRATCH, null);
		SpaceConfiguration configOutput2 = new SpaceConfiguration("http://hostA", "/tmp", "h1",
				SpaceType.OUTPUT, "output2");

		spaceInstanceInput1 = new SpaceInstanceInfo(MAIN_APPID, configInput1);
		spaceInstanceInput1b = new SpaceInstanceInfo(ANOTHER_APPID1, configInput1);
		spaceInstanceInput1c = new SpaceInstanceInfo(ANOTHER_APPID2, configInput1);

		spaceInstanceInput2 = new SpaceInstanceInfo(MAIN_APPID, configInput2);

		spaceInstanceOutput1 = new SpaceInstanceInfo(MAIN_APPID, configOutput1);
		spaceInstanceOutput1b = new SpaceInstanceInfo(ANOTHER_APPID1, configOutput1);
		spaceInstanceOutput2 = new SpaceInstanceInfo(MAIN_APPID, configOutput2);

		spaceInstanceScratch = new SpaceInstanceInfo(MAIN_APPID, "node1", "rt1", configScratch);
	}

	@Before
	public void before() throws ProActiveException, URISyntaxException {
		if (stub == null) {
			NamingService ns = new NamingService();

			roe = PARemoteObject.newRemoteObject(NamingService.class.getName(), ns);
			roe.createRemoteObject(NAME);
			final String url = roe.getURL();
			stub = Utils.createNamingServiceStub(url);

			// RemoteObjectHelper.generatedObjectStub(roe.getRemoteObject());
		}
	}

	@Test
	public void test() throws ApplicationAlreadyRegisteredException, WrongApplicationIdException,
			SpaceAlreadyRegisteredException, IllegalArgumentException {

		Set<SpaceInstanceInfo> spaces = new HashSet<SpaceInstanceInfo>();

		spaces.add(spaceInstanceInput1);
		spaces.add(spaceInstanceInput2);
		spaces.add(spaceInstanceOutput1);
		spaces.add(spaceInstanceOutput2);

		// TEST REGISTER APP
		stub.registerApplication(MAIN_APPID, spaces);

		// check if everything has been registered
		// TEST LOOKUP FIRST
		assertIsSpaceRegistered(spaceInstanceInput1);
		assertIsSpaceRegistered(spaceInstanceInput2);
		assertIsSpaceRegistered(spaceInstanceOutput1);
		assertIsSpaceRegistered(spaceInstanceOutput2);

		// TEST LOOKUP ALL
		final DataSpacesURI query = DataSpacesURI.createURI(MAIN_APPID);
		final Set<SpaceInstanceInfo> actual = stub.lookupAll(query);
		assertEquals(spaces, actual);

		// TEST UNREGISTER
		assertTrue(stub.unregister(spaceInstanceInput1.getMountingPoint()));
		assertTrue(stub.unregister(spaceInstanceOutput1.getMountingPoint()));

		// TEST LOOKUP FIRST WITH NULL ANSWER
		assertIsSpaceUnregistered(spaceInstanceInput1);
		assertIsSpaceUnregistered(spaceInstanceOutput1);

		// TEST REGISTER
		stub.register(spaceInstanceInput1);
		stub.register(spaceInstanceOutput1);

		// TEST EXCEPTION WHEN SPACE ALREADY REGISTERED
		try {
			stub.register(spaceInstanceInput1);
			fail("Exception expected");
		} catch (SpaceAlreadyRegisteredException e) {
		} catch (Exception e) {
			fail("Expected exception of different type");
		}

		// TEST EXCEPTION WHEN APP NOT REGISTERED
		try {
			stub.register(spaceInstanceInput1b);
			fail("Exception expected");
		} catch (WrongApplicationIdException e) {
		} catch (Exception e) {
			fail("Expected exception of different type");
		}

		// TEST EXCEPTION WHEN APP ALREADY REGISTERED
		try {
			stub.registerApplication(MAIN_APPID, null);
			fail("Exception expected");
		} catch (ApplicationAlreadyRegisteredException e) {
		} catch (Exception e) {
			fail("Expected exception of different type");
		}

		// TEST UNREGISTER APP
		stub.unregisterApplication(MAIN_APPID);
	}

	private void assertIsSpaceRegistered(SpaceInstanceInfo expected) {
		SpaceInstanceInfo actual = stub.lookupFirst(expected.getMountingPoint());
		assertEquals(actual.getMountingPoint(), expected.getMountingPoint());
	}

	private void assertIsSpaceUnregistered(SpaceInstanceInfo expected) {
		assertNull(stub.lookupFirst(expected.getMountingPoint()));
	}
}
