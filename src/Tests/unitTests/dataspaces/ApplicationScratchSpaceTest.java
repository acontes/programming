package unitTests.dataspaces;

import static org.junit.Assert.assertEquals;
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
import org.objectweb.proactive.extra.dataspaces.ApplicationScratchSpace;
import org.objectweb.proactive.extra.dataspaces.BaseScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.NodeScratchSpace;
import org.objectweb.proactive.extra.dataspaces.SpaceType;
import org.objectweb.proactive.extra.dataspaces.Utils;
import org.objectweb.proactive.extra.dataspaces.VFSFactory;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;

import unitTests.dataspaces.moc.MOCBody;
import unitTests.dataspaces.moc.MOCNode;


public class ApplicationScratchSpaceTest {

    private static final String NODE_ID = "node_id";
    private static final String RUNTIME_ID = "rt_id";
    private static final String SCRATCH_URL = "/";
    private static final String APP_ID = new Long(Utils.getApplicationId(null)).toString();

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
        testDir = new File(System.getProperty("java.io.tmpdir"), "ProActive-ApplicationScratchSpaceTest");
        assertTrue(testDir.mkdir());
        testDirPath = testDir.getCanonicalPath();
        scratchDataSpacePath = Utils.appendSubDirs(testDirPath, RUNTIME_ID, NODE_ID, APP_ID);

        node = new MOCNode(RUNTIME_ID, NODE_ID);
        body = new MOCBody();
        localAccessConfig = new BaseScratchSpaceConfiguration(SCRATCH_URL, testDirPath);
        nodeScratchSpace = new NodeScratchSpace(node, localAccessConfig);

        nodeScratchSpace.init(fileSystemManager);
        configured = true;

        applicationScratchSpace = nodeScratchSpace.initForApplication();
        assertNotNull(applicationScratchSpace);
        assertIsExistingEmptyDirectory(scratchDataSpacePath);

        activeObjectId = Utils.getActiveObjectId(body);
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
     */
    @Test
    public void testGetScratchForAO() throws FileSystemException {
        final String path = Utils.appendSubDirs(testDirPath, RUNTIME_ID, NODE_ID, APP_ID, activeObjectId);
        applicationScratchSpace.getScratchForAO(body);
        assertIsExistingEmptyDirectory(path);
    }

    /**
     * Check if returned URI is valid.
     *
     * @throws FileSystemException
     */
    @Test
    public void testGetScratchForAO1() throws FileSystemException {
        final String path = Utils.appendSubDirs(testDirPath, RUNTIME_ID, NODE_ID, APP_ID, activeObjectId);
        final DataSpacesURI uri = applicationScratchSpace.getScratchForAO(body);

        assertEquals(RUNTIME_ID, uri.getRuntimeId());
        assertEquals(NODE_ID, uri.getNodeId());
        assertEquals(APP_ID, new Long(uri.getAppId()).toString());
        assertEquals(SpaceType.SCRATCH, uri.getSpaceType());
        assertEquals(activeObjectId, uri.getPath());
        assertNull(uri.getName());
    }

    /**
     * Check if existing files will be removed.
     *
     * @throws IOException
     */
    @Test
    public void testGetScratchForAO2() throws IOException {
        final String scratchPath = Utils.appendSubDirs(scratchDataSpacePath, activeObjectId);
        dir = new File(scratchPath);
        file = new File(scratchPath, "test.txt");
        assertTrue(dir.mkdir());
        assertTrue(file.createNewFile());

        applicationScratchSpace.getScratchForAO(body);
        assertIsExistingEmptyDirectory(scratchPath);
    }

    private void assertIsExistingEmptyDirectory(String path) throws FileSystemException {
        FileObject fPartialDS = fileSystemManager.resolveFile(path);

        assertTrue(fPartialDS.exists());
        assertEquals(FileType.FOLDER, fPartialDS.getType());
        assertEquals(0, fPartialDS.getChildren().length);
    }
}
