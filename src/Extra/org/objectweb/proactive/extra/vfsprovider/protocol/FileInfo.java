package org.objectweb.proactive.extra.vfsprovider.protocol;

import java.io.Serializable;


public interface FileInfo extends Serializable {
    public FileType getType();

    public long getSize();

    public long getLastModifiedTime();

    public boolean isReadable();

    public boolean isWritable();

    public boolean isHidden();
}
