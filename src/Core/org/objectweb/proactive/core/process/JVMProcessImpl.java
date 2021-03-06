/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.process;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;
import org.objectweb.proactive.core.util.RemoteProcessMessageLogger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.utils.OperatingSystem;


/**
 * <p>
 * The JVMProcess class is able to start localy any class of the ProActive library by
 * creating a Java Virtual Machine.
 * </p><p>
 * For instance:
 * </p>
 * <pre>
 * .............
 * JVMProcessImpl process = new JVMProcessImpl(new StandardOutputMessageLogger());
 * process.setClassname("org.objectweb.proactive.core.node.StartNode");
 * process.setParameters("nodeName");
 * process.startProcess();
 * .............
 * </pre>
 * <p>
 * This piece of code launches the ProActive java class org.objectweb.proactive.core.node.StartNode
 * with nodeName as parameter.
 * </p>
 * @author The ProActive Team
 * @version 1.0,  2002/09/20
 * @since   ProActive 0.9.4
 */
public class JVMProcessImpl extends AbstractExternalProcess implements JVMProcess, Serializable {
    static Logger logger = ProActiveLogger.getLogger(Loggers.DEPLOYMENT_PROCESS);

    //private final static String POLICY_FILE = "proactive.java.policy";
    private final static String POLICY_OPTION = " -Djava.security.policy=";
    private final static String LOG4J_OPTION = " -Dlog4j.configuration=file:";

    //private final static String LOG4J_FILE = "proactive-log4j";
    public final static String DEFAULT_CLASSPATH = convertClasspathToAbsolutePath(System
            .getProperty("java.class.path"));
    public final static String DEFAULT_JAVAPATH = System.getProperty("java.home") + File.separator + "bin" +
        File.separator + "java";
    public static String DEFAULT_POLICY_FILE = CentralPAPropertyRepository.JAVA_SECURITY_POLICY.getValue();
    public static String DEFAULT_LOG4J_FILE = CentralPAPropertyRepository.LOG4J.getValue();

    static {
        if (DEFAULT_POLICY_FILE != null) {
            DEFAULT_POLICY_FILE = getAbsolutePath(DEFAULT_POLICY_FILE);
        }
        if (DEFAULT_LOG4J_FILE != null) {
            DEFAULT_LOG4J_FILE = getAbsolutePath(DEFAULT_LOG4J_FILE);
        }
    }

    public final static String DEFAULT_CLASSNAME = org.objectweb.proactive.core.node.StartNode.class
            .getName();
    public final static String DEFAULT_JVMPARAMETERS = "";
    protected String classpath = DEFAULT_CLASSPATH;
    protected String bootClasspath;
    protected String javaPath = DEFAULT_JAVAPATH;
    protected String policyFile = DEFAULT_POLICY_FILE;
    protected String log4jFile = DEFAULT_LOG4J_FILE;
    protected String classname = DEFAULT_CLASSNAME;

    //    protected String parameters = DEFAULT_JVMPARAMETERS;
    //    protected String jvmParameters;
    protected StringBuffer parameters = new StringBuffer();
    protected StringBuffer jvmParameters = new StringBuffer();

    //this array will be used to know which options have been modified in case
    //this process extends anothe jvmprocess in the descriptor
    protected ArrayList<String> modifiedOptions;

    /**
     * This attributes is used when this jvm extends another one.
     * If set to yes, the jvm options of the extended jvm will be ignored.
     * If false, jvm options of this jvm will be appended to extended jvm ones. Default is false.
     */
    protected boolean overwrite = false;

    // How many paths leading to a JVMProcessImpl have been encountered
    static private int groupID = 0;
    protected PriorityLevel priority = PriorityLevel.normal;
    protected OperatingSystem os = OperatingSystem.getOperatingSystem();

    //
    // -- CONSTRUCTORS -----------------------------------------------
    //

    /**
     * Creates a new JVMProcess
     * Used with XML Descriptor
     */
    public JVMProcessImpl() {
        this.modifiedOptions = new ArrayList<String>();
    }

