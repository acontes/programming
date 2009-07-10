package functionalTests.vfsprovider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.extra.dataspaces.vfs.VFSFactory;
import org.objectweb.proactive.extra.vfsprovider.client.ProActiveFileName;
import org.objectweb.proactive.extra.vfsprovider.server.FileSystemServerDeployer;

import unitTests.vfsprovider.AbstractIOOperationsTest;


/**
 * ProActiveProvider and FileSystemServerImpl tests for autoclosing feature.
 */
public class TestProActiveProviderAutoclosing {
    private static final int AUTOCLOSE_TIME = 100;
    private static final int CHECKING_TIME = 10;
    private static final int SLEEP_TIME = AUTOCLOSE_TIME * 3;
    private static final String EXISTING_FILE_NAME = "existing_file.txt";
    // big enough to ignore buffering behavior
    private static final int EXISTING_FILE_A_CHARS_NUMBER = 1000000;
    private static final int EXISTING_FILE_B_CHARS_NUMBER = 1000000;

    private final static File testDir = new File(System.getProperty("java.io.tmpdir"),
        "ProActive-TestProaActiveProviderAutoclosing");

    private static BufferedReader openBufferedReader(final FileObject fo) throws FileSystemException {
        return new BufferedReader(new InputStreamReader(openInputStream(fo)));
    }

    private static InputStream openInputStream(final FileObject fo) throws FileSystemException {
        return fo.getContent().getInputStream();
    }

    private static OutputStreamWriter openWriter(final FileObject fo) throws FileSystemException {
        return new OutputStreamWriter(fo.getContent().getOutputStream());
    }

    private static void assertContentEquals(final FileObject fo, final String expectedContent)
            throws FileSystemException, IOException {
        final BufferedReader reader = openBufferedReader(fo);
        try {
            assertEquals(expectedContent, reader.readLine());
        } finally {
            reader.close();
        }
    }

    private FileSystemServerDeployer serverDeployer;
    private DefaultFileSystemManager vfsManager;
    private String serverVFSRootURL;

    @Before
    public void setUp() throws Exception {
        // create directory with content; unfortunately we cannot easily reuse AbstractIOFileOperations (setUp order)
        assertTrue(testDir.mkdirs());
        assertTrue(testDir.exists());
        final Writer writer = new BufferedWriter(new FileWriter(new File(testDir, EXISTING_FILE_NAME)));
        for (int i = 0; i < EXISTING_FILE_A_CHARS_NUMBER; i++) {
            writer.write("a");
        }
        for (int i = 0; i < EXISTING_FILE_B_CHARS_NUMBER; i++) {
            writer.write("b");
        }
        writer.close();

        // start FileSystemServer with proper settings
        PAProperties.PA_VFSPROVIDER_SERVER_STREAM_AUTOCLOSE_CHECKING_INTERVAL_MILLIS.setValue(CHECKING_TIME);
        PAProperties.PA_VFSPROVIDER_SERVER_STREAM_OPEN_MAXIMUM_PERIOD_MILLIS.setValue(AUTOCLOSE_TIME);
        serverDeployer = new FileSystemServerDeployer(testDir.getAbsolutePath(), true);
        serverVFSRootURL = ProActiveFileName.getServerVFSRootURL(serverDeployer
                .getRemoteFileSystemServerURL());

        // set up VFS manager with ProActiveProvider
        vfsManager = VFSFactory.createDefaultFileSystemManager();
    }

    @After
    public void tearDown() throws Exception {
        if (vfsManager != null) {
            vfsManager.close();
            vfsManager = null;
        }

        if (serverDeployer != null) {
            serverDeployer.terminate();
            serverVFSRootURL = null;
            serverDeployer = null;
        }

        if (testDir.exists()) {
            AbstractIOOperationsTest.deleteRecursively(testDir);
        }
    }

    @Test
    public void testOutputStreamOpenAutocloseWrite() throws Exception {
        final FileObject fo = openFileObject("out.txt");
        final Writer writer = openWriter(fo);
        try {
            Thread.sleep(SLEEP_TIME);
            writer.write("test");
        } finally {
            writer.close();
        }
        assertContentEquals(fo, "test");
        fo.close();
    }

    @Test
    public void testOutputStreamOpenWriteAutocloseWrite() throws Exception {
        final FileObject fo = openFileObject("out.txt");
        final Writer writer = openWriter(fo);
        try {
            writer.write("abc");
            Thread.sleep(SLEEP_TIME);
            writer.write("def");
        } finally {
            writer.close();
        }
        assertContentEquals(fo, "abcdef");
        fo.close();
    }

    @Test
    public void testOutputStreamOpenWriteAutocloseFlush() throws Exception {
        final FileObject fo = openFileObject("out.txt");
        final Writer writer = openWriter(fo);
        try {
            writer.write("ghi");
            Thread.sleep(SLEEP_TIME);
            writer.flush();
            assertContentEquals(fo, "ghi");
        } finally {
            writer.close();
        }
        fo.close();
    }

    @Test
    public void testInputStreamOpenAutocloseRead() throws Exception {
        final FileObject fo = openFileObject(EXISTING_FILE_NAME);
        final BufferedReader reader = openBufferedReader(fo);
        try {
            Thread.sleep(SLEEP_TIME);
            for (int i = 0; i < EXISTING_FILE_A_CHARS_NUMBER; i++) {
                assertTrue('a' == reader.read());
            }
        } finally {
            reader.close();
        }
        fo.close();
    }

    @Test
    public void testInputStreamOpenReadAutocloseRead() throws Exception {
        final FileObject fo = openFileObject(EXISTING_FILE_NAME);
        final BufferedReader reader = openBufferedReader(fo);
        try {
            for (int i = 0; i < EXISTING_FILE_A_CHARS_NUMBER; i++) {
                assertTrue('a' == reader.read());
            }
            Thread.sleep(SLEEP_TIME);
            for (int i = 0; i < EXISTING_FILE_B_CHARS_NUMBER; i++) {
                assertTrue('b' == reader.read());
            }
        } finally {
            reader.close();
        }
        fo.close();
    }

    @Test
    public void testInputStreamOpenSkipAutocloseRead() throws Exception {
        final FileObject fo = openFileObject(EXISTING_FILE_NAME);
        // we have to use input stream to avoid Readers buffering (input stream buffering is ok) 
        final InputStream is = openInputStream(fo);
        try {
            is.skip(EXISTING_FILE_A_CHARS_NUMBER);
            Thread.sleep(SLEEP_TIME);
            assertTrue('b' == is.read());
        } finally {
            is.close();
        }
        fo.close();
    }

    // TODO randomAccess read-only and read-write

    private FileObject openFileObject(final String fileName) throws FileSystemException {
        return vfsManager.resolveFile(serverVFSRootURL).resolveFile(fileName);
    }
}
