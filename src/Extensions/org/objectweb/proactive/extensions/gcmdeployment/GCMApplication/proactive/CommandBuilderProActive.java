/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.proactive;

import static org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers.GCMD_LOGGER;

import java.io.File;
import java.io.IOException;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.httpserver.ClassServerServlet;
import org.objectweb.proactive.core.runtime.RuntimeFactory;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers;
import org.objectweb.proactive.extensions.gcmdeployment.PathElement;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.CommandBuilder;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.HostInfo;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.Tool;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.Tools;
import org.objectweb.proactive.extensions.gcmdeployment.core.StartRuntime;


public class CommandBuilderProActive implements CommandBuilder {
    final static String PROACTIVE_JAR = "ProActive.jar";

    ApplicationProActiveConfigurationBean configBean;
  

    public CommandBuilderProActive(ApplicationProActiveConfigurationBean configBean) {
        GCMD_LOGGER.trace(this.getClass().getSimpleName() + " created");
        this.configBean = configBean;
    }

    /**
     * Returns the java executable to be used
     * 
     * <ol>
     * <li> Uses the java element inside GCMA/proactive/config </li>
     * <li> Uses the java tool defined by the hostInfo </li>
     * <li> returns "java" and lets the $PATH magic occur </li>
     * 
     * @param hostInfo
     * @return the java command to be used for this host
     */
    private String getJava(HostInfo hostInfo) {
        String javaCommand = "java";

        PathElement javaPath = configBean.getJavaPath();
        if (javaPath != null) {
            javaCommand = javaPath.getFullPath(hostInfo, this);
        } else {
            Tool javaTool = hostInfo.getTool(Tools.JAVA.id);
            if (javaTool != null) {
                javaCommand = javaTool.getPath();
            }
        }
        return "\"" + javaCommand + "\"";
    }

    /**
     * 
     * ProActive then Application
     * 
     * @param hostInfo
     * @param withCP
     *            whether to include "-cp" in the return string or not
     * @return
     */
    public String getClasspath(HostInfo hostInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("-cp \"");

        if (!configBean.isOverwriteClasspath()) {
            // ProActive.jar contains a JAR index
            // see: http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html#JAR%20Index
            char fs = hostInfo.getOS().fileSeparator();
            sb.append(getPath(hostInfo));
            sb.append(fs);
            sb.append("dist");
            sb.append(fs);
            sb.append("lib");
            sb.append(fs);
            sb.append(PROACTIVE_JAR);
            sb.append(hostInfo.getOS().pathSeparator());
        }

        if (configBean.getProactiveClasspath() != null) {
            for (PathElement pe : configBean.getProactiveClasspath()) {
                sb.append(pe.getFullPath(hostInfo, this));
                sb.append(hostInfo.getOS().pathSeparator());
            }
        }

        if (configBean.getApplicationClasspath() != null) {
            for (PathElement pe : configBean.getApplicationClasspath()) {
                sb.append(pe.getFullPath(hostInfo, this));
                sb.append(hostInfo.getOS().pathSeparator());
            }
        }

        // Trailing pathSeparator don't forget to remove it later
        return sb.substring(0, sb.length() - 1) + "\"";
    }

