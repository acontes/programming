package org.objectweb.proactive.extra.vfsprovider.client;

import java.util.Collection;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.AbstractFileSystem;


public class ProActiveFileSystem extends AbstractFileSystem {

    protected ProActiveFileSystem(FileName rootName, FileObject parentLayer,
            FileSystemOptions fileSystemOptions) {
        super(rootName, parentLayer, fileSystemOptions);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void addCapabilities(Collection arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    protected FileObject createFile(FileName arg0) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
