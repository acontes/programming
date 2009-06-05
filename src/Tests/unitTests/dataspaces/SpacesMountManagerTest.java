package unitTests.dataspaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
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

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.extra.dataspaces.DataSpacesFileObjectImpl;
import org.objectweb.proactive.extra.dataspaces.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.InputOutputSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.PADataSpaces;
import org.objectweb.proactive.extra.dataspaces.SpaceInstanceInfo;
import org.objectweb.proactive.extra.dataspaces.SpaceType;
import org.objectweb.proactive.extra.dataspaces.SpacesDirectory;
import org.objectweb.proactive.extra.dataspaces.SpacesDirectoryImpl;
import org.objectweb.proactive.extra.dataspaces.SpacesMountManager;
import org.objectweb.proactive.extra.dataspaces.VFSFactory;
import org.objectweb.proactive.extra.dataspaces.exceptions.SpaceNotFoundException;


/**
 * This test is actually not a pure unit test run in isolation. It depends on correct behavior of
 * {@link SpacesDirectoryImpl}, {@link VFSFactory} and basic {@link SpaceInstanceInfo}/
 * {@link DataSpacesURI}.
 */
public class SpacesMountManagerTest {
    private static final String EXISTING_FILE = "file.txt";

    private static final String TEST_FILE_CONTENT = "test";

    private static final String NONEXISTING_FILE = "got_you_i_do_not_exist.txt";

    private static final DataSpacesURI NONEXISTING_SPACE = DataSpacesURI.createInOutSpaceURI(123,
            SpaceType.OUTPUT, "dummy");

    private SpacesMountManager manager;
    private SpacesDirectory directory;
    private File spaceDir;
    private File spaceFile;
    private DataSpacesURI spaceUri;
    private FileObject fileObject;

    @Before
    public void setUp() throws Exception {
        // create files
        spaceDir = new File(System.getProperty("java.io.tmpdir"), "ProActive-SpaceMountManagerTest");
        assertTrue(spaceDir.mkdir());

        spaceFile = new File(spaceDir, EXISTING_FILE);
        final OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(spaceFile));
        osw.write(TEST_FILE_CONTENT);
        osw.close();

        final String spaceUrl = "file:///" + spaceDir.getCanonicalPath().replaceFirst("^/", "");
        final InputOutputSpaceConfiguration spaceConf = InputOutputSpaceConfiguration
                .createInputSpaceConfiguration(spaceUrl, null, null, "some_name");
        final SpaceInstanceInfo spaceInfo = new SpaceInstanceInfo(123, spaceConf);
        spaceUri = spaceInfo.getMountingPoint();

        directory = new SpacesDirectoryImpl();
        directory.register(spaceInfo);

