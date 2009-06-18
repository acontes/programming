package org.objectweb.proactive.extra.vfsprovider.client;

import java.io.DataInputStream;
import java.io.IOException;

import org.apache.commons.vfs.provider.AbstractRandomAccessStreamContent;
import org.apache.commons.vfs.util.RandomAccessMode;


public class ProActiveRandomAccessContent extends AbstractRandomAccessStreamContent {

    protected ProActiveRandomAccessContent(RandomAccessMode mode) {
        super(mode);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected DataInputStream getDataInputStream() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    public void close() throws IOException {
        // TODO Auto-generated method stub

    }

    public long getFilePointer() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    public long length() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    public void seek(long arg0) throws IOException {
        // TODO Auto-generated method stub

    }

}
