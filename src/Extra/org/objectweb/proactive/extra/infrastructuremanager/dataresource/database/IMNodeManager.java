package org.objectweb.proactive.extra.infrastructuremanager.dataresource.database;

import java.util.ArrayList;
import java.util.Collection;

import org.objectweb.proactive.extra.infrastructuremanager.dataresource.IMNode;
import org.objectweb.proactive.extra.scheduler.scripting.ScriptResult;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;


/**
 * This Class is specified to handle all IMNodes in the Infrastructure Manager.
 * IMNodeManager isn't thread-safe, so be carefull not to access more that one at a time.
 *
 * @author ProActive Team
 * @version 1.0, Jul 11, 2007
 * @since ProActive 3.2
 */
public interface IMNodeManager {
    // ADDING OR REMOVING NODES
    /**
     * Adding an {@link IMNode} to the structure.
     * @param imnode the {@link IMNode} to add.
     */
    public void addIMNode(IMNode imnode);

    /**
     * Adding a set of IMNodes to the structure.
     * @param imnodes
     */
    public void addIMNodes(Collection<IMNode> imnodes);

    /**
     * Remove an {@link IMNode} from the structure.
     * @param imnode
     */
    public void removeIMNode(IMNode imnode);

    /**
     * Remove a set of {@link IMNode} from the structure.
     * @param imnodes
     */
    public void removeIMNodes(Collection<IMNode> imnodes);

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

    /**
     * Set the {@link IMNode} in a waiting state.
     * A Node is in a waiting state when a script executed on it isn't terminated.
     * The waiting nodes are handled in parallel, and will go in a free or down state.
     * @param imnode
     * @param script
     * @param futureResult
     */
    public void setWaitingForScriptResult(IMNode imnode,
        VerifyingScript script, ScriptResult<Boolean> futureResult);

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

    // ACCESSORS
    /**
     * The way to know the number of free nodes in the structure.
     */
    public int getNbFreeIMNode();

    /**
     * The way to know the number of busy nodes in the structure.
     */
    public int getNbBusyIMNode();

    /**
     * The way to know the number of down nodes in the structure.
     */
    public int getNbDownIMNode();

    /**
     * The way to know the number of waiting nodes in the structure.
     */
    public int getNbWaitingIMNode();

    /**
     * The way to know the number of nodes in the structure.
     */
    public int getNbAllIMNode();

    /**
     * The way to to get all free nodes in the structure.
     */
    public ArrayList<IMNode> getListFreeIMNode();

    /**
     * The way to to get all busy nodes in the structure.
     */
    public ArrayList<IMNode> getListBusyIMNode();

    /**
     * The way to to get all down nodes in the structure.
     */
    public ArrayList<IMNode> getListDownIMNode();

    /**
     * The way to to get all waiting nodes in the structure.
     */
    public ArrayList<IMNode> getListWaitingIMNode();

    /**
     * The way to to get all nodes in the structure.
     */
    public ArrayList<IMNode> getListAllIMNode();

    /**
     * The way to to get free nodes in the structure, ordered with the script.
     * The more a Node has chances to verify the script, the less it's far in the list.
     */
    public ArrayList<IMNode> getNodesByScript(VerifyingScript script);
}
