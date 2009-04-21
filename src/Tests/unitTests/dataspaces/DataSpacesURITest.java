package unitTests.dataspaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.objectweb.proactive.extra.dataspaces.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.SpaceType;
import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;

public class DataSpacesURITest {
	private DataSpacesURI uri;
	private DataSpacesURI uri2;

	@Test
	public void testCreateURIApp() {
		uri = DataSpacesURI.createURI(123);

		assertEquals(123, uri.getAppId());
		assertNull(uri.getSpaceType());
		assertNull(uri.getName());
		assertNull(uri.getRuntimeId());
		assertNull(uri.getNodeId());
		assertNull(uri.getPath());
		assertFalse(uri.isComplete());
	}

	@Test
	public void testCreateURIAppType() {
		uri = DataSpacesURI.createURI(123, SpaceType.SCRATCH);

		assertEquals(123, uri.getAppId());
		assertEquals(SpaceType.SCRATCH, uri.getSpaceType());
		assertNull(uri.getName());
		assertNull(uri.getRuntimeId());
		assertNull(uri.getNodeId());
		assertNull(uri.getPath());
		assertFalse(uri.isComplete());
	}

	@Test
	public void testCreateScratchSpaceURIAppRuntime() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA");

		assertEquals(123, uri.getAppId());
		assertEquals(SpaceType.SCRATCH, uri.getSpaceType());
		assertEquals("runtimeA", uri.getRuntimeId());
		assertNull(uri.getName());
		assertNull(uri.getNodeId());
		assertNull(uri.getPath());
		assertFalse(uri.isComplete());
	}

	@Test
	public void testCreateScratchSpaceURIAppRuntimeNode() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB");

		assertEquals(123, uri.getAppId());
		assertEquals(SpaceType.SCRATCH, uri.getSpaceType());
		assertEquals("runtimeA", uri.getRuntimeId());
		assertEquals("nodeB", uri.getNodeId());
		assertNull(uri.getName());
		assertNull(uri.getPath());
		assertTrue(uri.isComplete());
	}

	@Test
	public void testCreateScratchSpaceURIAppNoRuntimeNodeNoPath() {
		try {
			uri = DataSpacesURI.createScratchSpaceURI(123, null, "nodeB");
			fail("expected exception");
		} catch (IllegalArgumentException x) {
		}
	}

	@Test
	public void testCreateScratchSpaceURIAppRuntimeNodePath() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB", "dir/abc.txt");

		assertEquals(123, uri.getAppId());
		assertEquals(SpaceType.SCRATCH, uri.getSpaceType());
		assertEquals("runtimeA", uri.getRuntimeId());
		assertEquals("nodeB", uri.getNodeId());
		assertEquals("dir/abc.txt", uri.getPath());
		assertNull(uri.getName());
		assertTrue(uri.isComplete());
	}

	@Test
	public void testCreateScratchSpaceURIAppNoRuntimeNodePath() {
		try {
			uri = DataSpacesURI.createScratchSpaceURI(123, null, "nodeB", "dir/abc.txt");
			fail("expected exception");
		} catch (IllegalArgumentException x) {
		}
	}

	@Test
	public void testCreateScratchSpaceURIAppRuntimeNoNodePath() {
		try {
			uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", null, "dir/abc.txt");
			fail("expected exception");
		} catch (IllegalArgumentException x) {
		}
	}

	@Test
	public void testCreateScratchSpaceURIAppNoRuntimeNoNodePath() {
		try {
			uri = DataSpacesURI.createScratchSpaceURI(123, null, null, "dir/abc.txt");
			fail("expected exception");
		} catch (IllegalArgumentException x) {
		}
	}

	@Test
	public void testCreateInOutSpaceURIAppTypeName1() {
		uri = DataSpacesURI.createInOutSpaceURI(123, SpaceType.INPUT, "stats");

		assertEquals(123, uri.getAppId());
		assertEquals(SpaceType.INPUT, uri.getSpaceType());
		assertEquals("stats", uri.getName());
		assertNull(uri.getRuntimeId());
		assertNull("nodeB", uri.getNodeId());
		assertNull(uri.getPath());
		assertTrue(uri.isComplete());
	}

	@Test
	public void testCreateInOutSpaceURIAppTypeName2() {
		uri = DataSpacesURI.createInOutSpaceURI(123, SpaceType.OUTPUT, "stats");

		assertEquals(123, uri.getAppId());
		assertEquals(SpaceType.OUTPUT, uri.getSpaceType());
		assertEquals("stats", uri.getName());
		assertNull(uri.getRuntimeId());
		assertNull("nodeB", uri.getNodeId());
		assertNull(uri.getPath());
		assertTrue(uri.isComplete());
	}

	@Test
	public void testCreateInOutSpaceURIAppWrongTypeName() {
		try {
			uri = DataSpacesURI.createInOutSpaceURI(123, SpaceType.SCRATCH, "stats");
			fail("expected exception");
		} catch (IllegalArgumentException x) {
		}
	}

	@Test
	public void testCreateInOutSpaceURIAppNoTypeName() {
		try {
			uri = DataSpacesURI.createInOutSpaceURI(123, null, "stats");
			fail("expected exception");
		} catch (IllegalArgumentException x) {
		}
	}

	@Test
	public void testCreateInOutSpaceURIAppTypeNamePath() {
		uri = DataSpacesURI.createInOutSpaceURI(123, SpaceType.OUTPUT, "stats", "dir/abc.txt");

		assertEquals(123, uri.getAppId());
		assertEquals(SpaceType.OUTPUT, uri.getSpaceType());
		assertEquals("stats", uri.getName());
		assertEquals("dir/abc.txt", uri.getPath());
		assertNull(uri.getRuntimeId());
		assertNull("nodeB", uri.getNodeId());
		assertTrue(uri.isComplete());
	}

	@Test
	public void testCreateInOutSpaceURIAppTypeNoNamePath() {
		try {
			uri = DataSpacesURI.createInOutSpaceURI(123, SpaceType.OUTPUT, null, "dir/abc.txt");
			fail("expected exception");
		} catch (IllegalArgumentException x) {
		}
	}

	@Test
	public void testCreateInOutSpaceURIAppNoTypeNoNamePath() {
		try {
			uri = DataSpacesURI.createInOutSpaceURI(123, null, null, "dir/abc.txt");
			fail("expected exception");
		} catch (IllegalArgumentException x) {
		}
	}

	private String slash(boolean slashBoolean) {
		if (slashBoolean)
			return "/";
		return "";
	}

	private void testParseURIApp(boolean slash) throws MalformedURIException {
		uri = DataSpacesURI.parseURI("vfs:///123" + slash(slash));

		assertEquals(123, uri.getAppId());
		assertNull(uri.getSpaceType());
		assertNull(uri.getName());
		assertNull(uri.getRuntimeId());
		assertNull(uri.getNodeId());
		assertNull(uri.getPath());
	}

	@Test
	public void testParseURIAppSlash() throws MalformedURIException {
		testParseURIApp(true);
	}

	@Test
	public void testParseURIAppNoSlash() throws MalformedURIException {
		testParseURIApp(false);
	}

	private void testParseURIBadApp(boolean slash) {
		try {
			uri = DataSpacesURI.parseURI("vfs:///abc" + slash(slash));
			fail("expected exception");
		} catch (MalformedURIException x) {
		}
	}

	@Test
	public void testParseURIBadAppNoSlash() {
		testParseURIBadApp(false);
	}

	@Test
	public void testParseURIBadAppSlash() {
		testParseURIBadApp(true);
	}

	private void testParseURIAppType(boolean slash) throws MalformedURIException {
		uri = DataSpacesURI.parseURI("vfs:///123/input" + slash(slash));

		assertEquals(123, uri.getAppId());
		assertEquals(SpaceType.INPUT, uri.getSpaceType());
		assertNull(uri.getName());
		assertNull(uri.getRuntimeId());
		assertNull(uri.getNodeId());
		assertNull(uri.getPath());
	}

	@Test
	public void testParseURIAppTypeSlash() throws MalformedURIException {
		testParseURIAppType(true);
	}

	@Test
	public void testParseURIAppTypeNoSlash() throws MalformedURIException {
		testParseURIAppType(false);
	}

	private void testParseURIAppTypeName(boolean slash) throws MalformedURIException {
		uri = DataSpacesURI.parseURI("vfs:///123/input/abc" + slash(slash));

		assertEquals(123, uri.getAppId());
		assertEquals(SpaceType.INPUT, uri.getSpaceType());
		assertEquals("abc", uri.getName());
		assertNull(uri.getRuntimeId());
		assertNull(uri.getNodeId());
		assertNull(uri.getPath());
	}

	@Test
	public void testParseURIAppTypeNameSlash() throws MalformedURIException {
		testParseURIAppTypeName(true);
	}

	@Test
	public void testParseURIAppTypeNameNoSlash() throws MalformedURIException {
		testParseURIAppTypeName(false);
	}

	@Test
	public void testParseURIAppTypeNamePath() throws MalformedURIException {
		uri = DataSpacesURI.parseURI("vfs:///123/input/abc/file.txt");

		assertEquals(123, uri.getAppId());
		assertEquals(SpaceType.INPUT, uri.getSpaceType());
		assertEquals("abc", uri.getName());
		assertEquals("file.txt", uri.getPath());
		assertNull(uri.getRuntimeId());
		assertNull(uri.getNodeId());
	}

	private void testParseURIAppTypeRuntime(boolean slash) throws MalformedURIException {
		uri = DataSpacesURI.parseURI("vfs:///123/scratch/runtimeA" + slash(slash));

		assertEquals(123, uri.getAppId());
		assertEquals(SpaceType.SCRATCH, uri.getSpaceType());
		assertEquals("runtimeA", uri.getRuntimeId());
		assertNull(uri.getName());
		assertNull(uri.getNodeId());
		assertNull(uri.getPath());
	}

	@Test
	public void testParseURIAppTypeRuntimeSlash() throws MalformedURIException {
		testParseURIAppTypeRuntime(true);
	}

	@Test
	public void testParseURIAppTypeRuntimeNoSlash() throws MalformedURIException {
		testParseURIAppTypeRuntime(false);
	}

	private void testParseURIAppTypeRuntimeNode(boolean slash) throws MalformedURIException {
		uri = DataSpacesURI.parseURI("vfs:///123/scratch/runtimeA/nodeB" + slash(slash));

		assertEquals(123, uri.getAppId());
		assertEquals(SpaceType.SCRATCH, uri.getSpaceType());
		assertEquals("runtimeA", uri.getRuntimeId());
		assertEquals("nodeB", uri.getNodeId());
		assertNull(uri.getName());
		assertNull(uri.getPath());
	}

	@Test
	public void testParseURIAppTypeRuntimeNodeSlash() throws MalformedURIException {
		testParseURIAppTypeRuntimeNode(true);
	}

	@Test
	public void testParseURIAppTypeRuntimeNodeNoSlash() throws MalformedURIException {
		testParseURIAppTypeRuntimeNode(false);
	}

	@Test
	public void testParseURIAppTypeRuntimeNodePath() throws MalformedURIException {
		uri = DataSpacesURI.parseURI("vfs:///123/scratch/runtimeA/nodeB/file.txt");

		assertEquals(123, uri.getAppId());
		assertEquals(SpaceType.SCRATCH, uri.getSpaceType());
		assertEquals("runtimeA", uri.getRuntimeId());
		assertEquals("nodeB", uri.getNodeId());
		assertEquals("file.txt", uri.getPath());
		assertNull(uri.getName());
	}

	@Test
	public void testParseURIAppBadType() {
		try {
			uri = DataSpacesURI.parseURI("vfs:///123/abc/");
			fail("expected exception");
		} catch (MalformedURIException x) {
		}
	}

	@Test
	public void testWithPath() {
		uri = DataSpacesURI.createInOutSpaceURI(123, SpaceType.OUTPUT, "123", "dir/abc.txt");
		uri2 = uri.withPath("xyz");

		assertEquals("xyz", uri2.getPath());
		assertEquals("dir/abc.txt", uri.getPath());
	}

	@Test
	public void testWithPathEmpty() {
		uri = DataSpacesURI.createInOutSpaceURI(123, SpaceType.OUTPUT, "123", "dir/abc.txt");
		uri2 = uri.withPath(null);

		assertNull(uri2.getPath());
		assertEquals("dir/abc.txt", uri.getPath());
	}

	@Test
	public void testWithPathIllegal() {
		uri = DataSpacesURI.createURI(123, SpaceType.OUTPUT);
		try {
			uri.withPath("xyz");
			fail("expected exception");
		} catch (IllegalStateException x) {
		}
	}

	@Test
	public void testToStringCompleteNoPath() {
		uri = DataSpacesURI.createInOutSpaceURI(123, SpaceType.OUTPUT, "abc");
		assertEquals("vfs:///123/output/abc/", uri.toString());
	}

	@Test
	public void testToStringCompletePath() {
		uri = DataSpacesURI.createInOutSpaceURI(123, SpaceType.OUTPUT, "abc", "dir/abc.txt");
		assertEquals("vfs:///123/output/abc/dir/abc.txt", uri.toString());
	}

	@Test
	public void testToStringIncompleteApp() {
		uri = DataSpacesURI.createURI(123);
		assertEquals("vfs:///123/", uri.toString());
	}

	@Test
	public void testToStringIncompleteAppType1() {
		uri = DataSpacesURI.createURI(123, SpaceType.INPUT);
		assertEquals("vfs:///123/input/", uri.toString());
	}

	@Test
	public void testToStringIncompleteAppType2() {
		uri = DataSpacesURI.createURI(123, SpaceType.SCRATCH);
		assertEquals("vfs:///123/scratch/", uri.toString());
	}

	@Test
	public void testToStringIncompleteAppTypeRuntime() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA");
		assertEquals("vfs:///123/scratch/runtimeA/", uri.toString());
	}

	@Test
	public void testToStringIncompleteAppTypeRuntimeNode() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB");
		assertEquals("vfs:///123/scratch/runtimeA/nodeB/", uri.toString());
	}

	private void assertURIGreaterThanURI2() {
		assertTrue(uri.compareTo(uri2) > 0);
		assertTrue(uri2.compareTo(uri) < 0);
	}

	private void assertURIEqualURI2() {
		assertTrue(uri.compareTo(uri2) == 0);
		assertTrue(uri2.compareTo(uri) == 0);
	}

	@Test
	public void testCompareToDifferentLevelsPathVsNode() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB", "abc.txt");
		uri2 = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB");
		assertURIGreaterThanURI2();
	}

	@Test
	public void testCompareToDifferentLevelsNodeVsRuntime() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB");
		uri2 = DataSpacesURI.createScratchSpaceURI(123, "runtimeA");
		assertURIGreaterThanURI2();
	}

	@Test
	public void testCompareToDifferentLevelsRuntimeVsType() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA");
		uri2 = DataSpacesURI.createURI(123, SpaceType.SCRATCH);
		assertURIGreaterThanURI2();
	}

	@Test
	public void testCompareToDifferentLevelsNameVsType() {
		uri = DataSpacesURI.createInOutSpaceURI(123, SpaceType.INPUT, "name");
		uri2 = DataSpacesURI.createURI(123, SpaceType.INPUT);
		assertURIGreaterThanURI2();
	}

	@Test
	public void testCompareToDifferentLevelsTypeVsApp() {
		uri = DataSpacesURI.createURI(123, SpaceType.SCRATCH);
		uri2 = DataSpacesURI.createURI(123);
		assertURIGreaterThanURI2();
	}

	@Test
	public void testCompareToDifferentLevelsPathVsApp() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB", "abc.txt");
		uri2 = DataSpacesURI.createURI(123);
		assertURIGreaterThanURI2();
	}

	@Test
	public void testCompareToSameLevelsDiffPath() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB", "abc.txt2");
		uri2 = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB", "abc.txt");
		assertURIGreaterThanURI2();
	}

	@Test
	public void testCompareToSameLevelsDiffPath2() {
		uri = DataSpacesURI.createInOutSpaceURI(123, SpaceType.INPUT, "name", "abc.txt2");
		uri2 = DataSpacesURI.createInOutSpaceURI(123, SpaceType.INPUT, "name", "abc.txt");
		assertURIGreaterThanURI2();
	}

	@Test
	public void testCompareToSameLevelsDiffNode() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB2", "abc.txt");
		uri2 = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB", "abc.txt");
		assertURIGreaterThanURI2();
	}

	@Test
	public void testCompareToSameLevelsDiffRuntime() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA2", "nodeB", "abc.txt");
		uri2 = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB", "abc.txt");
		assertURIGreaterThanURI2();
	}

	@Test
	public void testCompareToSameLevelsDiffName() {
		uri = DataSpacesURI.createInOutSpaceURI(123, SpaceType.INPUT, "name2");
		uri2 = DataSpacesURI.createInOutSpaceURI(123, SpaceType.INPUT, "name");
		assertURIGreaterThanURI2();
	}

	@Test
	public void testCompareToSameLevelsDiffType() {
		uri = DataSpacesURI.createInOutSpaceURI(123, SpaceType.OUTPUT, "name", "abc.txt");
		uri2 = DataSpacesURI.createInOutSpaceURI(123, SpaceType.INPUT, "name", "abc.txt");
		assertURIGreaterThanURI2();
	}

	@Test
	public void testCompareToSameLevelsDiffApp() {
		uri = DataSpacesURI.createInOutSpaceURI(124, SpaceType.INPUT, "name", "abc.txt");
		uri2 = DataSpacesURI.createInOutSpaceURI(123, SpaceType.INPUT, "name", "abc.txt");
		assertURIGreaterThanURI2();
	}

	@Test
	public void testCompareToSameApp() {
		uri = DataSpacesURI.createURI(123);
		uri2 = DataSpacesURI.createURI(123);
		assertURIEqualURI2();
	}

	@Test
	public void testCompareToSameAppType1() {
		uri = DataSpacesURI.createURI(123, SpaceType.INPUT);
		uri2 = DataSpacesURI.createURI(123, SpaceType.INPUT);
		assertURIEqualURI2();
	}

	@Test
	public void testCompareToSameAppType2() {
		uri = DataSpacesURI.createURI(123, SpaceType.INPUT);
		uri2 = DataSpacesURI.createURI(123, SpaceType.INPUT);
		assertURIEqualURI2();
	}

	@Test
	public void testCompareToSameAppTypeName() {
		uri = DataSpacesURI.createInOutSpaceURI(123, SpaceType.INPUT, "name");
		uri2 = DataSpacesURI.createInOutSpaceURI(123, SpaceType.INPUT, "name");
		assertURIEqualURI2();
	}

	@Test
	public void testCompareToSameAppTypeNamePath() {
		uri = DataSpacesURI.createInOutSpaceURI(123, SpaceType.INPUT, "name", "file.txt");
		uri2 = DataSpacesURI.createInOutSpaceURI(123, SpaceType.INPUT, "name", "file.txt");
		assertURIEqualURI2();
	}

	@Test
	public void testCompareToSameAppTypeRuntime() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA");
		uri2 = DataSpacesURI.createScratchSpaceURI(123, "runtimeA");
		assertURIEqualURI2();
	}

	@Test
	public void testCompareToSameAppTypeRuntimeNode() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB");
		uri2 = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB");
		assertURIEqualURI2();
	}

	@Test
	public void testCompareToSameAppTypeRuntimeNodePath() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB", "file.txt");
		uri2 = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB", "file.txt");
		assertURIEqualURI2();
	}

	@Test
	public void testEqualsPositive() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB");
		uri2 = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB");
		assertEquals(uri, uri2);
	}

	@Test
	public void testEqualsNegative() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB");
		uri2 = DataSpacesURI.createScratchSpaceURI(123, "runtimeA");
		assertFalse(uri.equals(uri2));
	}

	@Test
	public void testHashCode() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB");
		uri2 = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB");
		assertEquals(uri.hashCode(), uri2.hashCode());
	}

	@Test
	public void testNextURIApp() {
		uri = DataSpacesURI.createURI(123);
		uri2 = uri.nextURI();

		assertEquals(124, uri2.getAppId());
	}

	@Test
	public void testNextURIAppType() {
		uri = DataSpacesURI.createURI(123, SpaceType.INPUT);
		uri2 = uri.nextURI();

		assertEquals(123, uri2.getAppId());
		assertEquals(SpaceType.OUTPUT, uri2.getSpaceType());
	}

	@Test
	public void testNextURIAppTypeLast() {
		uri = DataSpacesURI.createURI(123, SpaceType.SCRATCH);
		uri2 = uri.nextURI();

		assertEquals(124, uri2.getAppId());
		assertNull(uri2.getSpaceType());
	}

	@Test
	public void testNextURIAppTypeRuntime() {
		uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA");
		uri2 = uri.nextURI();

		assertEquals(123, uri2.getAppId());
		assertEquals(SpaceType.SCRATCH, uri2.getSpaceType());
		assertEquals("runtimeA\0", uri2.getRuntimeId());
	}

	@Test
	public void testNextURIComplete() {
		try {
			uri = DataSpacesURI.createScratchSpaceURI(123, "runtimeA", "nodeB", "abc.txt");
			uri.nextURI();
			fail("expected exception");
		} catch (IllegalStateException x) {
		}
	}
}
