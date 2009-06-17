package unitTests.dataspaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extra.dataspaces.Utils;
import org.objectweb.proactive.extra.dataspaces.core.ApplicationScratchSpace;
import org.objectweb.proactive.extra.dataspaces.core.BaseScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.core.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.core.NodeScratchSpace;
import org.objectweb.proactive.extra.dataspaces.core.SpaceInstanceInfo;
import org.objectweb.proactive.extra.dataspaces.core.SpaceType;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.vfs.VFSFactory;
import org.objectweb.proactive.extra.dataspaces.vfs.VFSNodeScratchSpaceImpl;

import unitTests.dataspaces.mock.MOCKBody;
import unitTests.dataspaces.mock.MOCKNode;


/**
 * Tests for {@link ApplicationScratchSpace} implementation from {@link VFSNodeScratchSpaceImpl}.
 * Uses MOCK objects for Body implementation.
 */
public class VFSApplicationScratchSpaceImplTest {

    private static final String NODE_ID = "node_id";
    private static final String RUNTIME_ID = "rt_id";
    private static final String ACCESS_URL = "/";
    private static final long APP_ID_LONG = 0;
    private static final String APP_ID = new Long(APP_ID_LONG).toString();

    private File testDir;
    private Node node;
    private String testDirPath;
    private boolean configured;
    private BaseScratchSpaceConfiguration localAccessConfig;
    private NodeScratchSpace nodeScratchSpace;
    private ApplicationScratchSpace applicationScratchSpace;
    private Body body;
    private String activeObjectId;
    private String scratchDataSpacePath;
    private File file;
    private File dir;
    private static DefaultFileSystemManager fileSystemManager;
    private String scratchPath;

    @BeforeClass
    static public void init() throws FileSystemException {
        fileSystemManager = VFSFactory.createDefaultFileSystemManager();
    }

    @AfterClass
    static public void close() {
        fileSystemManager.close();
    }

    @Before
    public void setUp() throws ConfigurationException, IOException {
        testDir = new File(System.getProperty("java.io.tmpdir"),
            "ProActive-VFSApplicationScratchSpaceImplTest");
        assertTrue(testDir.mkdir());
        testDirPath = testDir.getCanonicalPath();
        scratchDataSpacePath = Utils.appendSubDirs(testDirPath, RUNTIME_ID, NODE_ID, APP_ID);

        node = new MOCKNode(RUNTIME_ID, NODE_ID, APP_ID_LONG);
        body = new MOCKBody();
        localAccessConfig = new BaseScratchSpaceConfiguration(ACCESS_URL, testDirPath);
        nodeScratchSpace = new VFSNodeScratchSpaceImpl();

        nodeScratchSpace.init(node, localAccessConfig);
        configured = true;

        applicationScratchSpace = nodeScratchSpace.initForApplication();
        assertNotNull(applicationScratchSpace);
        assertIsExistingEmptyDirectory(scratchDataSpacePath);

        activeObjectId = Utils.getActiveObjectId(body);
        scratchPath = Utils.appendSubDirs(scratchDataSpacePath, activeObjectId);
    }

    @After
    public void tearDown() throws FileSystemException, IllegalStateException {
        try {
            if (nodeScratchSpace != null && configured) {
                nodeScratchSpace.close();
            }
        } finally {
            if (testDir != null) {
                assertTrue(testDir.delete());
                testDir = null;
            }
        }
    }

    /**
     * Check if directory is being created.
     * 
     * @throws FileSystemException
     * @throws FileSystemException
     */
    @Test
    public void testGetScratchForAO() throws FileSystemException,
            org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException {

        applicationScratchSpace.getScratchForAO(body);
        assertIsExistingEmptyDirectory(scratchPath);
    }

    /**
     * Check if returned URI is valid.
     * 
     * @throws FileSystemException
     * @throws org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException
     */
    @Test
    public void testGetScratchForAO1() throws FileSystemException,
            org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException {

        final DataSpacesURI uri = applicationScratchSpace.getScratchForAO(body);

        assertValidDataSpacesURI(uri, activeObjectId);
        assertTrue(uri.isSpacePartFullyDefined());
        assertFalse(uri.isSpacePartOnly());
        assertTrue(uri.isSuitableForUserPath());
    }

