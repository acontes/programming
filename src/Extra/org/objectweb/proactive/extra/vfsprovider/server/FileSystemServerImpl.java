package org.objectweb.proactive.extra.vfsprovider.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.objectweb.proactive.extra.vfsprovider.exceptions.StreamNotFoundException;
import org.objectweb.proactive.extra.vfsprovider.exceptions.WrongStreamTypeException;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileInfo;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileSystemServer;
import org.objectweb.proactive.extra.vfsprovider.protocol.FileType;
import org.objectweb.proactive.extra.vfsprovider.protocol.StreamMode;


public class FileSystemServerImpl implements FileSystemServer {

    public void streamClose(long stream) throws IOException, StreamNotFoundException {
        // TODO Auto-generated method stub

    }

    public long streamGetLength(long stream) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        // TODO Auto-generated method stub
        return 0;
    }

    public long streamGetPosition(long stream) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        // TODO Auto-generated method stub
        return 0;
    }

    public long streamOpen(String path, StreamMode mode) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    public byte[] streamRead(long stream, long bytes) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        // TODO Auto-generated method stub
        return null;
    }

    public void streamSeek(long stream, long position) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        // TODO Auto-generated method stub

    }

    public long streamSkip(long stream, long bytes) throws IOException, StreamNotFoundException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int streamWrite(long stream, byte[] data) throws IOException, StreamNotFoundException,
            WrongStreamTypeException {
        // TODO Auto-generated method stub
        return 0;
    }

    public void fileCreate(String path, FileType type) throws IOException {
        // TODO Auto-generated method stub

    }

    public void fileDelete(String path, boolean recursive) throws IOException, FileNotFoundException {
        // TODO Auto-generated method stub

    }

    public FileInfo fileGetInfo(String path) throws IOException, FileNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<String> fileListChildren(String path) throws IOException, FileNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<String, FileInfo> fileListChildrenInfo(String path) throws IOException, FileNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    public void fileRename(String path, String newPath) throws IOException, FileNotFoundException {
        // TODO Auto-generated method stub

    }

    public void fileSetLastModifiedTime(String path, long time) throws IOException, FileNotFoundException {
        // TODO Auto-generated method stub

    }

}
