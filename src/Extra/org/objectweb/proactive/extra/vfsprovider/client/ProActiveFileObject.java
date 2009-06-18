package org.objectweb.proactive.extra.vfsprovider.client;

import java.io.InputStream;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.AbstractFileObject;
import org.apache.commons.vfs.provider.AbstractFileSystem;


public class ProActiveFileObject extends AbstractFileObject {

    protected ProActiveFileObject(FileName name, AbstractFileSystem fs) {
        super(name, fs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected long doGetContentSize() throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected InputStream doGetInputStream() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected FileType doGetType() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String[] doListChildren() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
