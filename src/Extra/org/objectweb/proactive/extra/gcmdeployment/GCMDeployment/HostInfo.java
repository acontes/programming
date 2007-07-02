package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment;

import java.util.Set;

import org.objectweb.proactive.core.util.OperatingSystem;


/**
 *
 * @author cmathieu
 *
 */
public interface HostInfo {

    /**
     * Returns the Id of this of set
     * @return the Id as declared by the host's id attribute
     */
    public String getId();

    /**
     * Returns the username associated to this set of hosts
     * @return the username it is present inside the GCM Deployment Descriptor. If
     * not null is returned
     */
    public String getUsername();

    /**
     * Returns the homeDirectory associated to this set of hosts
     *
     * @return the home directory as an platform dependent absolute path.
     */
    public String getHomeDirectory();

    /**
     * Returns the Operating System associated to this set of hosts
     *
     * @return the Operating System
     */
    public OperatingSystem getOS();

    /**
     * Returns the set of available tools on this set of hosts
     *
     * @return A set of available tools as declared inside the GCM Deployment Descriptor
     */
    public Set<Tool> getTools();

    /**
     * Returns the Tool identified by this id
     *
     * @param id the identifier of this tool
     * @return If declared the Tool is returned. Otherwise null is returned
     */
    public Tool getTool(final String id);
}
