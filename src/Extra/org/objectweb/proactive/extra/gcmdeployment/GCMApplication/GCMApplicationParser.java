package org.objectweb.proactive.extra.gcmdeployment.GCMApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.CommandBuilder;
import org.objectweb.proactive.extra.gcmdeployment.VirtualNode;


/**
 * A parser for the GCM Application descriptor schema.
 *
 * @author cmathieu
 *
 */
public interface GCMApplicationParser {

    /**
     * Returns all the Resources Providers
     * Descriptor
     *
     * @return all the declared Resources Providers as ResourceProviderParams
     */
    public Set<GCMDeploymentDescriptorParams> getResourceProviders();

    /**
     * Returns all the Virtual Node
     *
     * @return all the declared Virtual Nodes
     */
    public Map<String, VirtualNode> getVirtualNodes();

    /**
     * Returns the Command Builder
     *
     * @return the Command Builder associated to this application
     */
    public CommandBuilder getCommandBuilder();
}
