package org.objectweb.proactive.extensions.dataspaces.vfs.adapter;

import static java.util.Arrays.asList;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.util.List;

import org.objectweb.proactive.extensions.dataspaces.api.DataSpacesFileObject;
import org.objectweb.proactive.extensions.dataspaces.api.FileContent;
import org.objectweb.proactive.extensions.dataspaces.api.RandomAccessContent;
import org.objectweb.proactive.extensions.dataspaces.api.RandomAccessMode;
import org.objectweb.proactive.extensions.dataspaces.exceptions.FileSystemException;


public class VFSContentAdapter implements FileContent {

    final private org.apache.commons.vfs.FileContent adaptee;
    final private DataSpacesFileObject owningFile;

    public VFSContentAdapter(org.apache.commons.vfs.FileContent content, DataSpacesFileObject dsFileObject) {
        adaptee = content;
        owningFile = dsFileObject;
    }

    public void close() throws FileSystemException {
        try {
            adaptee.close();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public List<Certificate> getCertificates() throws FileSystemException {
        try {
            final Certificate[] vfsCerts = adaptee.getCertificates();
            return asList(vfsCerts);
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public String getContentEncoding() {
        try {
            return adaptee.getContentInfo().getContentEncoding();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            // parsing errors, a file server fault or not supported meta information
            return null;
        }
    }

    public String getContentMIMEType() {
        try {
            return adaptee.getContentInfo().getContentType();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            // parsing errors, a file server fault or not supported meta information
            return null;
        }
    }

    public DataSpacesFileObject getFile() {
        return owningFile;
    }

    public InputStream getInputStream() throws FileSystemException {
        try {
            return adaptee.getInputStream();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public long getLastModifiedTime() throws FileSystemException {
        try {
            return adaptee.getLastModifiedTime();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public OutputStream getOutputStream() throws FileSystemException {
        try {
            return adaptee.getOutputStream();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public OutputStream getOutputStream(boolean append) throws FileSystemException {
        try {
            return adaptee.getOutputStream(append);
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public RandomAccessContent getRandomAccessContent(RandomAccessMode mode) throws FileSystemException {
        final org.apache.commons.vfs.util.RandomAccessMode vfsMode = buildVFSRandomAccessMode(mode);
        try {
            // according to this VFS build it cannot be null but check it in adaptVFSResult method
            return adaptVFSResult(adaptee.getRandomAccessContent(vfsMode));
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public long getSize() throws FileSystemException {
        try {
            return adaptee.getSize();
        } catch (org.apache.commons.vfs.FileSystemException e) {
            throw new FileSystemException(e);
        }
    }

    public boolean isOpen() {
        return adaptee.isOpen();
    }

    private static RandomAccessContent adaptVFSResult(
            org.apache.commons.vfs.RandomAccessContent randomAccessContent) {
        return randomAccessContent == null ? null : new VFSRandomAccessContentAdapter(randomAccessContent);
    }

    private static org.apache.commons.vfs.util.RandomAccessMode buildVFSRandomAccessMode(RandomAccessMode mode) {
        switch (mode) {
            case READ_ONLY:
                return org.apache.commons.vfs.util.RandomAccessMode.READ;
            case READ_WRITE:
                return org.apache.commons.vfs.util.RandomAccessMode.READWRITE;
            default:
                return null;
        }
    }
}
