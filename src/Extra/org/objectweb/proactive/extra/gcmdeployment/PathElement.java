package org.objectweb.proactive.extra.gcmdeployment;

import static org.objectweb.proactive.extra.gcmdeployment.GCMDeploymentLoggers.GCMD_LOGGER;
import org.objectweb.proactive.extra.gcmdeployment.process.CommandBuilder;
import org.objectweb.proactive.extra.gcmdeployment.process.HostInfo;
import org.objectweb.proactive.extra.gcmdeployment.process.hostinfo.Tool;
import org.objectweb.proactive.extra.gcmdeployment.process.hostinfo.Tools;
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

    public String getFullPath(HostInfo hostInfo, CommandBuilder commandBuilder) {
        char fp = hostInfo.getOS().fileSeparator();

        switch (base) {
        case ROOT:
            return relPath;
        case HOME:
            return hostInfo.getHomeDirectory() + fp + relPath;
        case PROACTIVE:
            Tool tool = hostInfo.getTool(Tools.PROACTIVE.id);
            if (tool != null) {
                return tool.getPath() + fp + relPath;
            } else {
                String bp = commandBuilder.getPath();
                if (bp != null) {
                    return bp + fp + relPath;
                } else {
                    GCMD_LOGGER.warn("Full Path cannot be returned since nor the ProActive tool nor the CommandBuilder base path have been specified",
                        new IllegalStateException());
                    return null;
                }
            }
        }

        GCMD_LOGGER.warn("Reached unreachable code",
            new Exception("Unreachable"));
        return null;
    }
}
