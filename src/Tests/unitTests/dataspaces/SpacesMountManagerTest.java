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


// TODO: add test for URI not suitable for having path and perhaps AO owner 
/**
 * This test is actually not a pure unit test run in high isolation. It depends on correct behavior
 * of {@link SpacesDirectoryImpl}, {@link VFSFactory}, {@link VFSFileObjectAdapter} together with
 * {@link DataSpacesLimitingFileObject} and basic classes - {@link SpaceInstanceInfo}/
 * {@link DataSpacesURI}.
 */
public class SpacesMountManagerTest {
    private static final String EXISTING_FILE = "file.txt";

    private static final String EXISTING_SUBDIR = "dir";

    private static final String TEST_FILE_CONTENT = "test";

    private static final String NONEXISTING_FILE = "got_you_i_do_not_exist.txt";

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

    private SpacesMountManager manager;
    private SpacesDirectory directory;
    private File spacesDir;
    private DataSpacesURI readOnlyUri;
    private DataSpacesURI readWriteUri;
    private DataSpacesFileObject fileObjectRO;
    private DataSpacesFileObject fileObjectRW;

    @Before
    public void setUp() throws Exception {
        // create files
        spacesDir = new File(System.getProperty("java.io.tmpdir"), "ProActive-SpaceMountManagerTest");
        final File roSpaceDir = new File(spacesDir, "readOnly");
        final File rwSpaceDir = new File(spacesDir, "readWrite");
        assertTrue(roSpaceDir.mkdirs());
        assertTrue(rwSpaceDir.mkdirs());

        final File readOnlySpaceFile = new File(roSpaceDir, EXISTING_FILE);
        final OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(readOnlySpaceFile));
        osw.write(TEST_FILE_CONTENT);
        osw.close();
        final File spaceSubdir = new File(roSpaceDir, EXISTING_SUBDIR);
        spaceSubdir.mkdir();

        final String roSpaceUrl = "file:///" + roSpaceDir.getCanonicalPath().replaceFirst("^/", "");
        final InputOutputSpaceConfiguration roSpaceConf = InputOutputSpaceConfiguration
                .createInputSpaceConfiguration(roSpaceUrl, null, null, "read_only_space");
        final SpaceInstanceInfo roSpaceInfo = new SpaceInstanceInfo(123, roSpaceConf);
        readOnlyUri = roSpaceInfo.getMountingPoint();

        final String rwSpaceUrl = "file:///" + rwSpaceDir.getCanonicalPath().replaceFirst("^/", "");
        final InputOutputSpaceConfiguration rwSpaceConf = InputOutputSpaceConfiguration
                .createOutputSpaceConfiguration(rwSpaceUrl, null, null, "read_write_space");
        final SpaceInstanceInfo rwSpaceInfo = new SpaceInstanceInfo(123, rwSpaceConf);
        readWriteUri = rwSpaceInfo.getMountingPoint();

        directory = new SpacesDirectoryImpl();
        directory.register(roSpaceInfo);
        directory.register(rwSpaceInfo);

