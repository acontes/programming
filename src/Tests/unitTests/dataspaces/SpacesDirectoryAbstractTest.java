/**
 * 
 */
package unitTests.dataspaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.extra.dataspaces.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.InputOutputSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.ScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.SpaceInstanceInfo;
import org.objectweb.proactive.extra.dataspaces.SpaceType;
import org.objectweb.proactive.extra.dataspaces.SpacesDirectory;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceAlreadyRegisteredException;

/**
 * 
 *
 */
public abstract class SpacesDirectoryAbstractTest {

	protected static final int MAIN_APPID = 1;

	protected static final int ANOTHER_APPID1 = 0;

	protected static final int ANOTHER_APPID2 = 2;

	private SpacesDirectory dir;

	protected SpaceInstanceInfo spaceInstanceInput1;

	protected SpaceInstanceInfo spaceInstanceInput1b;

	protected SpaceInstanceInfo spaceInstanceInput1c;

	protected SpaceInstanceInfo spaceInstanceInput2;

	protected SpaceInstanceInfo spaceInstanceOutput1;

	protected SpaceInstanceInfo spaceInstanceOutput1b;

	protected SpaceInstanceInfo spaceInstanceOutput2;

	protected SpaceInstanceInfo spaceInstanceScratch;

	@Before
	public void setUp() throws Exception {

		dir = getSource();

		InputOutputSpaceConfiguration configInput1 = InputOutputSpaceConfiguration
				.createInputSpaceConfiguration("http://hostA", "/tmp", "h1", "input1");
		InputOutputSpaceConfiguration configInput2 = InputOutputSpaceConfiguration
				.createInputSpaceConfiguration("http://hostB", "/tmp", "h1", "input2");
		InputOutputSpaceConfiguration configOutput1 = InputOutputSpaceConfiguration
				.createOutputSpaceConfiguration("http://hostC", "/tmp", "h1", "output1");
		ScratchSpaceConfiguration configScratch = ScratchSpaceConfiguration.createConfiguration(
				"http://hostD", "/tmp");
		InputOutputSpaceConfiguration configOutput2 = InputOutputSpaceConfiguration
				.createOutputSpaceConfiguration("http://hostA", "/tmp", "h1", "output2");

		spaceInstanceInput1 = new SpaceInstanceInfo(MAIN_APPID, configInput1);
		spaceInstanceInput1b = new SpaceInstanceInfo(ANOTHER_APPID1, configInput1);
		spaceInstanceInput1c = new SpaceInstanceInfo(ANOTHER_APPID2, configInput1);

		spaceInstanceInput2 = new SpaceInstanceInfo(MAIN_APPID, configInput2);

		spaceInstanceOutput1 = new SpaceInstanceInfo(MAIN_APPID, configOutput1);
		spaceInstanceOutput1b = new SpaceInstanceInfo(ANOTHER_APPID1, configOutput1);
		spaceInstanceOutput2 = new SpaceInstanceInfo(MAIN_APPID, configOutput2);

		spaceInstanceScratch = new SpaceInstanceInfo(MAIN_APPID, "node1", "rt1", configScratch);

		dir.register(spaceInstanceInput1b);
		dir.register(spaceInstanceInput1);
		dir.register(spaceInstanceInput1c);
		dir.register(spaceInstanceInput2);
		dir.register(spaceInstanceOutput1);
		dir.register(spaceInstanceScratch);
	}

	/**
	 * Get the Source to test
	 * 
	 * @throws Exception
	 */
	protected abstract SpacesDirectory getSource() throws Exception;

	@Test
	public void testRegister() {
		try {
			dir.register(spaceInstanceInput1b);
			fail("Exception expected");
		} catch (SpaceAlreadyRegisteredException e) {
		} catch (Exception e) {
			fail("Exception of different type expected");
		}
	}

