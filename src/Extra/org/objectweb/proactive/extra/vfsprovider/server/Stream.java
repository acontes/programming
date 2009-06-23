package org.objectweb.proactive.extra.vfsprovider.server;

public interface Stream {

    void close();

    long getLength();

    long getPosition();

    byte[] read(long bytes);

    void seek(long position);

    long skip(long bytes);

    int write(byte[] data);

    void open();
}
