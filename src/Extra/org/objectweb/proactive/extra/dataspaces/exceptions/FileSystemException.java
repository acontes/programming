package org.objectweb.proactive.extra.dataspaces.exceptions;

import java.io.IOException;

public class FileSystemException extends IOException {

    private static final long serialVersionUID = -3555529502633312529L;

    public FileSystemException(Throwable e) {
        super(e);
    }

    public FileSystemException(String msg) {
        super(msg);
    }
}
