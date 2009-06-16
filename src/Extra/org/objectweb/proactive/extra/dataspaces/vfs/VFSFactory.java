package org.objectweb.proactive.extra.dataspaces.vfs;

import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.cache.NullFilesCache;
import org.apache.commons.vfs.impl.DefaultFileReplicator;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;
import org.apache.commons.vfs.impl.PrivilegedFileReplicator;
import org.apache.commons.vfs.provider.ftp.FtpFileProvider;
import org.apache.commons.vfs.provider.http.HttpFileProvider;
import org.apache.commons.vfs.provider.https.HttpsFileProvider;
import org.apache.commons.vfs.provider.local.DefaultLocalFileProvider;
import org.apache.commons.vfs.provider.sftp.SftpFileProvider;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs.provider.url.UrlFileProvider;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * Factory class for creating configured VFS DefaultFileSystemManager instances.
 * <p>
 * Instances of managers created through this factory are guaranteed to have providers for following
 * protocols:
 * <ul>
 * <li>local files, scheme <code>file:</code></li>
 * <li>HTTP, scheme <code>http:</code></li>
 * <li>HTTPS, scheme <code>https:</code></li>
 * <li>FTP, scheme <code>ftp:</code></li>
 * <li>SFTP, scheme <code>sftp:</code> (with strict host-key checking disabled by default if no
 * other FileSystemOptions are provided)</li>
 * <li>default URL provider handled by Java URL class</code>
 * </ul>
 * 
 * Configured replicator, temporary storage and default files cache are also guaranteed.
 */
public class VFSFactory {
    private static final Log4JLogger logger = new Log4JLogger(ProActiveLogger
            .getLogger(Loggers.DATASPACES_VFS));

    /**
     * Creates new DefaultSystemManager instance with configured providers, replicator, temporary
     * storage and files cache - as described in class description.
     * <p>
     * Returned instance is initialized and it is a caller responsibility to close it to release
     * resources.
     * 
     * @return configured and initialized DefaultFileSystemManager instance
     * @throws FileSystemException
     *             when initialization or configuration process fails
     */
    public static DefaultFileSystemManager createDefaultFileSystemManager() throws FileSystemException {
        return createDefaultFileSystemManager(true);
    }

    /**
     * Creates new DefaultSystemManager instance with configured providers, replicator, temporary
     * storage - as described in class description, and <strong>DISABLED files cache</strong>
     * (NullFilesCache) .
     * <p>
     * Returned instance is initialized and it is a caller responsibility to close it to release
     * resources.
     * 
     * @param enableFilesCache
     *            when <code>true</code> DefaultFilesCache is configured for returned manager; when
     *            <code>false</code> file caching is disabled - NullFilesCache is configured
     * @return configured and initialized DefaultFileSystemManager instance
     * @throws FileSystemException
     *             when initialization or configuration process fails
     */
    public static DefaultFileSystemManager createDefaultFileSystemManager(boolean enableFilesCache)
            throws FileSystemException {
        logger.debug("Creating new VFS manager");
        final DefaultFileSystemManager manager = new DefaultOptionsFileSystemManager(
            createDefaultFileSystemOptions());
        manager.setLogger(logger);

        final DefaultFileReplicator replicator = new DefaultFileReplicator();
        manager.setReplicator(new PrivilegedFileReplicator(replicator));
        manager.setTemporaryFileStore(replicator);
        if (!enableFilesCache) {
            // WISH: one beautiful day one may try to use FilesCache aware of AO instead of NullFilesCache
            manager.setFilesCache(new NullFilesCache());
        }

        manager.addProvider("file", new DefaultLocalFileProvider());
        manager.addProvider("http", new HttpFileProvider());
        manager.addProvider("https", new HttpsFileProvider());
        manager.addProvider("ftp", new FtpFileProvider());
        manager.addProvider("sftp", new SftpFileProvider());
        manager.setDefaultProvider(new UrlFileProvider());

        manager.init();
        logger.info("Created and initialized new VFS manager");
        return manager;
    }

    private static FileSystemOptions createDefaultFileSystemOptions() throws FileSystemException {
        final FileSystemOptions options = new FileSystemOptions();
        // TODO or try to configure known hosts somehow (look for OpenSSH file etc.) 
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(options, "no");
        return options;
    }
}
