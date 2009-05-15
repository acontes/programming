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

import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.httpserver.BootstrapServlet;
import org.objectweb.proactive.core.httpserver.HTTPServer;
import org.objectweb.proactive.core.runtime.StartPARuntime;
import org.objectweb.proactive.core.util.ProActiveCounter;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;
import org.ow2.proactive.virtualizing.core.error.VirtualServiceException;
import org.ow2.proactive.virtualizing.vmwarevix.VMwareVM;
import org.ow2.proactive.virtualizing.vmwarevix.VMwareVMM;
import org.ow2.proactive.virtualizing.vmwarevix.VMwareVMM.Service;


public class VMMVMwareVix extends AbstractVMM {

    private ArrayList<VMwareVMM> vmms = new ArrayList<VMwareVMM>();
    private ArrayList<VMwareVM> vms = new ArrayList<VMwareVM>();
    private int port = -1;
    private Service service = Service.vmwareServerVI;

    @Override
    public void start(GCMApplicationInternal gcma) {
        //contact hypervisors & setup environment
        ArrayList<String> uris = super.getUris();
        for (String uri : uris) {
            try {
                VMwareVMM vmm = getVMM(uri);
                ArrayList<VMBean> vmsMap = super.getVms();
                for (VMBean vm : vmsMap) {
                    VMwareVM temp = vmm.getNewVM(vm.getId());
                    if (!vm.isClone()) {
                        this.vms.add(temp);
                        virtualMachineToTopologyImplMapper.put(temp, vm.getNode());
                    } else {
                        GCMDeploymentLoggers.GCMD_LOGGER.error("Unsupported clone feature for vmware vix.");
                    }
                }
            } catch (VirtualServiceException e) {
                GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to start virtual machine.", e);
            }
        }
        for (VMwareVM vm : vms) {
            startVM(vm, gcma);
        }
    }

    private VMwareVMM getVMM(String uri) throws VirtualServiceException {
        String user = getUser();
        String pwd = getPwd();
        int port = getPort();
        if (port == -1 && user == null && pwd == null)
            return new VMwareVMM(uri);
        else if (port == -1 && user != null && pwd != null)
            return new VMwareVMM(uri, user, pwd, getService());
        else if (user == null && pwd == null && port != -1)
            return new VMwareVMM(port, getService());
        else
            return new VMwareVMM(uri, user, pwd, port, getService());
    }

    @Override
    public void stop() {
        for (VMwareVM vm : vms) {
            try {
                vm.powerOff();
            } catch (VirtualServiceException e) {
                GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to power off the virtual machine " +
                    vm.getName());
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
                GCMDeploymentLoggers.GCMD_LOGGER
                        .error("BootstrapServlet unable to register every needed properties to launch virtual machine " +
                            vm.getName());
                GCMDeploymentLoggers.GCMD_LOGGER.error("Aborting");
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

    /*----------------------
     * Getters & Setters
     ---------------------*/
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
