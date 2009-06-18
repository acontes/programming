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
import org.ow2.proactive.virtualizing.xenserver.XenServerVM;
import org.ow2.proactive.virtualizing.xenserver.XenServerVMM;
import org.ow2.proactive.virtualizing.xenserver.XenServerVM.DataKey;


/**
 * This is an implementation of the {@link AbstractVMM} class for
 * XenOss & XenServer hypervisors. Note that the
 * underlying API isn't fully implemented for XenOss, please see
 * the state of the libxen & xenapi implementation directly
 * from the website of your hypervisor (XenServer handles it at least
 * since the 4.0 release).
 * This class provides an implementation to clone virtual machines.
 * To enable an efficient cloning feature from your
 * XenServer environment, be sure that the underlying Datastore is built on
 * ext3 filesystem. XenOss doesn't currently handles clones.
 * @author jmguilla
 *
 */
public class VMMLibXen extends AbstractVMM {

    /** All registered hypervisors */
    private ArrayList<XenServerVMM> vmms = new ArrayList<XenServerVMM>();
    //the associated boolean specify if the virtual is a clone.
    /** all registered virtual machines */
    private Map<XenServerVM, Boolean> vms = new HashMap<XenServerVM, Boolean>();

    @Override
    /**
     * This method handles clones. To enable an efficient cloning feature from your
     * XenServer environment, be sure that the underlying Datastore is built on
     * ext3 filesystem.
     */
    public void start(CommandBuilderProActive comm, GCMApplicationInternal gcma) {
        //contact hypervisors & setup environment
        ArrayList<String> uris = super.getUris();
        for (String uri : uris) {
            XenServerVMM vmm;
            try {
                vmm = new XenServerVMM(uri, getUser(), getPwd());
            } catch (VirtualServiceException e) {
                GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to connect to " + uri + "'s hypervisor.", e);
                return;
            }
            ArrayList<VMBean> vmsMap = super.getVms();
            for (VMBean vm : vmsMap) {
                XenServerVM temp = null;
                try {
                    temp = vmm.getNewVM(vm.getId());
                } catch (VirtualServiceException e) {
                    GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to get " + vm.getId() +
                        " from Xen hypervisor.", e);
                    continue;
                }
                if (vm.isClone()) {
                    try {
                        temp = temp.clone(vm.getName());
                    } catch (VirtualServiceException e) {
                        GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to clone " + vm.getId() +
                            " from Xen hypervisor.", e);
                        continue;
                    }
                }
                try {
                    startVM(temp, gcma, comm, vm.getHostInfo());
                } catch (Exception e) {
                    GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to start " + vm.getName() +
                        " from Xen hypervisor.", e);
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
        Set<XenServerVM> vmSet = vms.keySet();
        for (XenServerVM vm : vmSet) {
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
     * Starts the given virtual machine.
     * This method is also in charge of registering a web page to make the remote
     * PART able to bootstrap ( see {@link BootstrapServlet} ).
     * @param vm the virtual machine to boot
     * @param gcma the GCMApplication object associated to this deployment
     * @param comm the CommandBuildProActive used to build the remote PART command
     * @param hostInfo the hostinfo needed to build the command
     * @param topologyImpl 
     * @throws VirtualServiceException
     */
    private void startVM(XenServerVM vm, GCMApplicationInternal gcma, CommandBuilderProActive comm,
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
            vm.pushDataHVM(DataKey.proacBootstrapURL, bootstrapAddress);
            GCMDeploymentLoggers.GCMD_LOGGER.info("Updated vm environment through HVM.");
        } catch (VirtualServiceException e) {
            GCMDeploymentLoggers.GCMD_LOGGER
                    .error("Cannot pass the bootstrap URL to the virtual machine.", e);
        }
    }
}
