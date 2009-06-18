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
package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.objectweb.proactive.core.httpserver.BootstrapServlet;
import org.objectweb.proactive.core.util.ProActiveCounter;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.CommandBuilderProActive;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.HostInfoImpl;
import org.ow2.proactive.virtualizing.core.error.VirtualServiceException;
import org.ow2.proactive.virtualizing.vmwarevi.VMwareVM;
import org.ow2.proactive.virtualizing.vmwarevi.VMwareVMM;


/**
 * An implementation of {@link AbstractVMM} based on VMware Virtual Infrastructure API.
 * This implementation is designed to handle every ESX3.x, ESXi, Server 2.x environments.
 * See {@link VMMVMwareVix} for other supports.
 * Note that this environment emulate the clone feature as not every hypervisors handle it.
 * See documentation of {@link VMwareVM} for more information.
 * @author jmguilla
 *
 */
public class VMMVMwareVI extends AbstractVMM {

    /** Every registered hypervisors */
    private ArrayList<VMwareVMM> vmms = new ArrayList<VMwareVMM>();
    //the associated boolean specify if the virtual is a clone.
    /** Every registered virtual machines */
    private Map<VMwareVM, Boolean> vms = new HashMap<VMwareVM, Boolean>();

    @Override
    public void start(CommandBuilderProActive comm, GCMApplicationInternal gcma) {
        //contact hypervisors & setup environment
        ArrayList<String> uris = super.getUris();
        for (String uri : uris) {
            VMwareVMM vmm;
            try {
                vmm = new VMwareVMM(uri, getUser(), getPwd());
            } catch (VirtualServiceException e) {
                GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to connect to " + uri + "'s hypervisor.", e);
                return;
            }
            ArrayList<VMBean> vmsMap = super.getVms();
            for (VMBean vm : vmsMap) {
                VMwareVM temp = null;
                try {
                    temp = vmm.getNewVM(vm.getId());
                } catch (VirtualServiceException e) {
                    GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to get " + vm.getId() +
                        " from VMware hypervisor.", e);
                    continue;
                }
                if (vm.isClone()) {
                    try {
                        temp = temp.clone(vm.getName());
                    } catch (VirtualServiceException e) {
                        GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to clone " + vm.getId() +
                            " from VMware hypervisor.", e);
                        continue;
                    }
                }
                try {
                    startVM(temp, gcma, comm, vm.getHostInfo());
                } catch (Exception e) {
                    GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to start " + vm.getName() +
                        " from VMware hypervisor.", e);
                    if (vm.isClone()) {
                        try {
                            temp.destroy();
                        } catch (VirtualServiceException e1) {
                            GCMDeploymentLoggers.GCMD_LOGGER.error(
                                    "A sever exception occured while destroying unused " + vm.getName() +
                                        " virtual machine." + System.getProperty("line.separator") +
                                        ". You have to do it by yourself, be careful.", e);
                            continue;
                        }
                    }
                }
                this.vms.put(temp, vm.isClone());
            }
        }
    }

    @Override
    public void stop() {
        Set<VMwareVM> vmSet = vms.keySet();
        for (VMwareVM vm : vmSet) {
            try {
                vm.powerOff();
            } catch (VirtualServiceException e) {
                try {
                    GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to power off the virtual machine " +
                        vm.getName());
                } catch (VirtualServiceException e1) {
                    GCMDeploymentLoggers.GCMD_LOGGER
                            .error("A sever error occured while powering off a virtual machine");
                }
            }
            if (vms.get(vm))
                try {
                    vm.destroy();
                } catch (VirtualServiceException e) {
                    try {
                        GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to destroy the virtual machine " +
                                vm.getName());
                    } catch (VirtualServiceException e1) {
                        GCMDeploymentLoggers.GCMD_LOGGER
                                .error("A sever error occured while destroying a virtual machine");
                    }
                }
        }
    }

    /**
     * This method is in charge of booting the virtual machine and registering a web page
     * ( see {@link BootstrapServlet} ) to allow remote PART to bootstrap.
     * @param vm the virtual machine to boot
     * @param gcma the associated GCMA object
     * @param comm the CommandBuilderProActive used to build the command of the remote PART
     * @param hostInfo the hostinfo used to build the command
     * @throws VirtualServiceException if a problem occurs
     */
    private void startVM(VMwareVM vm, GCMApplicationInternal gcma, CommandBuilderProActive comm,
            HostInfoImpl hostInfo) throws VirtualServiceException {
        //register appli within boostrapServlet singleton
        BootstrapServlet bootstrapServlet = BootstrapServlet.get();
        String deploymentIdKey = new Long(gcma.getDeploymentId()).toString();
        long key = ProActiveCounter.getUniqID();
        String vmKey = deploymentIdKey + ":" + key;
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(BootstrapServlet.PA_RT_COMMAND, comm.buildCommand(hostInfo, gcma));
        String bootstrapAddress = bootstrapServlet.registerAppli(vmKey, values);
        vm.powerOn();
        try {
            vm.pushData(VMwareVM.DataKey.proacBootstrapURL, bootstrapAddress);
        } catch (VirtualServiceException e) {
            GCMDeploymentLoggers.GCMD_LOGGER
                    .error("Cannot pass the bootstrap URL to the virtual machine.", e);
        }
    }
}
