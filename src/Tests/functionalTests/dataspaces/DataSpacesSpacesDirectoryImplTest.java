package functionalTests.dataspaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.extra.dataspaces.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.SpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.SpaceInstanceInfo;
import org.objectweb.proactive.extra.dataspaces.SpaceType;
import org.objectweb.proactive.extra.dataspaces.SpacesDirectoryImpl;

public class DataSpacesSpacesDirectoryImplTest {

	private SpacesDirectoryImpl dir;

	private SpaceInstanceInfo spaceInstanceInfo1a;

	private SpaceInstanceInfo spaceInstanceInfo1b;

	private SpaceInstanceInfo spaceInstanceInfo1c;

	private SpaceInstanceInfo spaceInstanceInfo2;

	private SpaceInstanceInfo spaceInstanceInfo3;

	private SpaceInstanceInfo spaceInstanceInfo4;

	@Before
	public void setUp() {
		dir = new SpacesDirectoryImpl();

		SpaceConfiguration config1 = new SpaceConfiguration("http://hostA", "/tmp", "h1", SpaceType.INPUT,
				"input1");
		SpaceConfiguration config2 = new SpaceConfiguration("http://hostB", "/tmp", "h1", SpaceType.INPUT,
				"input2");
		SpaceConfiguration config3 = new SpaceConfiguration("http://hostC", "/tmp", "h1", SpaceType.OUTPUT,
				"output1");
		SpaceConfiguration config4 = new SpaceConfiguration("http://hostD", "/tmp", "h1", SpaceType.SCRATCH,
				null);

		spaceInstanceInfo1a = new SpaceInstanceInfo(0, config1);
		spaceInstanceInfo1b = new SpaceInstanceInfo(1, config1);
		spaceInstanceInfo1c = new SpaceInstanceInfo(2, config1);
		spaceInstanceInfo2 = new SpaceInstanceInfo(1, config2);
		spaceInstanceInfo3 = new SpaceInstanceInfo(1, config3);
		spaceInstanceInfo4 = new SpaceInstanceInfo(1, "node1", "rt1", config4);

		dir.register(spaceInstanceInfo1a);
		dir.register(spaceInstanceInfo1b);
		dir.register(spaceInstanceInfo1c);
		dir.register(spaceInstanceInfo2);
		dir.register(spaceInstanceInfo3);
		dir.register(spaceInstanceInfo4);
	}

	@Test
	public void testRegister() {
		try {
			dir.register(spaceInstanceInfo1a);
			fail("Exception expected");
		} catch (Exception e) {
		}
	}

	@Test
	public void testLookupFirst() {
		final DataSpacesURI query1 = DataSpacesURI.createInOutSpaceURI(1, SpaceType.INPUT, "input1");
		final DataSpacesURI query2 = DataSpacesURI.createInOutSpaceURI(1, SpaceType.INPUT, "input2");
		final DataSpacesURI query3 = DataSpacesURI.createInOutSpaceURI(1, SpaceType.OUTPUT, "output1");

		final SpaceInstanceInfo actual1 = dir.lookupFirst(query1);
		final SpaceInstanceInfo actual2 = dir.lookupFirst(query2);
		final SpaceInstanceInfo actual3 = dir.lookupFirst(query3);

		assertEquals(spaceInstanceInfo1b, actual1);
		assertEquals(spaceInstanceInfo2, actual2);
		assertEquals(spaceInstanceInfo3, actual3);
	}

	@Test
	public void testLookupAllInputs1() {
		final DataSpacesURI query = DataSpacesURI.createURI(1, SpaceType.INPUT);
		final Set<SpaceInstanceInfo> expected = new HashSet<SpaceInstanceInfo>();
		final Set<SpaceInstanceInfo> actual = dir.lookupAll(query);

		expected.add(spaceInstanceInfo1b);
		expected.add(spaceInstanceInfo2);

		assertEquals(expected, actual);
	}

	@Test
	public void testLookupAllInputs0() {
		final DataSpacesURI query = DataSpacesURI.createURI(0, SpaceType.INPUT);
		final Set<SpaceInstanceInfo> expected = new HashSet<SpaceInstanceInfo>();
		final Set<SpaceInstanceInfo> actual = dir.lookupAll(query);

		expected.add(spaceInstanceInfo1a);

		assertEquals(expected, actual);
	}

	@Test
	public void testLookupAllForApp1() {
		final DataSpacesURI query = DataSpacesURI.createURI(1);
		final Set<SpaceInstanceInfo> expected = new HashSet<SpaceInstanceInfo>();
		final Set<SpaceInstanceInfo> actual = dir.lookupAll(query);

		expected.add(spaceInstanceInfo1b);
		expected.add(spaceInstanceInfo2);
		expected.add(spaceInstanceInfo3);
		expected.add(spaceInstanceInfo4);

		assertEquals(expected, actual);
	}

	@Test
	public void testLookupAllForApp0() {
		final DataSpacesURI query = DataSpacesURI.createURI(0);
		final Set<SpaceInstanceInfo> expected = new HashSet<SpaceInstanceInfo>();
		final Set<SpaceInstanceInfo> actual = dir.lookupAll(query);

		expected.add(spaceInstanceInfo1a);

		assertEquals(expected, actual);
	}

	@Test
	public void testLookupAllForApp2() {
		final DataSpacesURI query = DataSpacesURI.createURI(2);
		final Set<SpaceInstanceInfo> expected = new HashSet<SpaceInstanceInfo>();
		final Set<SpaceInstanceInfo> actual = dir.lookupAll(query);

		expected.add(spaceInstanceInfo1c);

		assertEquals(expected, actual);
	}

	@Test
	public void testLookupAllIllegalArgumentException() {
		final DataSpacesURI query = DataSpacesURI.createScratchSpaceURI(1, "runtime1", "node1");

		try {
			dir.lookupAll(query);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testLookupFirstIllegalArgumentException1() {
		final DataSpacesURI query = DataSpacesURI.createURI(2);

		try {
			dir.lookupFirst(query);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testLookupFirstIllegalArgumentException2() {
		final DataSpacesURI query = DataSpacesURI.createInOutSpaceURI(1, SpaceType.INPUT, "name1", "path");

		try {
			dir.lookupFirst(query);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
		}
	}
}
