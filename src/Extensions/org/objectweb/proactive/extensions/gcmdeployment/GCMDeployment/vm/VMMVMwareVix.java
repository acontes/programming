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
import org.ow2.proactive.virtualizing.vmwarevix.VMwareVM;
import org.ow2.proactive.virtualizing.vmwarevix.VMwareVMM;
import org.ow2.proactive.virtualizing.vmwarevix.VMwareVM.DataKey;
import org.ow2.proactive.virtualizing.vmwarevix.VMwareVMM.Service;

/**
 * An implementation of {@link AbstractVMM} based on VMware Vix API.
 * This class is able to handle VMware server <= 2.0, Workstations and some ESX.
 * Please, note that this implementation doesn't allow clones.
 * See {@link VMwareVM} for more informations.
 * @author jmguilla
 *
 */
public class VMMVMwareVix extends AbstractVMM {

    private ArrayList<VMwareVMM> vmms = new ArrayList<VMwareVMM>();
    private ArrayList<VMwareVM> vms = new ArrayList<VMwareVM>();
    private int port = -1;
    private Service service = Service.vmwareServerVI;

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
					GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to get " + vm.getId() + " from VMware hypervisor.", e);
					continue;
				}
				if (vm.isClone()) {
					GCMDeploymentLoggers.GCMD_LOGGER.error("Clone feature isn't supported for VMware hypervisor.");
					GCMDeploymentLoggers.GCMD_LOGGER.error("Trying to boot the template...");
				}
				try {
					startVM(temp, gcma, comm, vm.getHostInfo());
				} catch (Exception e) {
					GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to start " + vm.getName() + " from VMware hypervisor.", e);
				}
				this.vms.add(temp);
			}
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
			vm.pushData(DataKey.proacBootstrapURL, bootstrapAddress);
		} catch (VirtualServiceException e) {
			GCMDeploymentLoggers.GCMD_LOGGER.error(
					"Cannot pass the bootstrap URL to the virtual machine.", e);
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
