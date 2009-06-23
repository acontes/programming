package org.objectweb.proactive.extra.vfsprovider.protocol;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;


public interface FileOperations {

    public abstract Set<String> fileListChildren(String path) throws IOException, FileNotFoundException;

    public abstract Map<String, FileInfo> fileListChildrenInfo(String path) throws IOException,
            FileNotFoundException;

    public abstract FileInfo fileGetInfo(String path) throws IOException, FileNotFoundException;

    public abstract boolean fileCreate(String path, FileType type) throws IOException;

    public abstract void fileDelete(String path, boolean recursive) throws IOException, FileNotFoundException;

    public abstract void fileRename(String path, String newPath) throws IOException, FileNotFoundException;

    public abstract void fileSetLastModifiedTime(String path, long time) throws IOException,
            FileNotFoundException;
}
