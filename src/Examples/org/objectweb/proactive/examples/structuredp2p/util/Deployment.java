package org.objectweb.proactive.examples.structuredp2p.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


/**
 * Manages the deployment and how to retrieve virtual nodes from a GCMA
 * deployment file.
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
public class Deployment {

    private static GCMApplication deployer;
    private static boolean deployed = false;

    /*
     * Initialize the deployment.
     * 
     * @param descriptor the path to the GCMApplication deployment file.
     * 
     * @throws NodeException
     * 
     * @throws ProActiveException
     */
    public static void deploy(String descriptor) throws NodeException, ProActiveException {
        // Create object representation of the deployment file
        deployer = PAGCMDeployment.loadApplicationDescriptor(new File(descriptor));
        // Activate all virtual nodes
        deployer.startDeployment();
        // Wait for all the virtual nodes to become ready
        deployer.waitReady();
        deployed = true;
    }

    /*
     * Return a virtual node from his name.
     * 
     * @param vnName name of the virtual node in the {@link GCMApplication} file.
     * 
     * @return the specified virtual node.
     */
    public static GCMVirtualNode getVirtualNode(String vnName) {
        if (!deployed) {
            throw new IllegalStateException("You must deploy before retrieve nodes.");
        }

        return deployer.getVirtualNode(vnName);
    }

    /*
     * Gets all Virtual Nodes specified in the descriptor file.
     * 
     * @param descriptor the path to the GCMApplication deployment file.
     * 
     * @return the virtual nodes specified in the descriptor file.
     */
    public static ArrayList<GCMVirtualNode> getAllVirtualNodes() {
        if (!deployed) {
            throw new IllegalStateException("You must deploy before retrieve nodes.");
        }

        ArrayList<GCMVirtualNode> listVn = new ArrayList<GCMVirtualNode>();
        Iterator<GCMVirtualNode> it = deployer.getVirtualNodes().values().iterator();

        while (it.hasNext()) {
            listVn.add(it.next());
        }
        return listVn;
    }

    /*
     * Call the kill method on the deployed {@link GCMApplication}.
     */
    public static void kill() {
        try {
            deployer.kill();
        } catch (Exception e) {
            // do nothing
        }

        PALifeCycle.exitSuccess();
    }
}