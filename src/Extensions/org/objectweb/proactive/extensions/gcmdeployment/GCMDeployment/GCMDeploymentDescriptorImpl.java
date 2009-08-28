/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment;

import static org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers.GCMD_LOGGER;

import java.net.URL;
import java.util.List;

import org.objectweb.proactive.core.xml.VariableContractImpl;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.CommandBuilder;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.CommandBuilderProActive;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.bridge.Bridge;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.group.Group;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.HostInfo;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.vm.AbstractVMM;


public class GCMDeploymentDescriptorImpl implements GCMDeploymentDescriptor {
    private GCMDeploymentParser parser;
    private VariableContractImpl environment;
    private GCMDeploymentResources resources;
    private GCMDeploymentAcquisition acquisitions;

    public GCMDeploymentDescriptorImpl(URL descriptor, VariableContractImpl vContract) throws Exception {
        parser = new GCMDeploymentParserImpl(descriptor, vContract);
        environment = parser.getEnvironment();
        resources = parser.getResources();
        acquisitions = parser.getAcquisitions();
    }

    public void start(CommandBuilder commandBuilder, GCMApplicationInternal gcma) {
        // Start Local JVMs
        startLocal(commandBuilder, gcma);

        startGroups(commandBuilder, gcma);
        startBridges(commandBuilder, gcma);
        startVMs(commandBuilder, gcma);
    }

    private void startLocal(CommandBuilder commandBuilder, GCMApplicationInternal gcma) {
        HostInfo hostInfo = resources.getHostInfo();
        if (hostInfo != null) {
            // Something needs to be started on this host
            String command = commandBuilder.buildCommand(hostInfo, gcma);

            GCMD_LOGGER.info("Starting a process on localhost");
            GCMD_LOGGER.debug("command= " + command);
            Executor.getExecutor().submit(command);
        }
    }

    private void startGroups(CommandBuilder commandBuilder, GCMApplicationInternal gcma) {
        List<Group> groups = resources.getGroups();
        for (Group group : groups) {
            List<String> commands = group.buildCommands(commandBuilder, gcma);
            GCMD_LOGGER.info("Starting group id=" + group.getId() + " #commands=" + commands.size());

            for (String command : commands) {
                GCMD_LOGGER.debug("group id=" + group.getId() + " command= " + command);
                Executor.getExecutor().submit(command);
            }
        }
    }

    private void startBridges(CommandBuilder commandBuilder, GCMApplicationInternal gcma) {
        List<Bridge> bridges = resources.getBridges();
        for (Bridge bridge : bridges) {
            List<String> commands = bridge.buildCommands(commandBuilder, gcma);

            GCMD_LOGGER.info("Starting bridge id=" + bridge.getId() + " #commands=" + commands.size());

            for (String command : commands) {
                GCMD_LOGGER.debug("bridge id=" + bridge.getId() + " command= " + command);
                Executor.getExecutor().submit(command);
            }
        }
    }

    /**
     * Starts all registered virtual machines registered within a proprietary hypervisor tag
     * from an associated GCMD file
     * @param commandBuilder the commandBuilder that will be used to build the remote proactive runtime command
     * @param gcma the gcma object tied to this application
     */
    private void startVMs(CommandBuilder commandBuilder, GCMApplicationInternal gcma) {
        try {
            List<AbstractVMM> vmms = resources.getVMM();
            for (AbstractVMM vmm : vmms) {
                //since deployment with virtualization is meaningless outside of a ProActive application
                //it is compulsory to downcast the commandBuilder to be able to bootstrap remote PART
            	GCMDeploymentLoggers.GCMD_LOGGER.debug("VMM with refid " + vmm.getId() + " is going to boot");
                vmm
                        .start(
                                commandBuilder instanceof CommandBuilderProActive ? (CommandBuilderProActive) commandBuilder
                                        : null, gcma);
            }
        } catch (Exception e) {
            GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to start virtual machines.", e);
        }
    }

    public VariableContractImpl getEnvironment() {
        return environment;
    }

    public GCMDeploymentResources getResources() {
        return resources;
    }

    public GCMDeploymentParser getParser() {
        return parser;
    }

    public GCMDeploymentAcquisition getAcquisitions() {
        return acquisitions;
    }

    public URL getDescriptorURL() {
        return parser.getDescriptorURL();
    }
}
