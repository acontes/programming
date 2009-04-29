package unitTests.dataspaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extra.dataspaces.ApplicationScratchSpace;
import org.objectweb.proactive.extra.dataspaces.BaseScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.NodeScratchSpace;
import org.objectweb.proactive.extra.dataspaces.Utils;
import org.objectweb.proactive.extra.dataspaces.VFSFactory;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;

import unitTests.dataspaces.mock.MOCKNode;


/**
 * Test for {@link NodeScratchSpaceTest} class, uses view MOCK Objects for imitating integration
 * with ProActive (obtaining ID's of a node, runtime).
 */
public class NodeScratchSpaceTest {

    private static final String NODE_ID_2 = "second_node";
    private static final String NODE_ID = "node_id";
    private static final String RUNTIME_ID = "rt_id";
    private static final String SCRATCH_URL = "/";

    private static final String APP_ID = new Long(Utils.getApplicationId(null)).toString();
    private static final String TEST_FILE_CONTENT = "qwerty";

    private static DefaultFileSystemManager fileSystemManager;
    private File testDir;
    private MOCKNode node;
    private NodeScratchSpace nodeScratchSpace;
    private String testDirPath;
    private boolean configured;
    private File partialDSDummyFile;
    private File dsDummyFile;
    private BaseScratchSpaceConfiguration localAccessConfig;
    private NodeScratchSpace nodeScratchSpace2;
    private boolean configured2;

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
        testDir = new File(System.getProperty("java.io.tmpdir"), "ProActive-NodeScratchSpaceTest");
        assertTrue(testDir.mkdir());
        testDirPath = testDir.getCanonicalPath();
        localAccessConfig = new BaseScratchSpaceConfiguration(SCRATCH_URL, testDirPath);

