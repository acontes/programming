package org.objectweb.proactive.extra.gcmdeployment;

public class PathElement {
    protected String relPath;
    public enum PathBase {PROACTIVE,
        HOME,
        ROOT;
    }
    ;
    protected PathBase base;

    public PathElement() {
        this.relPath = null;
        this.base = PathBase.HOME;
    }

    public PathElement(String relPath) {
        this.relPath = relPath;
        this.base = PathBase.HOME;
    }

    public PathElement(String relPath, String base) {
        this.relPath = relPath;
        setBase(base);
    }

    public String getRelPath() {
        return relPath;
    }

    public void setRelPath(String relPath) {
        this.relPath = relPath;
    }

    public PathBase getBase() {
        return base;
    }

    public void setBase(PathBase base) {
        this.base = base;
    }

    public void setBase(String baseString) {
        if (baseString == null) {
            this.base = PathBase.PROACTIVE; // TODO - what should be the default ?
        } else {
            String baseStringCanonical = baseString.trim();
            if (baseStringCanonical.equalsIgnoreCase("proactive")) {
                this.base = PathBase.PROACTIVE;
            } else if (baseStringCanonical.equalsIgnoreCase("home")) {
                this.base = PathBase.HOME;
            } else if (baseStringCanonical.equalsIgnoreCase("root")) {
                this.base = PathBase.ROOT;
            }
        }
    }

    public String getFullPath() {
        // TODO return the full path based on the relative root. A pointer to some external structure is needed here (argument or encapsulation ?)
        return null;
    }
}