        manager = new SpacesMountManager(directory);
    }

    @After
    public void tearDown() {
        if (fileObject != null) {
            try {
                fileObject.close();
            } catch (FileSystemException x) {
                System.err.println("Could not close file object: " + x);
            }
            fileObject = null;
        }
        if (manager != null) {
            manager.close();
            manager = null;
        }
        if (spaceFile != null) {
            spaceFile.delete();
            spaceFile = null;
        }
        if (spaceDir != null) {
            spaceDir.delete();
            spaceDir = null;
        }
    }

    @Test
    public void testResolveFileForSpace() throws FileSystemException, SpaceNotFoundException {
        fileObject = manager.resolveFile(spaceUri);
        assertIsSpaceDir(fileObject);
    }

    @Test
    public void testResolveFileForSpaceAlreadyMounted1() throws FileSystemException, SpaceNotFoundException {
        testResolveFileForSpace();
        testResolveFileForSpace();
    }

    @Test
    public void testResolveFileForSpaceAlreadyMounted2() throws SpaceNotFoundException, IOException {
        testResolveFileForFileInSpace();
        testResolveFileForSpace();
    }

    @Test
    public void testResolveFilesNotSharedFileObject() throws FileSystemException, SpaceNotFoundException {
        final FileObject fileObject1 = manager.resolveFile(spaceUri);
        final FileObject fileObject2 = manager.resolveFile(spaceUri);

        assertNotSame(fileObject1, fileObject2);
    }

    @Test
    public void testResolveFileForUnexistingSpace() throws SpaceNotFoundException, IOException {
        try {
            manager.resolveFile(NONEXISTING_SPACE);
            fail("Exception expected");
        } catch (SpaceNotFoundException x) {
        }
    }

    private void assertIsSpaceDir(final FileObject fo) throws FileSystemException {
        assertTrue(fo.exists());
        // is it that directory?
        final FileObject child = fo.getChild(EXISTING_FILE);
        assertNotNull(child);
        assertTrue(child.exists());
        assertEquals(spaceUri.toString(), PADataSpaces.getURI(fo));
    }

    @Test
    public void testResolveFileForFileInSpace() throws SpaceNotFoundException, IOException {
        final DataSpacesURI fileUri = spaceUri.withPath(EXISTING_FILE);
        fileObject = manager.resolveFile(fileUri);

        assertTrue(fileObject.exists());
        // is it that file?
        final InputStream io = fileObject.getContent().getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(io));
        assertEquals(TEST_FILE_CONTENT, reader.readLine());
        assertEquals(fileUri.toString(), PADataSpaces.getURI(fileObject));
    }

    @Test
    public void testResolveFileForFileInSpaceAlreadyMounted1() throws SpaceNotFoundException, IOException {
        testResolveFileForFileInSpace();
        testResolveFileForFileInSpace();
    }

    @Test
    public void testResolveFileForFileInSpaceAlreadyMounted2() throws SpaceNotFoundException, IOException {
        testResolveFileForSpace();
        testResolveFileForFileInSpace();
    }

    @Test
    public void testResolveFileForUnexistingFileInSpace() throws SpaceNotFoundException, IOException {
        final DataSpacesURI fileUri = spaceUri.withPath(NONEXISTING_FILE);
        fileObject = manager.resolveFile(fileUri);
        assertFalse(fileObject.exists());
    }

    @Test
    public void testResolveFileForUnexistingFileInSpaceAlreadyMounted1() throws SpaceNotFoundException,
            IOException {
        testResolveFileForFileInSpace();
        testResolveFileForUnexistingFileInSpace();
    }

    @Test
    public void testResolveFileForUnexistingFileInSpaceAlreadyMounted2() throws SpaceNotFoundException,
            IOException {
        testResolveFileForSpace();
        testResolveFileForUnexistingFileInSpace();
    }

    @Test
    public void testResolveFileForFileInNonexistingSpace() throws SpaceNotFoundException, IOException {
        final DataSpacesURI fileUri = NONEXISTING_SPACE.withPath(NONEXISTING_FILE);
        try {
            manager.resolveFile(fileUri);
            fail("Exception expected");
        } catch (SpaceNotFoundException x) {
        }
    }

    @Test
    public void testResolveSpaces() throws FileSystemException {
        final DataSpacesURI queryUri = DataSpacesURI.createURI(spaceUri.getAppId());
        final Map<DataSpacesURI, DataSpacesFileObjectImpl> spaces = manager.resolveSpaces(queryUri);
        assertEquals(1, spaces.size());

        fileObject = spaces.get(spaceUri);
        assertNotNull(fileObject);
        assertIsSpaceDir(fileObject);
    }

    @Test
    public void testResolveSpacesAlreadyMounted1() throws FileSystemException {
        testResolveSpaces();
        testResolveSpaces();
    }

    @Test
    public void testResolveSpacesAlreadyMounted2() throws SpaceNotFoundException, IOException {
        testResolveFileForFileInSpace();
        testResolveSpaces();
    }

    @Test
    public void testResolveSpacesNotSharedFileObject() throws FileSystemException {
        final DataSpacesURI queryUri = DataSpacesURI.createURI(spaceUri.getAppId());

        final Map<DataSpacesURI, DataSpacesFileObjectImpl> spaces1 = manager.resolveSpaces(queryUri);
        assertEquals(1, spaces1.size());
        final FileObject fileObject1 = spaces1.get(spaceUri);

        final Map<DataSpacesURI, DataSpacesFileObjectImpl> spaces2 = manager.resolveSpaces(queryUri);
        assertEquals(1, spaces2.size());
        final FileObject fileObject2 = spaces2.get(spaceUri);
        assertNotSame(fileObject1, fileObject2);
    }
}