    /**
     * Creates a new JVMProcess
     * @param messageLogger The logger that handles input and error stream of this process
     */
    public JVMProcessImpl(RemoteProcessMessageLogger messageLogger) {
        super(messageLogger);
        this.modifiedOptions = new ArrayList<String>();
    }

    /**
     * Creates a new JVMProcess
     * @param inputMessageLogger The logger that handles input stream of this process
     * @param errorMessageLogger The logger that handles error stream of this process
     */
    public JVMProcessImpl(RemoteProcessMessageLogger inputMessageLogger,
            RemoteProcessMessageLogger errorMessageLogger) {
        super(inputMessageLogger, errorMessageLogger);
        this.modifiedOptions = new ArrayList<String>();
    }

    //
    // -- PUBLIC METHODS -----------------------------------------------
    //
    public static void main(String[] args) {
        try {
            JVMProcessImpl rsh = new JVMProcessImpl(new StandardOutputMessageLogger());
            rsh.setClassname(org.objectweb.proactive.core.node.StartNode.class.getName());
            rsh.setParameters(args[0]);
            rsh.startProcess();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    // -- implements JVMProcess -----------------------------------------------
    //
    public String getClasspath() {
        return classpath;
    }

    public void setClasspath(String classpath) {
        checkStarted();
        modifiedOptions.add("classpath");
        this.classpath = classpath;
    }

    public void setBootClasspath(String bootClasspath) {
        checkStarted();
        modifiedOptions.add("bootClasspath");
        this.bootClasspath = bootClasspath;
    }

    public String getBootClasspath() {
        return bootClasspath;
    }

    public String getJavaPath() {
        return javaPath;
    }

    public void setJavaPath(String javaPath) {
        checkStarted();
        if (javaPath == null) {
            throw new NullPointerException();
        }
        modifiedOptions.add("javaPath");
        this.javaPath = javaPath;
    }

    public String getPolicyFile() {
        return policyFile;
    }

    public void setPolicyFile(String policyFile) {
        checkStarted();
        modifiedOptions.add("policyFile");
        this.policyFile = policyFile;
    }

    public String getLog4jFile() {
        return log4jFile;
    }

    public void setLog4jFile(String log4jFile) {
        modifiedOptions.add("log4jFile");
        this.log4jFile = log4jFile;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        checkStarted();
        this.classname = classname;
    }

    public String getParameters() {
        return parameters.toString();
    }

    public void resetParameters() {
        this.parameters = new StringBuffer();
    }

    public void setParameters(String parameters) {
        checkStarted();
        this.parameters.append(parameters + " ");
    }

    public void setJvmOptions(String string) {
        checkStarted();
        jvmParameters.append(string + " ");
    }

    public String getJvmOptions() {
        return jvmParameters.toString();
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#getProcessId()
     */
    public String getProcessId() {
        return "jvm";
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#getNodeNumber()
     */
    public int getNodeNumber() {
        return 1;
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#getFinalProcess()
     */
    public UniversalProcess getFinalProcess() {
        return this;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public void setExtendedJVM(JVMProcessImpl jvmProcess) {
        changeSettings(jvmProcess);
    }

    //
    // -- PROTECTED METHODS -----------------------------------------------
    //
    @Override
    protected String buildCommand() {
        return buildJavaCommand();
    }

    protected String buildJavaCommand() {
        StringBuffer javaCommand = new StringBuffer();

        if (!priority.equals(PriorityLevel.normal)) {
            switch (os) {
                case unix:
                    javaCommand.append(priority.unixCmd());
                    break;
                case windows:
                    javaCommand.append(priority.windowsCmd());
                    break;
            }
        }

        // append java command
        if (javaPath == null) {
            javaCommand.append("java");
        } else {
            javaCommand.append(checkWhiteSpaces(javaPath));
        }

        if (bootClasspath != null) {
            javaCommand.append(" -Xbootclasspath:");
            javaCommand.append(checkWhiteSpaces(bootClasspath));
            javaCommand.append(" ");
        }

        // append jvmParameters
        if (jvmParameters != null) {
            javaCommand.append(" " + jvmParameters);
        }

        // append classpath
        if ((classpath != null) && (classpath.length() > 0)) {
            javaCommand.append(" -cp ");
            javaCommand.append(checkWhiteSpaces(classpath));
        }

        // append policy option
        if (policyFile != null) {
            javaCommand.append(POLICY_OPTION);
            javaCommand.append(checkWhiteSpaces(policyFile));
        }

        // append log4j option
        if (log4jFile != null) {
            javaCommand.append(LOG4J_OPTION);
            javaCommand.append(checkWhiteSpaces(log4jFile));
        }

        // append proactive policy File
        // if (securityFile != null) {
        //      javaCommand.append(PROACTIVE_POLICYFILE_OPTION);
        //      javaCommand.append(securityFile);
        //  }// else if (System.getProperty("proactive.runtime.security") != null) {
        //    javaCommand.append(PROACTIVE_POLICYFILE_OPTION);
        //      javaCommand.append(System.getProperty("proactive.runtime.security"));
        //	 }
        // append classname
        javaCommand.append(" ");
        javaCommand.append(classname);
        if (logger.isDebugEnabled()) {
            logger.debug("JVMProcessImpl.buildJavaCommand()  Parameters " + parameters);
        }
        if (parameters != null) {
            javaCommand.append(" ");
            javaCommand.append(parameters);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(javaCommand.toString());
        }
        if (logger.isDebugEnabled()) {
            logger.debug(javaCommand.toString() + "\n");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("JVMProcessImpl.buildJavaCommand() " + javaCommand);
        }
        return javaCommand.toString();
    }

    protected void changeSettings(JVMProcess jvmProcess) {
        if (!modifiedOptions.contains("classpath")) {
            this.classpath = jvmProcess.getClasspath();
        }
        if (!modifiedOptions.contains("bootClasspath")) {
            this.bootClasspath = jvmProcess.getBootClasspath();
        }
        if (!modifiedOptions.contains("javaPath")) {
            this.javaPath = jvmProcess.getJavaPath();
        }
        if (!modifiedOptions.contains("policyFile")) {
            this.policyFile = jvmProcess.getPolicyFile();
        }
        if (!modifiedOptions.contains("log4jFile")) {
            this.log4jFile = jvmProcess.getLog4jFile();
        }
        if (!overwrite) {
            setJvmOptions(jvmProcess.getJvmOptions());
        }
    }

    //
    // -- PRIVATE METHODS -----------------------------------------------
    //
    private static String convertClasspathToAbsolutePath(String classpath) {
        StringBuffer absoluteClasspath = new StringBuffer();
        String pathSeparator = File.pathSeparator;
        java.util.StringTokenizer st = new java.util.StringTokenizer(classpath, pathSeparator);
        while (st.hasMoreTokens()) {
            absoluteClasspath.append(new java.io.File(st.nextToken()).getAbsolutePath());
            absoluteClasspath.append(pathSeparator);
        }
        return absoluteClasspath.substring(0, absoluteClasspath.length() - 1);
    }

    private static String getAbsolutePath(String path) {
        if (path.startsWith("file:")) {
            //remove file part to build absolute path
            path = path.substring(5);
        }
        return new File(path).getAbsolutePath();
    }

    private String checkWhiteSpaces(String path) {
        if (!path.startsWith("\"") && !path.startsWith("'")) {
            //if path does not start with " or ' we can check if there is whitespaces, 
            //if it does, we let the user handle its path
            if (path.indexOf(" ") > 0) {
                //if whitespaces, we surround all the path with double quotes
                path = "\"" + path + "\"";
            }
        }
        return path;
    }

    public int getNewGroupId() {
        return groupID++;
    }

    public void setPriority(PriorityLevel priority) {
        this.priority = priority;
    }

    public void setOperatingSystem(OperatingSystem os) {
        this.os = os;
    }

    public OperatingSystem getOperatingSystem() {
        return os;
    }
}
