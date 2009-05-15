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
import org.ow2.proactive.virtualizing.virtualbox.VirtualboxVM;
import org.ow2.proactive.virtualizing.xenserver.XenServerVM;
import org.ow2.proactive.virtualizing.xenserver.XenServerVMM;


public class VMMLibXen extends AbstractVMM {

    private ArrayList<XenServerVMM> vmms = new ArrayList<XenServerVMM>();
    //the associated boolean specify if the virtual is a clone.
    private Map<XenServerVM, Boolean> vms = new HashMap<XenServerVM, Boolean>();

    @Override
    public void start(GCMApplicationInternal gcma) {
        //contact hypervisors & setup environment
        ArrayList<String> uris = super.getUris();
        for (String uri : uris) {
            try {
                XenServerVMM vmm = new XenServerVMM(uri, getUser(), getPwd());
                ArrayList<VMBean> vmsMap = super.getVms();
                for (VMBean vm : vmsMap) {
                    XenServerVM temp = vmm.getNewVM(vm.getId());
                    if (!vm.isClone()) {
                        this.vms.put(temp, vm.isClone());
                        virtualMachineToTopologyImplMapper.put(temp, vm.getNode());
                    } else {
                        XenServerVM clone = temp.clone(vm.getName());
                        this.vms.put(clone, vm.isClone());
                        virtualMachineToTopologyImplMapper.put(temp, vm.getNode());
                    }
                }
            } catch (VirtualServiceException e) {
                GCMDeploymentLoggers.GCMD_LOGGER.error("Unable to start virtual machine.", e);
            }
        }
        Set<XenServerVM> vmSet = vms.keySet();
        for (XenServerVM vm : vmSet) {
            startVM(vm, gcma);
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

    private void startVM(XenServerVM vm, GCMApplicationInternal gcma) {
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
                //TODO hardcoded HVM, this implies to have the guest tools installed.
                //just find a way to precise, (through GCMD) if pvm or hvm?
                String bootstrapAddress = bootstrapServlet.getBaseURI() + "?" + BootstrapServlet.VM_ID + "=" +
                    vmKey;
                GCMDeploymentLoggers.GCMD_LOGGER.info("Bootstrap servlet registered an app on:  " +
                    bootstrapAddress);
                vm.pushDataHVM(XenServerVM.DataKey.proacBootstrapURL, bootstrapAddress);
            } catch (VirtualServiceException e) {
                GCMDeploymentLoggers.GCMD_LOGGER.error(
                        "Cannot pass the bootstrap URL to the virtual machine.", e);
            }
        } catch (VirtualServiceException e1) {
            GCMDeploymentLoggers.GCMD_LOGGER.error("Cannot power on the virtual machine.", e1);
        }
    }
}
