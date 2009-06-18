package org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.vm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.objectweb.proactive.core.util.OperatingSystem;
import org.objectweb.proactive.core.util.ProActiveCounter;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.CommandBuilderProActive;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.HostInfoImpl;
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
     * Method called when parsing a new hypervisor from a GCMD file
     * @param uri the uri of the hypervisor (if null, associating "localhost").
     * Note that any URI is hypervisor dependant.
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
     * @param key the name associated to your virtual machine in your virtual environment.
     * (The name you gave it from the GUI management tool)
     * @param count the number of time you want to boot the given virtual machine. Be sure that
     * the virtualization layer handles clones...
     */
    public void addVMBean(String key, int count) {
        addVMBean(key, count, null);
    }

    /**
     * see {@link #addVMBean(String, int, String)}
     * @param key
     * @param count
     * @param osType the operating system of your virtual machine (unix || windows).
     */
    public void addVMBean(String key, int count, String osType) {
        HostInfoImpl hostInfo = new HostInfoImpl();
        if (osType != null) {
            hostInfo.setOs(osType.equals(OperatingSystem.unix.name()) ? OperatingSystem.unix
                    : OperatingSystem.windows);
        }
        if (count == 1) {
            vms.add(new VMBean(key, false, key, hostInfo));
        } else
            for (int i = 0; i < count; i++) {
                vms.add(new VMBean(key, true,
                    key + "_PAClone" + (i + 1) + "_" + ProActiveCounter.getUniqID(), hostInfo));
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

    /**
     * This method must starts every registered virtual machines, cloning them if necessary.
     * @param comm the CommandBuilderProActive associated to this application
     * @param gcma the associated GCM application descriptor object.
     */
    public abstract void start(CommandBuilderProActive comm, GCMApplicationInternal gcma);

    /**
     * Used to stop every launched virtual machines and to destroy cloned ones.
     */
    public abstract void stop();
}
