package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.vm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.runtime.StartPARuntime;
import org.objectweb.proactive.core.util.ProActiveCounter;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;
import org.objectweb.proactive.extensions.gcmdeployment.core.TopologyImpl;
import org.ow2.proactive.virtualizing.core.VirtualMachine;


/**
 * This class can be seen as a JavaBean for the first part of the GCMD parsing.
 * It just handles "set" operations during that step to keep being
 * "Serializable". It's only during the "start" method call that
 * everything (VMM & VM) will be initialized.
 * @author jmguilla
 *
 */
public abstract class AbstractVMM implements Serializable {

    /** user's info & id (infrastructure & resources identification) */
    private String pwd, user, id;
    /** to keep all hypervisor's uri */
    private ArrayList<String> uris = new ArrayList<String>();
    /** and every virtual machines */
    private ArrayList<VMBean> vms = new ArrayList<VMBean>();
    /** to easily get a ref to topologyId */
    protected HashMap<VirtualMachine, TopologyImpl> virtualMachineToTopologyImplMapper = new HashMap<VirtualMachine, TopologyImpl>();

    /**
     * 
     */
    public void addHypervisorBean(String uri) {
        if (uri != null)
            this.uris.add(uri);
        else
            this.uris.add("localhost");
    }

    /**
     * Builds a new {@link VMBean} and adds it to the known list of {@link VMBean}.
     * the key parameter represents the virtual machine's name within your virtualization software
     * and the count parameter, the number of times you want to boot different virtual machines.
     * If count == 1, {@link #getId()#equals(#getName())} returns true, otherwise, a new "ProActive Unique name"
     * is given to the VMBean, and thus, the future cloned virtual machine. 
     * @param key
     * @param count
     */
    public void addVMBean(String key, int count) {
        if (count == 1) {
            vms.add(new VMBean(key, false, key));
        } else
            for (int i = 0; i < count; i++) {
                vms
                        .add(new VMBean(key, true, key + "_PAClone" + (i + 1) + "_" +
                            ProActiveCounter.getUniqID()));
            }
    }

    /*------------------
     * Getters & Setters
     -------------------*/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<VMBean> getVms() {
        return vms;
    }

    public void setVms(ArrayList<VMBean> vms) {
        this.vms = vms;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public ArrayList<String> getUris() {
        return uris;
    }

    public void setUris(ArrayList<String> uris) {
        this.uris = uris;
    }

    class HypervisorBean {
        String url = null;
    }

    public abstract void start(GCMApplicationInternal gcma);

    public abstract void stop();
}
