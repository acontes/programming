package unitTests.dataspaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.extensions.calcium.system.SkeletonSystemImpl;
import org.objectweb.proactive.extra.dataspaces.DataSpacesLimitingFileObject;
import org.objectweb.proactive.extra.dataspaces.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.InputOutputSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.PADataSpaces;
import org.objectweb.proactive.extra.dataspaces.ScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.SpaceInstanceInfo;
import org.objectweb.proactive.extra.dataspaces.SpaceType;
import org.objectweb.proactive.extra.dataspaces.SpacesDirectory;
import org.objectweb.proactive.extra.dataspaces.SpacesDirectoryImpl;
import org.objectweb.proactive.extra.dataspaces.SpacesMountManager;
import org.objectweb.proactive.extra.dataspaces.VFSFactory;
import org.objectweb.proactive.extra.dataspaces.adapter.vfs.VFSFileObjectAdapter;
import org.objectweb.proactive.extra.dataspaces.api.DataSpacesFileObject;
import org.objectweb.proactive.extra.dataspaces.exceptions.FileSystemException;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceNotFoundException;


/**
 * This test is actually not a pure unit test run in high isolation. It depends on correct behavior
 * of {@link SpacesDirectoryImpl}, {@link VFSFactory}, {@link VFSFileObjectAdapter} together with
 * {@link DataSpacesLimitingFileObject} and basic classes - {@link SpaceInstanceInfo}/
 * {@link DataSpacesURI}.
 */
public class SpacesMountManagerTest {
    private static final String INPUT_FILE = "file.txt";

    private static final String INPUT_FILE_CONTENT = "test";

    private static final String NONEXISTING_FILE = "got_you_i_do_not_exist.txt";

    private static final String SCRATCH_ACTIVE_OBJECT_ID = "777";

    private static final DataSpacesURI NONEXISTING_SPACE = DataSpacesURI.createInOutSpaceURI(123,
            SpaceType.OUTPUT, "dummy");

    private static void closeFileObject(final DataSpacesFileObject file) {
        if (file != null) {
            try {
                file.close();
            } catch (FileSystemException x) {
                System.err.println("Could not close file object: " + x);
            }
        }
    }

    private static String getURLForFile(final File file) throws IOException {
        return "file:///" + file.getCanonicalPath().replaceFirst("^/", "");
    }

    private SpacesMountManager manager;
    private SpacesDirectory directory;
    private File spacesDir;
    private DataSpacesURI inputUri;
    private DataSpacesURI outputUri;
    private DataSpacesURI scratchUri;
    private DataSpacesFileObject fileObject;

    @Before
    public void setUp() throws Exception {
        spacesDir = new File(System.getProperty("java.io.tmpdir"), "ProActive-SpaceMountManagerTest");

        // input space
        final File inputSpaceDir = new File(spacesDir, "input");
        assertTrue(inputSpaceDir.mkdirs());
        final File inputSpaceFile = new File(inputSpaceDir, INPUT_FILE);
        final OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(inputSpaceFile));
        osw.write(INPUT_FILE_CONTENT);
        osw.close();

        final String inputSpaceUrl = getURLForFile(inputSpaceDir);
        final InputOutputSpaceConfiguration inputSpaceConf = InputOutputSpaceConfiguration
                .createInputSpaceConfiguration(inputSpaceUrl, null, null, "read_only_space");
        final SpaceInstanceInfo inputSpaceInfo = new SpaceInstanceInfo(123, inputSpaceConf);
        inputUri = inputSpaceInfo.getMountingPoint();

        // output space
        final File outputSpaceDir = new File(spacesDir, "output");
        assertTrue(outputSpaceDir.mkdirs());

        final String outputSpaceUrl = getURLForFile(outputSpaceDir);
        final InputOutputSpaceConfiguration outputSpaceConf = InputOutputSpaceConfiguration
                .createOutputSpaceConfiguration(outputSpaceUrl, null, null, "read_write_space");
        final SpaceInstanceInfo outputSpaceInfo = new SpaceInstanceInfo(123, outputSpaceConf);
        outputUri = outputSpaceInfo.getMountingPoint();

