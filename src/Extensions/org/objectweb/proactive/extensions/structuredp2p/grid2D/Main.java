package org.objectweb.proactive.extensions.structuredp2p.grid2D;

import java.io.File;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


public class Main {
    private static GCMApplication pad;

    // deployment method
    private static GCMVirtualNode deploy(String descriptor) throws NodeException, ProActiveException {
        // TODO 1. Create object representation of the deployment file
        pad = PAGCMDeployment.loadApplicationDescriptor(new File(descriptor));
        // TODO 2. Activate all Virtual Nodes
        pad.startDeployment();
        // TODO 3. Wait for all the virtual nodes to become ready
        pad.waitReady();
        // TODO 4. Get the first Virtual Node specified in the descriptor file
        GCMVirtualNode vn = pad.getVirtualNodes().values().iterator().next();
        // TODO 5. Return the virtual node
        return vn;
    }

    public static void main(String args[]) {
        try {
            // TODO 6. Get the virtual node through the deploy method
            GCMVirtualNode vn = deploy(args[1]);
            // TODO 7. Create the active object using a node on the virtual node
            CMAgentInitialized ao = (CMAgentInitialized) PAActiveObject.newActive(CMAgentInitialized.class
                    .getName(), new Object[] {}, vn.getANode());
            // TODO 8. Get the current state from the active object
            String currentState = ao.getCurrentState().toString();
            // TODO 9. Print the state
            System.out.println(currentState);
            // TODO 10. Stop the active object
            PAActiveObject.terminateActiveObject(ao, false);
        } catch (NodeException nodeExcep) {
            System.err.println(nodeExcep.getMessage());
        } catch (ActiveObjectCreationException aoExcep) {
            System.err.println(aoExcep.getMessage());
        } catch (ProActiveException poExcep) {
            System.err.println(poExcep.getMessage());
        } finally {
            // TODO 11. Stop the virtual node
            if (pad != null)
                pad.kill();
            PALifeCycle.exitSuccess();
        }
    }
}