    /**
     * Check if existing files will be removed.
     * 
     * @throws IOException
     */
    @Test
    public void testGetScratchForAO2() throws IOException {
        dir = new File(scratchPath);
        file = new File(scratchPath, "test.txt");
        assertTrue(dir.mkdir());
        assertTrue(file.createNewFile());

        applicationScratchSpace.getScratchForAO(body);
        assertIsExistingEmptyDirectory(scratchPath);
    }

    /**
     * Check if created files in a scratch still remain there after calling second
     * {@link ApplicationScratchSpace#getScratchForAO}.
     * 
     * @throws IOException
     */
    @Test
    public void testGetScratchForAO3() throws IOException {
        // first - create scratch
        applicationScratchSpace.getScratchForAO(body);
        assertIsExistingEmptyDirectory(scratchPath);

        // secondly - create files
        dir = new File(scratchPath);
        file = new File(scratchPath, "test.txt");
        assertTrue(file.createNewFile());

        // re-get scratch
        applicationScratchSpace.getScratchForAO(body);
        assertTrue(dir.exists());
        assertTrue(file.exists());
    }

    /**
     * Check if returning space info is valid.
     */
    @Test
    public void testGetInstanceInfo() {
        final String spaceAccessURL = Utils.appendSubDirs(ACCESS_URL, RUNTIME_ID, NODE_ID, APP_ID);
        final SpaceInstanceInfo sii = applicationScratchSpace.getSpaceInstanceInfo();
        final String hostname = Utils.getHostname();

        assertNotNull(sii);
        assertEquals(APP_ID_LONG, sii.getAppId());
        assertEquals(SpaceType.SCRATCH, sii.getType());
        assertEquals(hostname, sii.getHostname());
        assertEquals(scratchDataSpacePath, sii.getPath());
        assertEquals(spaceAccessURL, sii.getUrl());
        assertNull(sii.getName());
    }

    /**
     * Check if mounting point URI is valid.
     */
    @Test
    public void testGetInstanceInfo1() {
        final SpaceInstanceInfo sii = applicationScratchSpace.getSpaceInstanceInfo();
        final DataSpacesURI uri = sii.getMountingPoint();

        assertValidDataSpacesURI(uri, null);
        assertTrue(uri.isSpacePartFullyDefined());
        assertTrue(uri.isSpacePartOnly());
    }

    /**
     * Check if mounting point URI is valid.
     */
    @Test
    public void testGetSpaceMountingPoint() {
        final DataSpacesURI uri = applicationScratchSpace.getSpaceMountingPoint();

        assertValidDataSpacesURI(uri, null);
        assertTrue(uri.isSpacePartFullyDefined());
        assertTrue(uri.isSpacePartOnly());
    }

    /**
     * Try to close empty data space.
     * 
     * @throws IOException
     */
    @Test
    public void testClose() throws IOException {
        applicationScratchSpace.getScratchForAO(body);
        assertIsExistingEmptyDirectory(scratchPath);
        dir = new File(scratchPath);

        // close data space
        applicationScratchSpace.close();

        assertFalse(dir.exists());
    }

    /**
     * Check if all files are being removed.
     * 
     * @throws IOException
     */
    @Test
    public void testClose1() throws IOException {
        // first - create scratch
        applicationScratchSpace.getScratchForAO(body);
        assertIsExistingEmptyDirectory(scratchPath);

        // secondly - create files
        dir = new File(scratchPath);
        file = new File(scratchPath, "test.txt");
        assertTrue(file.createNewFile());

        // close data space
        applicationScratchSpace.close();

        assertFalse(dir.exists());
        assertFalse(file.exists());
    }

    private void assertIsExistingEmptyDirectory(final String path) throws FileSystemException {
        final FileObject fPartialDS = fileSystemManager.resolveFile(path);

        assertTrue(fPartialDS.exists());
        assertEquals(FileType.FOLDER, fPartialDS.getType());
        assertEquals(0, fPartialDS.getChildren().length);
    }

    private void assertValidDataSpacesURI(final DataSpacesURI uri, final String activeObjectId) {
        assertNotNull(uri);
        assertEquals(RUNTIME_ID, uri.getRuntimeId());
        assertEquals(NODE_ID, uri.getNodeId());
        assertEquals(APP_ID_LONG, uri.getAppId());
        assertEquals(SpaceType.SCRATCH, uri.getSpaceType());
        assertEquals(activeObjectId, uri.getActiveObjectId());
        assertNull(uri.getName());
        assertNull(uri.getUserPath());
    }
}
