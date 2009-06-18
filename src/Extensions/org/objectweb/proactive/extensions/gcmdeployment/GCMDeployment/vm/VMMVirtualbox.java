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

import org.objectweb.proactive.core.httpserver.BootstrapServlet;
import org.objectweb.proactive.core.util.ProActiveCounter;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.CommandBuilderProActive;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.HostInfoImpl;
import org.ow2.proactive.virtualizing.core.error.VirtualServiceException;
import org.ow2.proactive.virtualizing.virtualbox.VirtualboxVM;
import org.ow2.proactive.virtualizing.virtualbox.VirtualboxVMM;
import org.ow2.proactive.virtualizing.virtualbox.VirtualboxVM.DataKey;


/**
 * This class is an implementation of the {@link AbstractVMM} class for
 * VirtualBox nonOse hypervisor. It is compulsory to run a non open source
 * copy of VirtualBox with that implementation since we need vboxwebsrv
 * to be started to deploy the virtual environment and vboxwebsrv is only
 * shipped with the non open source version. Please, note that the current
 * version of the implementation doesn't handles clones.
 * @author jmguilla
 *
 */
public class VMMVirtualbox extends AbstractVMM {

    /** Every registered hypervisors */
    private ArrayList<VirtualboxVMM> vmms = new ArrayList<VirtualboxVMM>();
    /** Every registered virtual machines */
    private ArrayList<VirtualboxVM> vms = new ArrayList<VirtualboxVM>();

    @Override
    public void start(CommandBuilderProActive comm, GCMApplicationInternal gcma) {
        //contact hypervisors & setup environment
        ArrayList<String> uris = super.getUris();
        for (String uri : uris) {
            VirtualboxVMM vmm;
            try {
                vmm = new VirtualboxVMM(uri, getUser(), getPwd());
            } catch (VirtualServiceException e) {
                GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to connect to " + uri + "'s hypervisor.", e);
                return;
            }
            ArrayList<VMBean> vmsMap = super.getVms();
            for (VMBean vm : vmsMap) {
                VirtualboxVM temp = null;
                try {
                    temp = vmm.getNewVM(vm.getId());
                } catch (VirtualServiceException e) {
                    GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to get " + vm.getId() +
                        " from Virtualbox hypervisor.", e);
                    continue;
                }
                if (vm.isClone()) {
                    GCMDeploymentLoggers.GCMD_LOGGER
                            .error("Clone feature isn't supported for Virtualbox hypervisor.");
                    GCMDeploymentLoggers.GCMD_LOGGER.error("Trying to boot the template...");
                }
                try {
                    startVM(temp, gcma, comm, vm.getHostInfo());
                } catch (Exception e) {
                    GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to start " + vm.getName() +
                        " from Virtualbox hypervisor.", e);
                }
                this.vms.add(temp);
            }
        }
    }

    @Override
    public void stop() {
        for (VirtualboxVM vm : vms) {
            try {
                vm.powerOff();
            } catch (VirtualServiceException e) {
                GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to power off the virtual machine " +
                    vm.getName());
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
    private void startVM(VirtualboxVM vm, GCMApplicationInternal gcma, CommandBuilderProActive comm,
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
            vm.pushData(DataKey.proacBootstrapURL, bootstrapAddress);
        } catch (VirtualServiceException e) {
            GCMDeploymentLoggers.GCMD_LOGGER
                    .error("Cannot pass the bootstrap URL to the virtual machine.", e);
        }
    }
}
