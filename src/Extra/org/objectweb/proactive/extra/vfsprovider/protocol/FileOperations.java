package org.objectweb.proactive.extra.vfsprovider.protocol;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


public interface FileOperations {

    public abstract Set<String> fileListChildren(String path);

    public abstract Map<String, FileInfo> fileListChildrenInfo(String path);

    public abstract FileInfo fileGetInfo(String path);

    public abstract boolean fileCreate(String path, FileType type) throws IOException;

    public abstract boolean fileDelete(String path, boolean recursive);

    public abstract boolean fileRename(String path, String newPath);

    public abstract boolean fileSetLastModifiedTime(String path, long time);
}
