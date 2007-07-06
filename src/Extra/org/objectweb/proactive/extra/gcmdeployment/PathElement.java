package org.objectweb.proactive.extra.gcmdeployment;

public class PathElement {
    protected String relPath;
    public enum PathBase {PROACTIVE,
        HOME,
        ROOT;
    }
    ;
    protected PathBase base;

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
        if (baseString.trim().toLowerCase().equals("proactive")) {
            this.base = PathBase.PROACTIVE;
        } else {
            this.base = PathBase.HOME;
        }
    }

    public String getFullPath() {
        // TODO
        return null;
    }
}
