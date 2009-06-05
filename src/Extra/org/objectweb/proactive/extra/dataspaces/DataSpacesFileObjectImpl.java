package org.objectweb.proactive.extra.dataspaces;

import org.apache.commons.vfs.FileObject;


/**
 * Implements the public interface of a file representation in Data Spaces framework.
 */
public class DataSpacesFileObjectImpl extends AbstractLimitingFileObject implements DataSpacesFileObject {

    private final DataSpacesURI spaceUri;

    /**
     * @param fileObject
     *            that is to be represented as DataSpacesFileObject, cannot be <code>null</code>
     * @param URI
     *            Data Spaces "virtual file system tree" URI of that file object, cannot be
     *            <code>null</code>
     */
    public DataSpacesFileObjectImpl(FileObject fileObject, DataSpacesURI URI) {
        super(fileObject);
        this.spaceUri = URI.getSpacePartOnly();
    }

    public String getURI() {
        return Utils.appendSubDirs(spaceUri.toString(), getName().getPath());
    }

    @Override
    protected FileObject doDecorateFile(FileObject file) {
        final DataSpacesFileObjectImpl newFile = new DataSpacesFileObjectImpl(file, spaceUri);
        final LimitingPolicy policy = getLimitingPolicy();

        if (policy != null) {
            final LimitingPolicy newPolicy = policy.newInstance(newFile);
            newFile.setLimitingPolicy(newPolicy);
        }
        return newFile;
    }
}
