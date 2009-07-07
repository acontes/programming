package functionalTests.vfsprovider;

import junit.framework.Test;

import org.apache.commons.AbstractVfsTestCase;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.test.AbstractProviderTestConfig;
import org.apache.commons.vfs.test.ProviderTestSuite;
import org.objectweb.proactive.extra.vfsprovider.client.ProActiveFileName;
import org.objectweb.proactive.extra.vfsprovider.client.ProActiveFileProvider;
import org.objectweb.proactive.extra.vfsprovider.client.ProActiveFileName.ProActiveProviderScheme;
import org.objectweb.proactive.extra.vfsprovider.server.FileSystemServerDeployer;


/**
 * Test suite for VFS ProActiveProvider basing on VFS generic provider tests.
 * <p>
 * Run with junit3 with java property test.basedir set to VFS test directory (can be found as
 * test-data directory in VFS tests jar).
 */
public class ProActiveProviderTest extends AbstractProviderTestConfig {
    private FileSystemServerDeployer deployer;

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

    @Override
    protected void setUp() throws Exception {
        deployer = new FileSystemServerDeployer(AbstractVfsTestCase.getTestDirectory());
    }

    @Override
    protected void tearDown() throws Exception {
        if (deployer != null) {
            deployer.terminate();
            deployer = null;
        }
    };

    @Override
    public void prepare(final DefaultFileSystemManager manager) throws Exception {
        final ProActiveFileProvider provider = new ProActiveFileProvider();
        for (ProActiveProviderScheme scheme : ProActiveFileName.ProActiveProviderScheme.values()) {
            manager.addProvider(scheme.getVFSScheme(), provider);
        }
    }

    @Override
    public FileObject getBaseTestFolder(FileSystemManager fs) throws Exception {
        final String serverURL = deployer.getRemoteFileSystemServerURL();
        final String vfsRootURL = ProActiveFileName.getServerVFSRootURL(serverURL);
        return fs.resolveFile(vfsRootURL);
    }
}
