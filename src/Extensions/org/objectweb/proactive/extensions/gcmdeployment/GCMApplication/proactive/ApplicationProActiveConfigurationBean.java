package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.proactive;

import static org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers.GCMD_LOGGER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.proactive.core.security.ProActiveSecurityManager;
import org.objectweb.proactive.extensions.gcmdeployment.PathElement;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.TechnicalServicesProperties;
import org.objectweb.proactive.extensions.gcmdeployment.core.GCMVirtualNodeInternal;

public class ApplicationProActiveConfigurationBean {

    /** Path to the ProActive installation */
    private PathElement proActivePath;

    /** Declared Virtual nodes */
    private Map<String, GCMVirtualNodeInternal> vns;

    /** Path to ${java.home}/bin/java */
    private PathElement javaPath = null;

    /** Arguments to be passed to java */
    private List<String> jvmArgs;

    /**
     * ProActive classpath
     * 
     * If not set, then the default classpath is used
     */
    private List<PathElement> proactiveClasspath;
    private boolean overwriteClasspath;

    /** Application classpath */
    private List<PathElement> applicationClasspath;

    /** Security Policy file */
    private PathElement javaSecurityPolicy;

    /** Log4j configuration file */
    private PathElement log4jProperties;

    /** User properties file */
    private PathElement userProperties;

    private ProActiveSecurityManager psm;
    
    private PathElement securityPolicy;
    
    /** application security policy file */
    private PathElement applicationPolicy;

    /** runtime security policy file */
    private PathElement runtimePolicy;
    
    private TechnicalServicesProperties applicationLevelTechnicalSerives;

    public ApplicationProActiveConfigurationBean() {
        vns = new HashMap<String, GCMVirtualNodeInternal>();
        jvmArgs = new ArrayList<String>();
    }
    
    
    protected PathElement getProActivePath() {
        return proActivePath;
    }

    protected void setProActivePath(PathElement proActivePath) {
        this.proActivePath = proActivePath;
    }

    protected Map<String, GCMVirtualNodeInternal> getVns() {
        return vns;
    }

    protected void addVN(GCMVirtualNodeInternal vn) {
        this.vns.put(vn.getName(), vn);
    }

    protected PathElement getJavaPath() {
        return javaPath;
    }

    protected void setJavaPath(PathElement javaPath) {
        this.javaPath = javaPath;
    }

    protected List<String> getJvmArgs() {
        return jvmArgs;
    }

    protected void addJvmArg(String jvmArg) {
        this.jvmArgs.add(jvmArg);
    }

    protected List<PathElement> getProactiveClasspath() {
        return proactiveClasspath;
    }

    protected void setProactiveClasspath(List<PathElement> proactiveClasspath) {
        this.proactiveClasspath = proactiveClasspath;
    }

    protected boolean isOverwriteClasspath() {
        return overwriteClasspath;
    }

    protected void setOverwriteClasspath(boolean overwriteClasspath) {
        this.overwriteClasspath = overwriteClasspath;
    }

    protected List<PathElement> getApplicationClasspath() {
        return applicationClasspath;
    }

    protected PathElement getJavaSecurityPolicy() {
        return javaSecurityPolicy;
    }

    protected void setJavaSecurityPolicy(PathElement javaSecurityPolicy) {
        this.javaSecurityPolicy = javaSecurityPolicy;
    }

    protected PathElement getLog4jProperties() {
        return log4jProperties;
    }

    protected void setLog4jProperties(PathElement log4jProperties) {
        this.log4jProperties = log4jProperties;
    }

    protected PathElement getUserProperties() {
        return userProperties;
    }

    protected void setUserProperties(PathElement userProperties) {
        this.userProperties = userProperties;
    }

    protected PathElement getApplicationPolicy() {
        return applicationPolicy;
    }

    protected void setApplicationPolicy(PathElement applicationPolicy) {
        this.applicationPolicy = applicationPolicy;
    }

    protected PathElement getRuntimePolicy() {
        return runtimePolicy;
    }

    protected void setRuntimePolicy(PathElement runtimePolicy) {
        this.runtimePolicy = runtimePolicy;
    }
    
    public void addProActivePath(PathElement pe) {
        if (proactiveClasspath == null) {
            proactiveClasspath = new ArrayList<PathElement>();
        }

        proactiveClasspath.add(pe);
    }

    public void setProActiveClasspath(List<PathElement> pe) {
        proactiveClasspath = pe;
    }

    public void addApplicationPath(PathElement pe) {
        if (applicationClasspath == null) {
            applicationClasspath = new ArrayList<PathElement>();
        }

        applicationClasspath.add(pe);
    }

    public void setApplicationClasspath(List<PathElement> pe) {
        if (GCMD_LOGGER.isTraceEnabled()) {
            GCMD_LOGGER.trace(" Set ApplicationClasspath to:");
            for (PathElement e : pe) {
                GCMD_LOGGER.trace("\t" + e);
            }
        }

        applicationClasspath = pe;
    }
    
    public void setProActivePath(String proActivePath, String base) {
        if (proActivePath != null) {
            this.proActivePath = new PathElement(proActivePath, base);
            GCMD_LOGGER.trace(" Set ProActive relpath to " + this.proActivePath.getRelPath());
        }
    }


    protected ProActiveSecurityManager getPsm() {
        return psm;
    }


    protected void setPsm(ProActiveSecurityManager psm) {
        this.psm = psm;
    }


    protected PathElement getSecurityPolicy() {
        return securityPolicy;
    }


    protected void setSecurityPolicy(PathElement securityPolicy) {
        this.securityPolicy = securityPolicy;
    }


    protected TechnicalServicesProperties getApplicationLevelTechnicalSerives() {
        return applicationLevelTechnicalSerives;
    }


    protected void setApplicationLevelTechnicalSerives(
            TechnicalServicesProperties applicationLevelTechnicalSerives) {
        this.applicationLevelTechnicalSerives = applicationLevelTechnicalSerives;
    }
    
}
