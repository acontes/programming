package org.objectweb.proactive.extra.vfsprovider.protocol;

/**
 * Protocol definition made of two parts: {@link StreamOperations} and {@link FileOperations}.
 * <p>
 * Instances of this class are intended to work as remote objects and hence are thread-safe.
 */
public interface FileSystemServer extends StreamOperations, FileOperations {
}