	@Test
	public void testLookupFirst() {
		final DataSpacesURI query1 = DataSpacesURI.createInOutSpaceURI(MAIN_APPID, SpaceType.INPUT, "input1");
		final DataSpacesURI query2 = DataSpacesURI.createInOutSpaceURI(MAIN_APPID, SpaceType.INPUT, "input2");
		final DataSpacesURI query3 = DataSpacesURI.createInOutSpaceURI(MAIN_APPID, SpaceType.OUTPUT,
				"output1");

		final SpaceInstanceInfo actual1 = dir.lookupFirst(query1);
		final SpaceInstanceInfo actual2 = dir.lookupFirst(query2);
		final SpaceInstanceInfo actual3 = dir.lookupFirst(query3);

		assertEquals(spaceInstanceInput1, actual1);
		assertEquals(spaceInstanceInput2, actual2);
		assertEquals(spaceInstanceOutput1, actual3);
	}

	@Test
	public void testLookupAllInputs1() {
		final DataSpacesURI query = DataSpacesURI.createURI(MAIN_APPID, SpaceType.INPUT);
		final Set<SpaceInstanceInfo> expected = new HashSet<SpaceInstanceInfo>();
		final Set<SpaceInstanceInfo> actual = dir.lookupAll(query);

		expected.add(spaceInstanceInput1);
		expected.add(spaceInstanceInput2);

		assertEquals(expected, actual);
	}

	@Test
	public void testLookupAllInputs0() {
		final DataSpacesURI query = DataSpacesURI.createURI(ANOTHER_APPID1, SpaceType.INPUT);
		final Set<SpaceInstanceInfo> expected = new HashSet<SpaceInstanceInfo>();
		final Set<SpaceInstanceInfo> actual = dir.lookupAll(query);

		expected.add(spaceInstanceInput1b);

		assertEquals(expected, actual);
	}

	@Test
	public void testLookupAllForApp1() {
		final DataSpacesURI query = DataSpacesURI.createURI(MAIN_APPID);
		final Set<SpaceInstanceInfo> expected = new HashSet<SpaceInstanceInfo>();
		final Set<SpaceInstanceInfo> actual = dir.lookupAll(query);

		expected.add(spaceInstanceInput1);
		expected.add(spaceInstanceInput2);
		expected.add(spaceInstanceOutput1);
		expected.add(spaceInstanceScratch);

		assertEquals(expected, actual);
	}

	@Test
	public void testLookupAllForApp0() {
		final DataSpacesURI query = DataSpacesURI.createURI(ANOTHER_APPID1);
		final Set<SpaceInstanceInfo> expected = new HashSet<SpaceInstanceInfo>();
		final Set<SpaceInstanceInfo> actual = dir.lookupAll(query);

		expected.add(spaceInstanceInput1b);

		assertEquals(expected, actual);
	}

	@Test
	public void testLookupAllForApp2() {
		final DataSpacesURI query = DataSpacesURI.createURI(ANOTHER_APPID2);
		final Set<SpaceInstanceInfo> expected = new HashSet<SpaceInstanceInfo>();
		final Set<SpaceInstanceInfo> actual = dir.lookupAll(query);

		expected.add(spaceInstanceInput1c);

		assertEquals(expected, actual);
	}

	@Test
	public void testLookupAllIllegalArgumentException() {
		final DataSpacesURI query = DataSpacesURI.createScratchSpaceURI(1, "runtime1", "node1");

		try {
			dir.lookupAll(query);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail("Exception of different type expected");
		}
	}

	@Test
	public void testLookupFirstIllegalArgumentException1() {
		final DataSpacesURI query = DataSpacesURI.createURI(ANOTHER_APPID2);

		try {
			dir.lookupFirst(query);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail("Exception of different type expected");
		}
	}

	@Test
	public void testLookupFirstIllegalArgumentException2() {
		final DataSpacesURI query = DataSpacesURI.createInOutSpaceURI(MAIN_APPID, SpaceType.INPUT, "name1",
				"path");

		try {
			dir.lookupFirst(query);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail("Exception of different type expected");
		}
	}
}
