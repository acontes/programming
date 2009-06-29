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

import org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.CommandBuilderProActive;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.HostInfoImpl;
import org.ow2.proactive.virtualizing.core.error.VirtualServiceException;
import org.ow2.proactive.virtualizing.libvirt.LibvirtVM;
import org.ow2.proactive.virtualizing.libvirt.LibvirtVMM;


/**
 * This class is an implementation of the {@link AbstractVMM} class for
 * Libvirt environment. Please, note that the current
 * version of the implementation doesn't handles clones.
 * You can check supported softwares & get information at:
 * {@linkplain http://libvirt.org/}
 * @author jmguilla
 *
 */
public class VMMLibvirt extends AbstractVMM {

    /** Every registered hypervisors */
    private ArrayList<LibvirtVMM> vmms = new ArrayList<LibvirtVMM>();
    /** Every registered virtual machines */
    private ArrayList<LibvirtVM> vms = new ArrayList<LibvirtVM>();

    @Override
    public void start(CommandBuilderProActive comm, GCMApplicationInternal gcma) {
        //contact hypervisors & setup environment
        ArrayList<String> uris = super.getUris();
        for (String uri : uris) {
            LibvirtVMM vmm;
            try {
                vmm = new LibvirtVMM(uri);
            } catch (VirtualServiceException e) {
                GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to connect to " + uri + "'s hypervisor.", e);
                return;
            }
            ArrayList<VMBean> vmsMap = super.getVms();
            for (VMBean vm : vmsMap) {
                LibvirtVM temp = null;
                try {
                    temp = vmm.getNewVM(vm.getId());
                } catch (VirtualServiceException e) {
                    GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to get " + vm.getId() +
                        " from Libvirt hypervisor.", e);
                    continue;
                }
                if (vm.isClone()) {
                    GCMDeploymentLoggers.GCMD_LOGGER
                            .error("Clone feature isn't supported for Libvirt hypervisor.");
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
        for (LibvirtVM vm : vms) {
            try {
                vm.powerOff();
            } catch (VirtualServiceException e) {
                GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to power off the virtual machine " +
                    vm.getName());
            }
        }
    }

    /**
     * This method is in charge of booting the virtual machine.
     * @param vm the virtual machine to boot
     * @param gcma the associated GCMA object
     * @param comm the CommandBuilderProActive used to build the command of the remote PART
     * @param hostInfo the hostinfo used to build the command
     * @throws VirtualServiceException if a problem occurs
     */
    private void startVM(LibvirtVM vm, GCMApplicationInternal gcma, CommandBuilderProActive comm,
            HostInfoImpl hostInfo) throws VirtualServiceException {
        vm.powerOn();
    }
}
