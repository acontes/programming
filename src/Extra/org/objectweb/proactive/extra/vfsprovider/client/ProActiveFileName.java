package org.objectweb.proactive.extra.vfsprovider.client;

import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.GenericFileName;


public class ProActiveFileName extends GenericFileName {

    protected ProActiveFileName(String scheme, String hostName, int port, int defaultPort, String userName,
            String password, String path, FileType type) {
        super(scheme, hostName, port, defaultPort, userName, password, path, type);
        // TODO Auto-generated constructor stub
    }

}
