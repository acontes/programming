/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.core.process.glite;

import javax.naming.directory.InvalidAttributeValueException;

import org.glite.wms.jdlj.*; // /lib/glite/glite-wms-jdlj.jar
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.process.AbstractExternalProcessDecorator;
import org.objectweb.proactive.core.process.ExternalProcess;
import org.objectweb.proactive.core.process.JVMProcess;
import org.objectweb.proactive.core.process.UniversalProcess;
import org.objectweb.proactive.core.process.filetransfer.FileDependant;
import org.objectweb.proactive.core.process.filetransfer.FileTransferDefinition;


/**
 * GLite Process implementation.
 * This implementation works only for ProActive deployment, and not to submit single commands
 * @author  ProActive Team
 * @version 1.0,  2005/09/20
 * @since   ProActive 3.0
 */
public class GLiteProcess extends AbstractExternalProcessDecorator
    implements FileDependant {

    /**
     * Firsts parameters
     */
    private static final long serialVersionUID = 1L;
    private static final String FILE_SEPARATOR = System.getProperty(
            "file.separator");
    protected static final String DEFAULT_PROCESSOR_NUMBER = "1";
    protected static final String DEFAULT_COMMAND_PATH = "glite-job-submit";
    protected static final String DEFAULT_FILE_LOCATION = System.getProperty(
            "user.home") + FILE_SEPARATOR + "public" + FILE_SEPARATOR + "JDL";
    protected static final String DEFAULT_STDOUPUT = System.getProperty(
            "user.home") + FILE_SEPARATOR + "out.log";
    protected static final String DEFAULT_CONFIG_FILE = System.getProperty(
            "user.home") + FILE_SEPARATOR + "public" + FILE_SEPARATOR + "JDL" +
        FILE_SEPARATOR + "vo.conf";
    protected int jobID;
    protected String hostList;
    protected String processor = DEFAULT_PROCESSOR_NUMBER;
    protected String command_path = DEFAULT_COMMAND_PATH;
    protected String interactive = "false";
    protected String filePath = DEFAULT_FILE_LOCATION;
    protected String stdOutput = DEFAULT_STDOUPUT;
    protected String fileName = "job.jdl";
    protected String configFile = DEFAULT_CONFIG_FILE;
    protected String remoteFilePath = null;
    protected boolean confFileOption = false;
    protected boolean jdlRemote = false;
    protected String netServer;
    protected String logBook;
    protected int cpuNumber = 1;

    // WARNING : variable appartenant a toutes les instances de la classe GLiteProcess
    public static GLiteJobAd jad;

    /**
     * Create a new GLiteProcess
     * Used with XML Descriptors
     */
    public GLiteProcess() {
        super();
        setCompositionType(COPY_FILE_AND_APPEND_COMMAND);
        this.hostname = null;
        command_path = DEFAULT_COMMAND_PATH;
        jad = new GLiteJobAd();
    }

    /**
     * Create a new GLiteProcess
     * @param targetProcess The target process associated to this process. The target process
     * represents the process that will be launched with the glite-job-submit command
     */
    public GLiteProcess(ExternalProcess targetProcess) {
        super(targetProcess);
        setCompositionType(COPY_FILE_AND_APPEND_COMMAND);
        this.hostname = null;
        jad = new GLiteJobAd();
    }

    public static void main(String[] args) {
        ProActiveDescriptor pad;
        try {
            pad = ProActive.getProactiveDescriptor(args[0]);
            pad.activateMappings();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the jdl file with all the options specified in the descriptor
     */
    public void buildJdlFile() {
        StringBuffer gLiteCommand = new StringBuffer();
        String args;
        gLiteCommand.append(command_path);
        String initial_args = ((JVMProcess) getTargetProcess()).getCommand();
        String java_cmd = System.getProperty("java.home") + "/bin/java";

        //if(jdlRemote) {
        //args = initial_args.substring(initial_args.indexOf("-Djava"));
        //}else { 
        args = initial_args.substring(initial_args.indexOf(java_cmd) +
                java_cmd.length());
        //}
        args = checkSyntax(args);

        try {
            if (jad.hasAttribute(Jdl.ARGUMENTS)) {
                jad.delAttribute(Jdl.ARGUMENTS);
            }
            jad.setAttribute(Jdl.ARGUMENTS, args);
            jad.toFile(filePath + "/" + fileName);

            //examples of requirements
            //jad.setAttributeExpr(Jdl.REQUIREMENTS,"other.GlueCEUniqueID ==\"pps-ce.egee.cesga.es:2119/blah-pbs-picard\"");
            //jad.setAttributeExpr(Jdl.REQUIREMENTS, "!(RegExp(\"*lxb2039*\",other.GlueCEUniqueID))");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String internalBuildCommand() {
        return buildGLiteCommand();
    }

    /**
     * Add java arguments to the jdl file.
     * Set the name of jdl file.
     * Mandatory attributes : Requirements, rank'
     *
     * @throws llegalArgumentException, Exception
     * @return Empty string. Command line is not necessary there.
     */
    protected String buildGLiteCommand() {
        String path = filePath;
        buildJdlFile();

        if (jdlRemote) {
            path = remoteFilePath;
        }

        if (!confFileOption) {
            return DEFAULT_COMMAND_PATH + " " + path + FILE_SEPARATOR +
            fileName;
        }

        return DEFAULT_COMMAND_PATH + " --config-vo " + configFile + " " +
        path + FILE_SEPARATOR + fileName;
    }

    /**
     * Check is java arguments are well formatted.
     * @param java arguments
     * @return java argments well formatted
     */
    private String checkSyntax(String args) {
        String formatted_args = "";
        String[] splitted_args = args.split("\\s");
        for (int i = 0; i < splitted_args.length; i++) {
            if (!(splitted_args[i].indexOf("=") < 0)) {
                splitted_args[i] = "\"" + splitted_args[i] + "\"";
            }
            formatted_args = formatted_args + " " + splitted_args[i];
        }
        return formatted_args;
    }

    /************************************************************************
     *                              GETTERS AND SETTERS                     *
     ************************************************************************/

    /* (non-Javadoc)
     * @see org.objectweb.proactive.core.process.UniversalProcess#getProcessId()
     */
    public String getProcessId() {
        return "glite_" + targetProcess.getProcessId();
    }

    public int getNodeNumber() {
        return (new Integer(getProcessorNumber()).intValue());
    }

    /**
     * Returns the number of processor requested for the job
     * @return String
     */
    public String getProcessorNumber() {
        return processor;
    }

    public UniversalProcess getFinalProcess() {
        checkStarted();
        return targetProcess.getFinalProcess();
    }

    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName The fileName to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return Returns the filePath.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath The filePath to set.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return Returns the command_path.
     */
    public String getCommand_path() {
        return command_path;
    }

    /**
     * @param command_path The command_path to set.
     */
    public void setCommand_path(String command_path) {
        this.command_path = command_path;
    }

    /**
     * @return Returns the jad.
     */
    public GLiteJobAd getJad() {
        return jad;
    }

    /**
     * @param attrName attributes to add to the GliteJobAd object
     * @param attrValue value of the atributes
     * @throws InvalidAttributeValueException
     * @throws IllegalArgumentException
     */
    public void addAtt(String attrName, Ad attrValue) throws Exception {
        jad.addAttribute(attrName, attrValue);
    }

    /**
     * @param attrName attributes to add to the GliteJobAd object
     * @param attrValue value of the added attrName
     * @throws InvalidAttributeValueException
     * @throws IllegalArgumentException
     */
    public void addAtt(String attrName, int attrValue)
        throws Exception {
        jad.addAttribute(attrName, attrValue);
    }

    /**
     * @param attrName attributes to add to the GliteJobAd object
     * @param attrValue value of the added attrName
     * @throws InvalidAttributeValueException
     * @throws IllegalArgumentException
     */
    public void addAtt(String attrName, double attrValue)
        throws Exception {
        jad.addAttribute(attrName, attrValue);
    }

    /**
     * @param attrName attributes to add to the GliteJobAd object
     * @param attrValue value of the added attrName
     * @throws InvalidAttributeValueException
     * @throws IllegalArgumentException
     */
    public void addAtt(String attrName, String attrValue)
        throws Exception {
        jad.addAttribute(attrName, attrValue);
    }

    /**
     * @param attrName attributes to add to the GliteJobAd object
     * @param attrValue value of the added attrName
     * @throws InvalidAttributeValueException
     * @throws IllegalArgumentException
     */
    public void addAtt(String attrName, boolean attrValue)
        throws Exception {
        jad.addAttribute(attrName, attrValue);
    }

    /**
     * @return Returns the netServer.
     */
    public String getNetServer() {
        return netServer;
    }

    /**
     * @param netServer The netServer to set.
     */
    public void setNetServer(String netServer) {
        this.netServer = netServer;
    }

    /**
     * @return Returns the configFile.
     */
    public String getConfigFile() {
        return configFile;
    }

    /**
     * @param configFile The configFile to set.
     */
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public void setConfigFileOption(boolean b) {
        confFileOption = b;
    }

    /**
     * @return Returns the jdlRemote.
     */
    public boolean isJdlRemote() {
        return jdlRemote;
    }

    /**
     * @param jdlRemote The jdlRemote to set.
     */
    public void setJdlRemote(boolean jdlRemote) {
        this.jdlRemote = jdlRemote;
    }

    /**
     * @return Returns the remoteFilePath.
     */
    public String getRemoteFilePath() {
        return remoteFilePath;
    }

    /**
     * @param remoteFilePath The remoteFilePath to set.
     */
    public void setRemoteFilePath(String remoteFilePath) {
        this.remoteFilePath = remoteFilePath;
    }

    public FileTransferDefinition getFileTransfertDefiniton() {
        FileTransferDefinition ft = new FileTransferDefinition("gliteProcess");
        ft.addFile(filePath + "/" + fileName, remoteFilePath + "/" + fileName);
        return ft;
    }

	public int getCpuNumber() {
		return cpuNumber;
	}

	public void setCpuNumber(int cpuNumber) {
		this.cpuNumber = cpuNumber;
	}

	

    /******************************************************************************************
     *                                END OF GETTERS AND SETTERS                              *
     ******************************************************************************************/
}
