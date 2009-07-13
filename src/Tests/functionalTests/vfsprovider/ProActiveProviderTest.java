package functionalTests.vfsprovider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;

import org.apache.commons.AbstractVfsTestCase;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FilesCache;
import org.apache.commons.vfs.cache.SoftRefFilesCache;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.test.ProviderTestConfig;
import org.apache.commons.vfs.test.ProviderTestSuite;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.extra.vfsprovider.FileSystemServerDeployer;
import org.objectweb.proactive.extra.vfsprovider.client.ProActiveFileName;
import org.objectweb.proactive.extra.vfsprovider.client.ProActiveFileProvider;
import org.objectweb.proactive.extra.vfsprovider.client.ProActiveFileName.ProActiveProviderScheme;

import unitTests.vfsprovider.AbstractIOOperationsBase;


/**
 * Test suite for VFS ProActiveProvider basing on VFS generic provider tests (junit3).
 */
public class ProActiveProviderTest extends TestCase implements ProviderTestConfig {
    private final static URL TEST_DATA_SRC_ZIP_URL = ProActiveProviderTest.class
            .getResource("/functionalTests/vfsprovider/_DATA/test-data.zip");

    private final static File testDir = new File(System.getProperty("java.io.tmpdir"),
        "ProActive-ProActiveProviderTest");

    public static Test suite() throws Exception {
        final ProActiveProviderTest providerTest = new ProActiveProviderTest();
        return new ProviderTestSuite(providerTest) {
            @Override
            protected void setUp() throws Exception {
                providerTest.setUp();
                super.setUp();
            }

            @Override
            protected void tearDown() throws Exception {
                super.tearDown();
                providerTest.tearDown();
            }
        };
    }

    public static void extractZip(final ZipInputStream zipStream, final File dstFile) throws IOException {
        ZipEntry zipEntry;
        while ((zipEntry = zipStream.getNextEntry()) != null) {
            final File dstSubFile = new File(dstFile, zipEntry.getName());

            if (zipEntry.isDirectory()) {
                dstSubFile.mkdirs();
                if (!dstSubFile.exists() || !dstSubFile.isDirectory())
                    throw new IOException("Could not create directory: " + dstSubFile);
            } else {
                final OutputStream os = new BufferedOutputStream(new FileOutputStream(dstSubFile));
                try {
                    int data;
                    while ((data = zipStream.read()) != -1)
                        os.write(data);
                } finally {
                    os.close();
                }
            }
        }
    }

    private FileSystemServerDeployer deployer;
    private FilesCache cache;

    public void prepare(final DefaultFileSystemManager manager) throws Exception {
        final ProActiveFileProvider provider = new ProActiveFileProvider();
        for (ProActiveProviderScheme scheme : ProActiveFileName.ProActiveProviderScheme.values()) {
            manager.addProvider(scheme.getVFSScheme(), provider);
        }
    }

    public FileObject getBaseTestFolder(FileSystemManager fs) throws Exception {
        final String vfsRootURL = deployer.getVFSRootURL();
        return fs.resolveFile(vfsRootURL);
    }

    public FilesCache getFilesCache() {
        return cache;
    }

    public void setUp() throws IOException, URISyntaxException {
        cache = new SoftRefFilesCache();
        setUpTestDir();
        startDeployer();
    }

    public void tearDown() throws ProActiveException {
        cache = null;
        removeTestDir();
        stopDeployer();
    }

    private void startDeployer() throws IOException {
        deployer = new FileSystemServerDeployer(AbstractVfsTestCase.getTestDirectory(), false);
    }

    private void stopDeployer() throws ProActiveException {
        if (deployer != null) {
            deployer.terminate();
            deployer = null;
        }
    }

    private void setUpTestDir() throws URISyntaxException, IOException {
        // create dir 
        Assert.assertFalse(testDir.exists());
        Assert.assertTrue(testDir.mkdirs());

        // extract files from archive with VFS provider test data
        final ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(
            TEST_DATA_SRC_ZIP_URL.openStream()));
        try {
            extractZip(zipInputStream, testDir);
        } finally {
            zipInputStream.close();
        }

        // set VFS tests property
        System.setProperty("test.basedir", testDir.getAbsolutePath());
    }

    private void removeTestDir() {
        AbstractIOOperationsBase.deleteRecursively(testDir);
        Assert.assertFalse(testDir.exists());
    }
}
