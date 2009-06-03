package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.Capability;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.impl.DecoratedFileObject;
import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;


public class CapabilitiesInfoFileObject extends DecoratedFileObject implements FileObject {

    private static final Logger logger = ProActiveLogger.getLogger(Loggers.DATASPACES);

    private final FileSystem originFileSystem;

    public CapabilitiesInfoFileObject(FileObject file, FileSystem fileSystem) {
        super(file);
        originFileSystem = fileSystem;
    }

    public void assertCapabilitiesMatch(Capability[] expected) throws ConfigurationException {
        if (logger.isTraceEnabled())
            logger.trace("CapabilitiesInfoFileObject: checking expected capabilities, count: " +
                expected.length);

        Utils.assertCapabilitiesMatch(expected, originFileSystem);
    }

    public static FileObject ensureRemovedDecorator(FileObject fo) {
        if (fo instanceof CapabilitiesInfoFileObject)
            return ((CapabilitiesInfoFileObject) fo).getDecoratedFileObject();
        return fo;
    }
}