        node = new MOCKNode(RUNTIME_ID, NODE_ID);
        nodeScratchSpace = new NodeScratchSpace(node, localAccessConfig);
        configured = false;
        configured2 = false;
    }

    @After
    public void tearDown() throws FileSystemException, IllegalStateException {
        try {
            if (nodeScratchSpace != null && configured) {
                nodeScratchSpace.close();
            }
        } finally {
            try {
                if (nodeScratchSpace2 != null && configured2) {
                    nodeScratchSpace2.close();
                }
            } finally {
                doCleanup();
            }
        }
    }

    private void doCleanup() {
        if (partialDSDummyFile != null) {
            File nodeDir = partialDSDummyFile.getParentFile();
            File rtDir = nodeDir.getParentFile();
            assertTrue(partialDSDummyFile.delete());
            assertTrue(nodeDir.delete());
            assertTrue(rtDir.delete());
            partialDSDummyFile = null;
        }

        if (dsDummyFile != null) {
            File appDir = dsDummyFile.getParentFile();
            File nodeDir = appDir.getParentFile();
            File rtDir = nodeDir.getParentFile();
            assertTrue(dsDummyFile.delete());
            assertTrue(appDir.delete());
            assertTrue(nodeDir.delete());
            assertTrue(rtDir.delete());
            dsDummyFile = null;
        }

        if (testDir != null) {
            assertTrue(testDir.delete());
            testDir = null;
        }
    }

    /**
     * Check if files are being created.
     * 
     * @throws ConfigurationException
     * @throws FileSystemException
     * @throws IllegalStateException
     */
    @Test
    public void testInitNSS() throws ConfigurationException, FileSystemException, IllegalStateException {
        nodeScratchSpace.init(fileSystemManager);
        configured = true;
        String path = Utils.appendSubDirs(testDirPath, RUNTIME_ID, NODE_ID);
        assertIsExistingEmptyDirectory(path);
    }

    /**
     * Check if existing files are being removed.
     * 
     * @throws ConfigurationException
     * @throws IllegalStateException
     * @throws IOException
     */
    @Test
    public void testInitNSS2() throws ConfigurationException, IllegalStateException, IOException {
        final String partialDS = Utils.appendSubDirs(testDirPath, RUNTIME_ID, NODE_ID);
        final File dir = new File(partialDS);

        dir.mkdirs();
        partialDSDummyFile = new File(dir, "test.txt");
        final OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(partialDSDummyFile));
        osw.write(TEST_FILE_CONTENT);
        osw.close();

        nodeScratchSpace.init(fileSystemManager);
        configured = true;
        assertIsExistingEmptyDirectory(partialDS);
        partialDSDummyFile = null;
    }

    /**
     * Double initialization case checking.
     * 
     * @throws ConfigurationException
     * @throws FileSystemException
     * @throws IllegalStateException
     */
    @Test
    public void testInitNSSIllegalState() throws ConfigurationException, FileSystemException,
            IllegalStateException {

        nodeScratchSpace.init(fileSystemManager);
        configured = true;
        try {
            nodeScratchSpace.init(fileSystemManager);
            fail("Exception expected");
        } catch (IllegalStateException e) {
        } catch (Exception e) {
            fail("Wrong exception");
        }
    }

    /**
     * Initialization after close method call.
     * 
     * @throws ConfigurationException
     * @throws FileSystemException
     * @throws IllegalStateException
     */
    @Test
    public void testInitNSSIllegalState2() throws ConfigurationException, FileSystemException,
            IllegalStateException {

        nodeScratchSpace.init(fileSystemManager);
        configured = true;
        nodeScratchSpace.close();
        configured = false;

        try {
            nodeScratchSpace.init(fileSystemManager);
            fail("Exception expected");
        } catch (IllegalStateException e) {
        } catch (Exception e) {
            fail("Wrong exception");
        }
    }

    /**
     * Check if files are being created and not null instance returned.
     * 
     * @throws ConfigurationException
     * @throws FileSystemException
     * @throws IllegalStateException
     */
    @Test
    public void testInitForApplication() throws ConfigurationException, FileSystemException,
            IllegalStateException {

        nodeScratchSpace.init(fileSystemManager);
        configured = true;
        checkInitForApplication();
    }

    /**
     * InitForApplication without former node initialization.
     * 
     * @throws ConfigurationException
     * @throws FileSystemException
     * @throws IllegalStateException
     */
    @Test
    public void testInitForApplicationIllegalState() throws ConfigurationException, FileSystemException,
            IllegalStateException {
        try {
            nodeScratchSpace.initForApplication();
            fail("Exception expected");
        } catch (IllegalStateException e) {
        } catch (Exception e) {
            fail("Wrong exception");
        }

        nodeScratchSpace.init(fileSystemManager);
        configured = true;
        checkInitForApplication();
    }

    /**
     * Passing configuration without remote access defined.
     * 
     * @throws ConfigurationException
     * @throws FileSystemException
     * @throws IllegalStateException
     */
    @Test
    public void testInitForApplicationConfigurationException() throws ConfigurationException,
            FileSystemException, IllegalStateException {

        BaseScratchSpaceConfiguration conf = new BaseScratchSpaceConfiguration(null, testDirPath);
        nodeScratchSpace = new NodeScratchSpace(node, conf);
        nodeScratchSpace.init(fileSystemManager);
        configured = true;

        try {
            nodeScratchSpace.initForApplication();
            fail("Exception expected");
        } catch (ConfigurationException e) {
        } catch (Exception e) {
            fail("Wrong exception");
        }

        // nodeScratchSpace instance cannot be used anymore..
    }

    /**
     * Check if only one data space is being removed. Note that closing is also tested on each
     * {@link #tearDown()} method call.
     * 
     * @throws ConfigurationException
     * @throws FileSystemException
     * @throws IllegalStateException
     */
    @Test
    public void testClose() throws ConfigurationException, FileSystemException, IllegalStateException {
        final String path1 = Utils.appendSubDirs(testDirPath, RUNTIME_ID, NODE_ID);
        final String path2 = Utils.appendSubDirs(testDirPath, RUNTIME_ID, NODE_ID_2);
        final Node node2 = new MOCKNode(RUNTIME_ID, NODE_ID_2);
        nodeScratchSpace2 = new NodeScratchSpace(node2, localAccessConfig);

        nodeScratchSpace.init(fileSystemManager);
        configured = true;
        nodeScratchSpace2.init(fileSystemManager);
        configured2 = true;

        assertIsExistingEmptyDirectory(path1);
        assertIsExistingEmptyDirectory(path2);
        nodeScratchSpace.close();
        assertIsExistingEmptyDirectory(path2);
    }

    private void assertIsExistingEmptyDirectory(String path) throws FileSystemException {
        FileObject fPartialDS = fileSystemManager.resolveFile(path);

        assertTrue(fPartialDS.exists());
        assertEquals(FileType.FOLDER, fPartialDS.getType());
        assertEquals(0, fPartialDS.getChildren().length);
    }

    private void checkInitForApplication() throws FileSystemException, ConfigurationException {
        final String dataSpacePath = Utils.appendSubDirs(testDirPath, RUNTIME_ID, NODE_ID, APP_ID);
        final ApplicationScratchSpace app = nodeScratchSpace.initForApplication();
        assertNotNull(app);
        assertIsExistingEmptyDirectory(dataSpacePath);
    }
}
