package org.objectweb.proactive.core.util;

import java.io.Serializable;
public enum OperatingSystem implements Serializable {windows(';', '\\'),
    unix(':', '/');
    protected char pathSeparator;
    protected char fileSeparator;

    OperatingSystem(char pathSeparator, char fileSeparator) {
        this.pathSeparator = pathSeparator;
        this.fileSeparator = fileSeparator;
    }

    public char pathSeparator() {
        return pathSeparator;
    }

    public char fileSeparator() {
        return fileSeparator;
    }

    static public OperatingSystem getOperatingSystem() {
        String val = System.getProperty("os.name");
        if (val.contains("Windows")) {
            return windows;
        }

        return unix;
    }
}