        manager = new SpacesMountManager(directory);
    }

    @After
    public void tearDown() {
        closeFileObject(fileObjectRO);
        fileObjectRO = null;
        closeFileObject(fileObjectRW);
        fileObjectRW = null;

        if (manager != null) {
            manager.close();
            manager = null;
        }
        if (spacesDir != null && spacesDir.exists()) {
            assertTrue(SkeletonSystemImpl.deleteDirectory(spacesDir));
            spacesDir = null;
        }
    }

    public void testResolveFileForReadOnlySpace() throws IOException, SpaceNotFoundException {
        fileObjectRO = manager.resolveFile(readOnlyUri, null);
        assertIsWorkingReadOnlySpaceDir(fileObjectRO);
    }

    @Test
    public void testResolveFileForReadOnlySpaceAlreadyMounted1() throws IOException, SpaceNotFoundException {
        testResolveFileForReadOnlySpace();
        testResolveFileForReadOnlySpace();
    }

    @Test
    public void testResolveFileForReadOnlySpaceAlreadyMounted2() throws SpaceNotFoundException, IOException {
        testResolveFileForFileInReadOnlySpace();
        testResolveFileForReadOnlySpace();
    }

    public void testResolveFilesNotSharedFileObject() throws IOException, SpaceNotFoundException {
        final DataSpacesFileObject fileObject1 = manager.resolveFile(readOnlyUri, null);
        final DataSpacesFileObject fileObject2 = manager.resolveFile(readOnlyUri, null);

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

    private void assertIsWorkingReadOnlySpaceDir(final DataSpacesFileObject fo) throws FileSystemException {
        assertTrue(fo.exists());
        // is it that directory?
        // FIXME: This casting should be changed as DataSpacesFileObject interface will change
        final DataSpacesFileObject child = (DataSpacesFileObject) fo.getChild(EXISTING_FILE);
        assertNotNull(child);
        assertTrue(child.exists());
        assertEquals(readOnlyUri.toString(), PADataSpaces.getURI(fo));

        // check if write access restrictions are computed correctly - this should be denied
        try {
            child.delete();
            fail("Expected exception - should not have right to write to read only space");
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

        // FIXME: This casting should be changed as DataSpacesFileObject interface will change
        final DataSpacesFileObject subdir = (DataSpacesFileObject) fo.getChild(EXISTING_SUBDIR);
        assertNotNull(subdir);
        assertTrue(subdir.exists());

        // check if access restrictions are computed correctly - this should be allowed
        subdir.getParent();
        // this not
        try {
            subdir.resolveFile("../..");
            fail("Expected exception - should not have access to parent file of space dir");
        } catch (FileSystemException x) {
        }
    }

    private void assertIsWorkingReadWriteSpaceDir(DataSpacesFileObject fo) throws FileSystemException {
        assertTrue(fo.exists());
        // FIXME: This casting should be changed as DataSpacesFileObject interface will change
        final DataSpacesFileObject child = (DataSpacesFileObject) fo.resolveFile("new_file");

        // check if write access restrictions are computed correctly - this should be allowed
        child.createFile();
        assertTrue(child.exists());
    }

    @Test
    public void testResolveFileForFileInReadOnlySpace() throws SpaceNotFoundException, IOException {
        final DataSpacesURI fileUri = readOnlyUri.withUserPath(EXISTING_FILE);
        fileObjectRO = manager.resolveFile(fileUri, null);

        assertTrue(fileObjectRO.exists());
        // is it that file?
        final InputStream io = fileObjectRO.getContent().getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(io));
        assertEquals(TEST_FILE_CONTENT, reader.readLine());
        assertEquals(fileUri.toString(), PADataSpaces.getURI(fileObjectRO));
    }

    @Test
    public void testResolveFileForFileInReadOnlySpaceAlreadyMounted1() throws SpaceNotFoundException,
            IOException {
        testResolveFileForFileInReadOnlySpace();
        testResolveFileForFileInReadOnlySpace();
    }

    @Test
    public void testResolveFileForFileInReadOnlySpaceAlreadyMounted2() throws SpaceNotFoundException,
            IOException {
        testResolveFileForReadOnlySpace();
        testResolveFileForFileInReadOnlySpace();
    }

    @Test
    public void testResolveFileForUnexistingFileInSpace() throws SpaceNotFoundException, IOException {
        final DataSpacesURI fileUri = readOnlyUri.withUserPath(NONEXISTING_FILE);
        fileObjectRO = manager.resolveFile(fileUri, null);
        assertFalse(fileObjectRO.exists());
    }

    @Test
    public void testResolveFileForUnexistingFileInReadOnlySpaceAlreadyMounted1()
            throws SpaceNotFoundException, IOException {
        testResolveFileForFileInReadOnlySpace();
        testResolveFileForUnexistingFileInSpace();
    }

    @Test
    public void testResolveFileForUnexistingFileInReadOnlySpaceAlreadyMounted2()
            throws SpaceNotFoundException, IOException {
        testResolveFileForReadOnlySpace();
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
        final DataSpacesURI queryUri = DataSpacesURI.createURI(readOnlyUri.getAppId());
        final Map<DataSpacesURI, ? extends DataSpacesFileObject> spaces = manager.resolveSpaces(queryUri,
                null);
        assertEquals(2, spaces.size());

        fileObjectRO = spaces.get(readOnlyUri);
        assertNotNull(fileObjectRO);
        assertIsWorkingReadOnlySpaceDir(fileObjectRO);

        fileObjectRW = spaces.get(readWriteUri);
        assertNotNull(fileObjectRW);
        assertIsWorkingReadWriteSpaceDir(fileObjectRW);
    }

    @Test
    public void testResolveSpacesAlreadyMounted1() throws IOException {
        testResolveSpaces();
        testResolveSpaces();
    }

    @Test
    public void testResolveSpacesAlreadyMounted2() throws SpaceNotFoundException, IOException {
        testResolveFileForFileInReadOnlySpace();
        testResolveSpaces();
    }

    @Test
    public void testResolveSpacesNotSharedFileObject() throws IOException {
        final DataSpacesURI queryUri = DataSpacesURI.createURI(readOnlyUri.getAppId());

        final Map<DataSpacesURI, ? extends DataSpacesFileObject> spaces1 = manager.resolveSpaces(queryUri,
                null);
        assertEquals(2, spaces1.size());
        final DataSpacesFileObject fileObject1 = spaces1.get(readOnlyUri);

        final Map<DataSpacesURI, ? extends DataSpacesFileObject> spaces2 = manager.resolveSpaces(queryUri,
                null);
        assertEquals(2, spaces2.size());
        final DataSpacesFileObject fileObject2 = spaces2.get(readOnlyUri);
        assertNotSame(fileObject1, fileObject2);
    }
}
