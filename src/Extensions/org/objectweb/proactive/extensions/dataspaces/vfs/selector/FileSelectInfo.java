/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.objectweb.proactive.extensions.dataspaces.vfs.selector;

import org.objectweb.proactive.extensions.dataspaces.api.DataSpacesFileObject;


/**
 * Information about a file, that is used to select files during the
 * traversal of a hierarchy.
 */
public class FileSelectInfo {
    private DataSpacesFileObject baseFolder;
    private DataSpacesFileObject file;
    private int depth;

    /**
     * Returns the base folder of the traversal.
     * @return FileObject representing the base folder.
     */
    public DataSpacesFileObject getBaseFolder() {
        return baseFolder;
    }

    void setBaseFolder(final DataSpacesFileObject baseFolder) {
        this.baseFolder = baseFolder;
    }

    /**
     * Returns the file (or folder) to be considered.
     * @return The FileObject.
     */
    public DataSpacesFileObject getFile() {
        return file;
    }

    void setFile(final DataSpacesFileObject file) {
        this.file = file;
    }

    /**
     * Returns the depth of the file relative to the base folder.
     * @return The depth of the file relative to the base folder.
     */
    int getDepth() {
        return depth;
    }

    public void setDepth(final int depth) {
        this.depth = depth;
    }
}
