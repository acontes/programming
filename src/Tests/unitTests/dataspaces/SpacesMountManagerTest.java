package unitTests.dataspaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.objectweb.proactive.extra.dataspaces.DataSpacesURI;
import org.objectweb.proactive.extra.dataspaces.InputOutputSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.SpaceInstanceInfo;
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
    private static final String TEST_FILE_CONTENT = "test";

    private SpacesMountManager manager;
    private SpacesDirectory directory;
    private File spaceDir;
    private File spaceFile;
    private DataSpacesURI spaceUri;

    @Before
    public void setUp() throws Exception {
        // create files
        spaceDir = new File(System.getProperty("java.io.tmpdir"), "ProActive-SpaceMountManagerTest");
        assertTrue(spaceDir.mkdir());

        spaceFile = new File(spaceDir, "file.txt");
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

        manager = new SpacesMountManager(VFSFactory.createDefaultFileSystemManager(), directory);
    }

    @After
    public void tearDown() {
        manager.close();
        assertTrue(spaceFile.delete());
        assertTrue(spaceDir.delete());
    }

    @Test
    public void testResolveFileForSpace() throws FileSystemException, SpaceNotFoundException {
        final FileObject fo = manager.resolveFile(spaceUri);
        assertIsSpaceDir(fo);
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

    private void assertIsSpaceDir(final FileObject fo) throws FileSystemException {
        assertTrue(fo.exists());
        // is it that directory?
        final FileObject child = fo.getChild("file.txt");
        assertNotNull(child);
        assertTrue(child.exists());
        assertEquals(spaceUri.toString(), fo.getName().getURI());
    }

    @Test
    public void testResolveFileForFileInSpace() throws SpaceNotFoundException, IOException {
        final DataSpacesURI fileUri = spaceUri.withPath("file.txt");
        final FileObject fo = manager.resolveFile(fileUri);

        assertTrue(fo.exists());
        // is it that file?
        final InputStream io = fo.getContent().getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(io));
        assertEquals(TEST_FILE_CONTENT, reader.readLine());
        assertEquals(fileUri.toString(), fo.getName().getURI());
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
    public void testResolveSpaces() throws FileSystemException {
        final DataSpacesURI queryUri = DataSpacesURI.createURI(spaceUri.getAppId());
        final Map<DataSpacesURI, FileObject> spaces = manager.resolveSpaces(queryUri);
        assertEquals(1, spaces.size());

        final FileObject fo = spaces.get(spaceUri);
        assertNotNull(fo);
        assertIsSpaceDir(fo);
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
}
