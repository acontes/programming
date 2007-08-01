package org.objectweb.proactive.core.util;

public enum OperatingSystem {windows(';', '\\'),
    unix(':', '/');
    protected char pathSeparator;
    protected char fileSeperator;

    OperatingSystem(char pathSeparator, char fileSeparator) {
        this.pathSeparator = pathSeparator;
        this.fileSeperator = fileSeparator;
    }

    public char pathSeparator() {
        return pathSeparator;
    }

    public char fileSeparator() {
        return fileSeperator;
    }

    static public OperatingSystem getOperatingSystem() {
        String val = System.getProperty("os.name");
        if (val.contains("Windows")) {
            return windows;
        }

        return unix;
    }
}
