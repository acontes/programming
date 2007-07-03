package org.objectweb.proactive.extra.gcmdeployment;

import java.io.File;


public class Helpers {

    /**
    * Checks that descriptor exist, is a file and is readable
    * @param descriptor The File to be checked
    * @throws IllegalArgumentException If the File is does not exist, is not a file or is not readable
    */
    public static File checkDescriptorFileExist(File descriptor)
        throws IllegalArgumentException {
        if (!descriptor.exists()) {
            throw new IllegalArgumentException(descriptor.getName() +
                " does not exist");
        }
        if (!descriptor.isFile()) {
            throw new IllegalArgumentException(descriptor.getName() +
                " is not a file");
        }
        if (!descriptor.canRead()) {
            throw new IllegalArgumentException(descriptor.getName() +
                " is not readable");
        }

        return descriptor;
    }
}
