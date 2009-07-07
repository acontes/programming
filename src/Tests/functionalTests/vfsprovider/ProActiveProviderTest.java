package functionalTests.vfsprovider;

import junit.framework.Test;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.test.AbstractProviderTestConfig;
import org.apache.commons.vfs.test.ProviderTestSuite;
import org.objectweb.proactive.extra.vfsprovider.client.ProActiveFileProvider;


public class ProActiveProviderTest extends AbstractProviderTestConfig {

    public static Test suite() throws Exception {
        return new ProviderTestSuite(new ProActiveProviderTest());
    }

    @Override
    public void prepare(final DefaultFileSystemManager manager) throws Exception {
        final ProActiveFileProvider provider = new ProActiveFileProvider();
        manager.addProvider("paprmi", provider);
        manager.addProvider("paprmissh", provider);
        manager.addProvider("paphttp", provider);
        manager.addProvider("papibis", provider);
    }

    @Override
    public FileObject getBaseTestFolder(FileSystemManager fs) throws Exception {

        return super.getBaseTestFolder(fs);
    }
}
