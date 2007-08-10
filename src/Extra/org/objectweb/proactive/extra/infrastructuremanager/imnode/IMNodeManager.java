package org.objectweb.proactive.extra.infrastructuremanager.imnode;

import java.util.ArrayList;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.frontend.NodeSourceInterface;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;


/**
 * This Interface is specified to handle IMNodes in the Infrastructure Manager.
 * IMNodeManager isn't thread-safe, so be carefull not to access more that one at a time.
 *
 * @author ProActive Team
 * @version 1.0, Jul 11, 2007
 * @since ProActive 3.2
 */
public interface IMNodeManager extends NodeSourceInterface{

    // SETTING NODE STATE
    /**
     * Set the {@link IMNode} in a busy state.
     * There is nothing to do more than that, like expressly setting
     * busy state <i>a mano</i>.
     * @param imnode
     */
    public void setBusy(IMNode imnode);

    /**
     * Set the {@link IMNode} in a free state.
     * A free node can be used by IM.
     * @param imnode
     */
    public void setFree(IMNode imnode);

    /**
     * Set the {@link IMNode} in a down state.
     * A Node is down when it's no longer responding.
     * @param imnode
     */
    public void setDown(IMNode imnode);

    // SETTING SCRIPT VERIFICATION
    /**
     * That's the way to say to the NodeManager that a Node verifies a script.
     * This will help ordering nodes for future calls to {@link #getNodesByScript(VerifyingScript)}.
     * @param imnode
     * @param script
     */
    public void setVerifyingScript(IMNode imnode, VerifyingScript script);

    /**
     * That's the way to say to the NodeManager that a Node doesn't (or no longer) verifie a script.
     * This will help ordering nodes for future calls to {@link #getNodesByScript(VerifyingScript)}.
     * @param imnode
     * @param script
     */
    public void setNotVerifyingScript(IMNode imnode, VerifyingScript script);

    /**
     * The way to to get free nodes in the structure, ordered (or not) with the script.
     * The more a Node has chances to verify the script, the less it's far in the list.
     */
    public ArrayList<IMNode> getNodesByScript(VerifyingScript script, boolean ordered);
    
    /**
     * Shutting down Node Manager, and everything depending on it.
     */
    public BooleanWrapper shutdown();
}