        // scratch space
        final File scratchSpaceDir = new File(spacesDir, "scratch");
        assertTrue(scratchSpaceDir.mkdirs());
        final File scratchSpaceSubdir = new File(scratchSpaceDir, SCRATCH_ACTIVE_OBJECT_ID);
        scratchSpaceSubdir.mkdir();

        final String scratchSpaceUrl = getURLForFile(scratchSpaceDir);
        final ScratchSpaceConfiguration scratchSpaceConf = new ScratchSpaceConfiguration(scratchSpaceUrl,
            null, null);
        final SpaceInstanceInfo scratchSpaceInfo = new SpaceInstanceInfo(123, "runtimeA", "nodeB",
            scratchSpaceConf);
        scratchUri = scratchSpaceInfo.getMountingPoint();

        // directory and finally manager
        directory = new SpacesDirectoryImpl();
        directory.register(inputSpaceInfo);
        directory.register(outputSpaceInfo);
        directory.register(scratchSpaceInfo);

        manager = new SpacesMountManager(directory);
    }

    @After
    public void tearDown() {
        closeFileObject(fileObject);
        fileObject = null;

        if (manager != null) {
            manager.close();
            manager = null;
        }
        if (spacesDir != null && spacesDir.exists()) {
            assertTrue(SkeletonSystemImpl.deleteDirectory(spacesDir));
            spacesDir = null;
        }
    }

    public void testResolveFileForInputSpace() throws IOException, SpaceNotFoundException {
        fileObject = manager.resolveFile(inputUri, null);
        assertIsWorkingInputSpaceDir(fileObject);
    }

    @Test
    public void testResolveFileForInputSpaceAlreadyMounted1() throws IOException, SpaceNotFoundException {
        testResolveFileForInputSpace();
        testResolveFileForInputSpace();
    }

    @Test
    public void testResolveFileForInputSpaceAlreadyMounted2() throws SpaceNotFoundException, IOException {
        testResolveFileForFileInInputSpace();
        testResolveFileForInputSpace();
    }

    public void testResolveFileForOutputSpace() throws IOException, SpaceNotFoundException {
        fileObject = manager.resolveFile(outputUri, null);
        assertIsWorkingOutputSpaceDir(fileObject);
    }

    public void testResolveFilesNotSharedFileObject() throws IOException, SpaceNotFoundException {
        final DataSpacesFileObject fileObject1 = manager.resolveFile(inputUri, null);
        final DataSpacesFileObject fileObject2 = manager.resolveFile(inputUri, null);

        assertNotSame(fileObject1, fileObject2);
    }

    @Test
    public void testResolveFileForUnexistingSpace() throws SpaceNotFoundException, IOException {
        try {
            manager.resolveFile(NONEXISTING_SPACE, null);
            fail("Exception expected");
        } catch (SpaceNotFoundException x) {
        }
    }

    @Test
    public void testResolveFileForSpacePartNotFullyDefined() throws SpaceNotFoundException, IOException {
        final DataSpacesURI uri = DataSpacesURI.createURI(inputUri.getAppId());
        assertFalse(uri.isSpacePartFullyDefined());
        try {
            manager.resolveFile(uri, null);
            fail("Exception expected");
        } catch (IllegalArgumentException x) {
        }
    }

    @Test
    public void testResolveFileForNotSuitableForUserPath() throws SpaceNotFoundException, IOException {
        assertFalse(scratchUri.isSuitableForUserPath());
        try {
            manager.resolveFile(scratchUri, null);
            fail("Exception expected");
        } catch (IllegalArgumentException x) {
        }
    }

    private void assertIsWorkingInputSpaceDir(final DataSpacesFileObject fo) throws FileSystemException {
        assertTrue(fo.exists());

        // is it that directory?
        final DataSpacesFileObject child = fo.getChild(INPUT_FILE);
        assertNotNull(child);
        assertTrue(child.exists());
        assertEquals(inputUri.toString(), PADataSpaces.getURI(fo));

        // check if write access restrictions are computed correctly - this should be denied
        try {
            child.delete();
            fail("Expected exception - should not have right to write to input space");
        } catch (FileSystemException x) {
            assertTrue(child.exists());
        }

        // check if access restrictions are computed correctly - these 2 should be denied 
        assertNull(fo.getParent());
        try {
            fo.resolveFile("../");
            fail("Expected exception - should not have access to parent file of space dir");
        } catch (FileSystemException x) {
        }

        // check if access restrictions are computed correctly - this should be allowed
        assertNotNull(child.getParent());
        // this not
        try {
            child.resolveFile("../..");
            fail("Expected exception - should not have access to parent file of space dir");
        } catch (FileSystemException x) {
        }
    }

    private void assertIsWorkingOutputSpaceDir(DataSpacesFileObject fo) throws FileSystemException {
        assertTrue(fo.exists());
        assertEquals(outputUri.toString(), PADataSpaces.getURI(fo));
        final DataSpacesFileObject child = fo.resolveFile("new_file");

        // check if write access restrictions are computed correctly - this should be allowed
        child.createFile();
        assertTrue(child.exists());
    }

    @Test
    public void testResolveFileForFileInInputSpace() throws SpaceNotFoundException, IOException {
        final DataSpacesURI fileUri = inputUri.withUserPath(INPUT_FILE);
        fileObject = manager.resolveFile(fileUri, null);

        assertTrue(fileObject.exists());
        // is it that file?
        final InputStream io = fileObject.getContent().getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(io));
        assertEquals(INPUT_FILE_CONTENT, reader.readLine());
        assertEquals(fileUri.toString(), PADataSpaces.getURI(fileObject));
    }

    @Test
    public void testResolveFileForFileInInputSpaceAlreadyMounted1() throws SpaceNotFoundException,
            IOException {
        testResolveFileForFileInInputSpace();
        testResolveFileForFileInInputSpace();
    }

    @Test
    public void testResolveFileForFileInInputSpaceAlreadyMounted2() throws SpaceNotFoundException,
            IOException {
        testResolveFileForInputSpace();
        testResolveFileForFileInInputSpace();
    }

    @Test
    public void testResolveFileForFileInScratchSpaceForOwner() throws SpaceNotFoundException, IOException {
        final DataSpacesURI fileUri = scratchUri.withActiveObjectId(SCRATCH_ACTIVE_OBJECT_ID);
        fileObject = manager.resolveFile(fileUri, SCRATCH_ACTIVE_OBJECT_ID);
        assertIsWorkingScratchForAODir(fileObject, fileUri, true);
    }

    @Test
    public void testResolveFileForFileInScratchSpaceForOtherAO() throws SpaceNotFoundException, IOException {
        final DataSpacesURI fileUri = scratchUri.withActiveObjectId(SCRATCH_ACTIVE_OBJECT_ID);
        final String nonexistingActiveObjectId = SCRATCH_ACTIVE_OBJECT_ID + "toto";
        fileObject = manager.resolveFile(fileUri, nonexistingActiveObjectId);
        assertIsWorkingScratchForAODir(fileObject, fileUri, false);
    }

    @Test
    public void testResolveFileForFileInScratchSpaceForAnonymousOwner() throws SpaceNotFoundException,
            IOException {
        final DataSpacesURI fileUri = scratchUri.withActiveObjectId(SCRATCH_ACTIVE_OBJECT_ID);
        fileObject = manager.resolveFile(fileUri, null);
        assertIsWorkingScratchForAODir(fileObject, fileUri, false);
    }

    private void assertIsWorkingScratchForAODir(final DataSpacesFileObject fo, final DataSpacesURI fileUri,
            final boolean owner) throws FileSystemException, IOException {
        assertTrue(fo.exists());
        assertEquals(fileUri.toString(), PADataSpaces.getURI(fo));
        final DataSpacesFileObject child = fo.resolveFile("new_file");

        if (owner) {
            // check if write access restrictions are computed correctly - this should be allowed
            child.createFile();
            assertTrue(child.exists());
        } else {
            try {
                child.createFile();
                fail("Expected exception - should not have right to write to other AO's scratch");
            } catch (FileSystemException x) {
            }
        }

        // check if access restrictions are computed correctly - these 2 should be denied 
        assertNull(fo.getParent());
        try {
            fo.resolveFile("../");
            fail("Expected exception - should not have access to parent file of scratch for AO");
        } catch (FileSystemException x) {
        }

        // check if access restrictions are computed correctly - this should be allowed
        assertNotNull(child.getParent());
        // this not
        try {
            child.resolveFile("../..");
            fail("Expected exception - should not have access to parent file of scratch for AO");
        } catch (FileSystemException x) {
        }
    }

    @Test
    public void testResolveFileForUnexistingFileInSpace() throws SpaceNotFoundException, IOException {
        final DataSpacesURI fileUri = inputUri.withUserPath(NONEXISTING_FILE);
        fileObject = manager.resolveFile(fileUri, null);
        assertFalse(fileObject.exists());
    }

    @Test
    public void testResolveFileForUnexistingFileInInputSpaceAlreadyMounted1() throws SpaceNotFoundException,
            IOException {
        testResolveFileForFileInInputSpace();
        testResolveFileForUnexistingFileInSpace();
    }

    @Test
    public void testResolveFileForUnexistingFileInInputSpaceAlreadyMounted2() throws SpaceNotFoundException,
            IOException {
        testResolveFileForInputSpace();
        testResolveFileForUnexistingFileInSpace();
    }

    @Test
    public void testResolveFileForFileInNonexistingSpace() throws SpaceNotFoundException, IOException {
        final DataSpacesURI fileUri = NONEXISTING_SPACE.withUserPath(NONEXISTING_FILE);
        try {
            manager.resolveFile(fileUri, null);
            fail("Exception expected");
        } catch (SpaceNotFoundException x) {
        }
    }

    @Test
    public void testResolveSpaces() throws IOException {
        final DataSpacesURI queryUri = DataSpacesURI.createURI(inputUri.getAppId(), inputUri.getSpaceType());
        final Map<DataSpacesURI, ? extends DataSpacesFileObject> spaces = manager.resolveSpaces(queryUri,
                null);
        assertEquals(1, spaces.size());

        fileObject = spaces.get(inputUri);
        assertNotNull(fileObject);
        assertIsWorkingInputSpaceDir(fileObject);
    }

    @Test
    public void testResolveSpacesAlreadyMounted1() throws IOException {
        testResolveSpaces();
        testResolveSpaces();
    }

    @Test
    public void testResolveSpacesAlreadyMounted2() throws SpaceNotFoundException, IOException {
        testResolveFileForFileInInputSpace();
        testResolveSpaces();
    }

    @Test
    public void testResolveSpacesNonexisting() throws SpaceNotFoundException, IOException {
        final String nonexistingRuntimeId = scratchUri.getRuntimeId() + "toto";
        final DataSpacesURI queryUri = DataSpacesURI.createScratchSpaceURI(scratchUri.getAppId(),
                nonexistingRuntimeId);
        assertEquals(0, manager.resolveSpaces(queryUri, null).size());
    }

    @Test
    public void testResolveSpacesNotSharedFileObject() throws IOException {
        final DataSpacesURI queryUri = DataSpacesURI.createURI(inputUri.getAppId(), inputUri.getSpaceType());

        final Map<DataSpacesURI, ? extends DataSpacesFileObject> spaces1 = manager.resolveSpaces(queryUri,
                null);
        assertEquals(1, spaces1.size());
        final DataSpacesFileObject fileObject1 = spaces1.get(inputUri);

        final Map<DataSpacesURI, ? extends DataSpacesFileObject> spaces2 = manager.resolveSpaces(queryUri,
                null);
        assertEquals(1, spaces2.size());
        final DataSpacesFileObject fileObject2 = spaces2.get(inputUri);
        assertNotSame(fileObject1, fileObject2);
    }

    @Test
    public void testResolveSpacesForSpacePartFullyDefined() throws SpaceNotFoundException, IOException {
        try {
            manager.resolveSpaces(inputUri, null);
            fail("Exception expected");
        } catch (IllegalArgumentException x) {
        }
    }

    @Test
    public void testResolveSpacesForNotSuitableForUserPath() throws SpaceNotFoundException, IOException {
        final DataSpacesURI uri = DataSpacesURI.createScratchSpaceURI(scratchUri.getAppId(), scratchUri
                .getRuntimeId());
        try {
            manager.resolveSpaces(uri, null);
            fail("Exception expected");
        } catch (IllegalArgumentException x) {
        }
    }
}
