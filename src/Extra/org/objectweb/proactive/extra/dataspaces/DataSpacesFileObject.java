package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.impl.DecoratedFileObject;

public class DataSpacesFileObject extends DecoratedFileObject {

    private final String uri;

    public DataSpacesFileObject(FileObject decoratedFileObject, DataSpacesURI uri) {
        super(decoratedFileObject);
        this.uri = uri.toString();
    }

    public String getURI() {
        return uri;
    }
}
