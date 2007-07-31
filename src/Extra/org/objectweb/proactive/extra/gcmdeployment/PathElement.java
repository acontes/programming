package org.objectweb.proactive.extra.gcmdeployment;

import static org.objectweb.proactive.extra.gcmdeployment.GCMDeploymentLoggers.GCMD_LOGGER;
import org.objectweb.proactive.extra.gcmdeployment.process.CommandBuilder;
import org.objectweb.proactive.extra.gcmdeployment.process.HostInfo;
import org.objectweb.proactive.extra.gcmdeployment.process.hostinfo.Tool;
import org.objectweb.proactive.extra.gcmdeployment.process.hostinfo.Tools;


public class PathElement implements Cloneable {
    protected String relPath;
    public enum PathBase {PROACTIVE,
        HOME,
        ROOT;
    }
    ;
    protected PathBase base;

    public PathElement() {
        this.relPath = null;
        this.base = PathBase.ROOT;
    }

    public PathElement(String relPath) {
        this.relPath = relPath;
        this.base = PathBase.ROOT;
    }

    public PathElement(String relPath, String base) {
        this.relPath = relPath;
        setBase(base);
    }

    public PathElement(String relPath, PathBase base) {
        this.relPath = relPath;
        this.base = base;
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
            this.base = PathBase.ROOT; // TODO - what should be the default ?
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
        switch (base) {
        case ROOT:
            return relPath;
        case HOME:
            return appendPath(hostInfo.getHomeDirectory(), relPath, hostInfo);
        case PROACTIVE:
            Tool tool = hostInfo.getTool(Tools.PROACTIVE.id);
            if (tool != null) {
                return appendPath(tool.getPath(), relPath, hostInfo);
            } else {
                String bp = commandBuilder.getPath(hostInfo);
                if (bp != null) {
                    return appendPath(bp, relPath, hostInfo);
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        PathElement res = new PathElement(this.relPath);
        res.setBase(this.base);
        return res;
    }

    /**
     * Concatenates two path using the file separator given by the hostInfo parameter
     *
     * @param s1 the first path
     * @param s2 the second path
     * @param hostInfo Indicates which file separator to use
     * @return The concatenation of s1 and s2
     */
    static public String appendPath(final String s1, final String s2,
        final HostInfo hostInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(s1);

        // If s1 ends with fp remove it
        if (sb.length() > 0) {
            if (sb.charAt(sb.length() - 1) == hostInfo.getOS().pathSeparator()) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }

        // Adds fp
        sb.append(hostInfo.getOS().pathSeparator());

        // If s2 begins with fp remove it
        if (s2.charAt(0) == hostInfo.getOS().pathSeparator()) {
            sb.append(s2.substring(1));
        } else {
            sb.append(s2);
        }

        return sb.toString();
    }
}
