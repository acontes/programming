package org.objectweb.proactive.extra.gcmdeployment.GCMApplication;

import java.util.Map;
import java.util.NoSuchElementException;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.extra.gcmdeployment.VirtualNode;


/**
 * GCM Application Descriptor public interface
 *
 * This interface is exported to ProActive user and allow them to
 * control and manage a GCM Application Descriptor. For example, this
 * interface must be used to retrieve the Virtual Nodes.
 *
 * @author cmathieu
 *
 */
@PublicAPI
public interface GCMApplicationDescriptor {

    /**
     * Returns the Virtual Node associated to this name
     *
     * @param vnName a Virtual Node name declared inside the GCM Application Descriptor
     * @return the VirtualNode associated to vnName
     * @throws NoSuchElementException if vnName is not declared inside the GCM Application Descriptor
     */
    public VirtualNode getVirtualNode(String vnName)
        throws IllegalArgumentException;

    /**
     * Returns all the Virtual Nodes declared inside this GCM Application Descriptor
     *
     * Keys are the Virtual Node names. Values are the Virtual Nodes.
     *
     * @return All the Virtual Nodes declared inside the GCM Application Descriptor.
     */
    public Map<String, VirtualNode> getVirtualNodes();
}