    public String buildCommand(HostInfo hostInfo, GCMApplicationInternal gcma) {

        if ((configBean.getProActivePath() == null) && (hostInfo.getTool(Tools.PROACTIVE.id) == null)) {
            throw new IllegalStateException(
                "ProActive installation path must be specified with the relpath attribute inside the proactive element (GCMA), or as tool in all hostInfo elements (GCMD). HostInfo=" +
                    hostInfo.getId());
        }

        if (!hostInfo.isCapacitiyValid()) {
            throw new IllegalStateException(
                "To enable capacity autodetection nor VM Capacity nor Host Capacity must be specified. HostInfo=" +
                    hostInfo.getId());
        }

        StringBuilder command = new StringBuilder();
        // Java
        command.append(getJava(hostInfo));
        command.append(" ");

        for (String arg : configBean.getJvmArgs()) {
            command.append(arg);
            command.append(" ");
        }

        if (PAProperties.PA_TEST.isTrue()) {
            command.append(PAProperties.PA_TEST.getCmdLine());
            command.append("true ");
        }

        if (PAProperties.PA_CLASSLOADER.isTrue() ||
            "org.objectweb.proactive.core.classloader.ProActiveClassLoader".equals(System
                    .getProperty("java.system.class.loader"))) {
            command
                    .append(" -Djava.system.class.loader=org.objectweb.proactive.core.classloader.ProActiveClassLoader ");
            // the following allows the deserializing of streams that were annotated with rmi utilities
            command
                    .append(" -Djava.rmi.server.RMIClassLoaderSpi=org.objectweb.proactive.core.classloader.ProActiveRMIClassLoaderSpi");
            // to avoid clashes due to multiple classloader, we initiate the
            // configuration of log4j ourselves 
            // (see StartRuntime.main)
            command.append(" -Dlog4j.defaultInitOverride=true ");
        }

        // Class Path: ProActive then Application
        command.append(getClasspath(hostInfo));
        command.append(" ");

        // Log4j
        PathElement log4jProperties = configBean.getLog4jProperties();
        if (log4jProperties != null) {
            command.append(PAProperties.LOG4J.getCmdLine());
            command.append("\"");
            command.append("file:");
            command.append(log4jProperties.getFullPath(hostInfo, this));
            command.append("\"");
            command.append(" ");
        }

        // Java Security Policy
        PathElement javaSecurityPolicy = configBean.getJavaSecurityPolicy();
        if (javaSecurityPolicy != null) {
            command.append(PAProperties.JAVA_SECURITY_POLICY.getCmdLine());
            command.append("\"");
            command.append(javaSecurityPolicy.getFullPath(hostInfo, this));
            command.append("\"");
            command.append(" ");
        } else {
            command.append(PAProperties.JAVA_SECURITY_POLICY.getCmdLine());
            command.append("\"");
            command.append(PAProperties.JAVA_SECURITY_POLICY.getValue());
            command.append("\"");
            command.append(" ");
        }

        if (hostInfo.getNetworkInterface() != null) {
            command.append(PAProperties.PA_NET_INTERFACE.getCmdLine() + hostInfo.getNetworkInterface());
            command.append(" ");
        }

        PathElement runtimePolicy = configBean.getRuntimePolicy();
        if (runtimePolicy != null) {
            command.append(PAProperties.PA_RUNTIME_SECURITY.getCmdLine());
            command.append("\"");
            command.append(runtimePolicy.getFullPath(hostInfo, this));
            command.append("\"");
            command.append(" ");
        }

        // Class to be started and its arguments
        command.append(StartRuntime.class.getName());
        command.append(" ");

        String parentURL;
        try {
            parentURL = RuntimeFactory.getDefaultRuntime().getURL();
        } catch (ProActiveException e) {
            GCMD_LOGGER.error(
                    "Cannot determine the URL of this runtime. Childs will not be able to register", e);
            parentURL = "unkownParentURL";
        }
        command.append("-" + StartRuntime.Params.parent.shortOpt() + " " + parentURL);
        command.append(" ");

        if (hostInfo.getVmCapacity() != 0) {
            command.append("-" + StartRuntime.Params.capacity.shortOpt() + " " + hostInfo.getVmCapacity());
            command.append(" ");
        }

        command.append("-" + StartRuntime.Params.topologyId.shortOpt() + " " + hostInfo.getToplogyId());
        command.append(" ");

        command.append("-" + StartRuntime.Params.deploymentId.shortOpt() + " " + gcma.getDeploymentId());
        command.append(" ");

        command.append("-" + StartRuntime.Params.codebase.shortOpt() + " " +
            ClassServerServlet.get().getCodeBase());
        command.append(" ");

        // TODO cdelbe Check FT properties here
        // was this.ftService.buildParamsLine();

        StringBuilder ret = new StringBuilder();

        if (hostInfo.getHostCapacity() == 0) {
            ret.append(command);
        } else {
            switch (hostInfo.getOS()) {
                case unix:
                    for (int i = 0; i < hostInfo.getHostCapacity(); i++) {
                        ret.append(command);
                        ret.append(" &");
                    }
                    ret.deleteCharAt(ret.length() - 1);
                    break;

                case windows:
                    char fs = hostInfo.getOS().fileSeparator();
                    ret.append("\"");
                    ret.append(getPath(hostInfo));
                    ret.append(fs);
                    ret.append("dist");
                    ret.append(fs);
                    ret.append("scripts");
                    ret.append(fs);
                    ret.append("gcmdeployment");
                    ret.append(fs);
                    ret.append("startn.bat");
                    ret.append("\"");

                    ret.append(" ");
                    ret.append(hostInfo.getHostCapacity());

                    ret.append(" ");
                    ret.append("\"");
                    ret.append(command);
                    ret.append("\"");
                    break;
            }
        }

        GCMD_LOGGER.trace(ret);
        return ret.toString();
    }

    public String getPath(HostInfo hostInfo) {
        PathElement proActivePath = configBean.getProActivePath();
        if (proActivePath != null) {
            return proActivePath.getFullPath(hostInfo, this);
        }

        return null;
    }

    private static String getAbsolutePath(String path) {
        if (path.startsWith("file:")) {
            //remove file part to build absolute path
            path = path.substring(5);
        }
        try {
            return new File(path).getCanonicalPath();
        } catch (IOException e) {
            GCMDeploymentLoggers.GCMA_LOGGER.error(e.getMessage());
            return path;
        }
    }
}
