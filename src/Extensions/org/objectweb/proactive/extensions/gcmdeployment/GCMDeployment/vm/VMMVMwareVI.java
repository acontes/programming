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

import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.httpserver.BootstrapServlet;
import org.objectweb.proactive.core.httpserver.HTTPServer;
import org.objectweb.proactive.core.runtime.StartPARuntime;
import org.objectweb.proactive.core.util.ProActiveCounter;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;
import org.ow2.proactive.virtualizing.core.error.VirtualServiceException;
import org.ow2.proactive.virtualizing.vmwarevi.VMwareVM;
import org.ow2.proactive.virtualizing.vmwarevi.VMwareVMM;


public class VMMVMwareVI extends AbstractVMM {

    private ArrayList<VMwareVMM> vmms = new ArrayList<VMwareVMM>();
    //the associated boolean specify if the virtual is a clone.
    private Map<VMwareVM, Boolean> vms = new HashMap<VMwareVM, Boolean>();

    @Override
    public void start(GCMApplicationInternal gcma) {
        //contact hypervisors & setup environment
        ArrayList<String> uris = super.getUris();
        for (String uri : uris) {
            try {
                VMwareVMM vmm = new VMwareVMM(uri, getUser(), getPwd());
                ArrayList<VMBean> vmsMap = super.getVms();
                for (VMBean vm : vmsMap) {
                    VMwareVM temp = vmm.getNewVM(vm.getId());
                    if (!vm.isClone()) {
                        this.vms.put(temp, vm.isClone());
                        virtualMachineToTopologyImplMapper.put(temp, vm.getNode());
                    } else {
                        VMwareVM clone = temp.clone(vm.getName());
                        this.vms.put(clone, vm.isClone());
                        virtualMachineToTopologyImplMapper.put(clone, vm.getNode());
                    }
                }
            } catch (VirtualServiceException e) {
                GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to start virtual machine.", e);
            }
        }
        Set<VMwareVM> vmSet = vms.keySet();
        for (VMwareVM vm : vmSet) {
            startVM(vm, gcma);
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

    private void startVM(VMwareVM vm, GCMApplicationInternal gcma) {
        //register appli within boostrapServlet singleton
        BootstrapServlet bootstrapServlet = BootstrapServlet.get();
        String deploymentIdKey = new Long(gcma.getDeploymentId()).toString();
        long key = ProActiveCounter.getUniqID();
        String vmKey = deploymentIdKey + ":" + key;
        if (!bootstrapServlet.isRegistered(deploymentIdKey)) {
            String topologyId = "-" + StartPARuntime.Params.topologyId.shortOpt() + " ";
            topologyId += virtualMachineToTopologyImplMapper.get(vm).getId();
            HashMap<String, String> values = new HashMap<String, String>();
            values.put(BootstrapServlet.TOPOLOGY_ID, topologyId);
            if (!bootstrapServlet.registerAppli(vmKey, gcma, values)) {
                try {
                    GCMDeploymentLoggers.GCMD_LOGGER
                            .error("BootstrapServlet unable to register every needed properties to launch virtual machine " +
                                vm.getName());
                    GCMDeploymentLoggers.GCMD_LOGGER.error("Aborting");
                } catch (VirtualServiceException e) {
                    GCMDeploymentLoggers.GCMD_LOGGER
                            .error("A fatal error occured while handling an Exception report.");
                }
            }
        }

        try {
            vm.powerOn();
            try {
                String bootstrapAddress = bootstrapServlet.getBaseURI() + "?" + BootstrapServlet.VM_ID + "=" +
                    vmKey;
                GCMDeploymentLoggers.GCMD_LOGGER.info("Bootstrap servlet registered an app on:  " +
                    bootstrapAddress);
                vm.pushData(VMwareVM.DataKey.proacBootstrapURL, bootstrapAddress);
            } catch (VirtualServiceException e) {
                GCMDeploymentLoggers.GCMD_LOGGER.error(
                        "Cannot pass the bootstrap URL to the virtual machine.", e);
            }
        } catch (VirtualServiceException e1) {
            GCMDeploymentLoggers.GCMD_LOGGER.error("Cannot power on the virtual machine.", e1);
        }
    }
}
