package org.objectweb.proactive.extra.infrastructuremanager.dataresource;

import java.util.ArrayList;
import java.util.HashMap;

import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMEventListener;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.NodeSet;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;


public interface IMDataResource {

    //----------------------------------------------------------------------//
    // INIT
    public void init();

    //----------------------------------------------------------------------//
    // ADD NODE and PUT PAD 
    public void addNewDeployedNode(Node node, String vnName, String padName);

    public void putPAD(String padName, ProActiveDescriptor pad);

    //----------------------------------------------------------------------//
    // MONITORING
    // Nb All Node : free + down + busy
    public IntWrapper getSizeListFreeIMNode();

    public IntWrapper getSizeListBusyIMNode();

    public IntWrapper getSizeListDownIMNode();

    /**
     * Nb All IMNode = free + down + busy
     * @return number of all resources (IMNode)
     */
    public IntWrapper getNbAllIMNode();

    public IntWrapper getSizeListPad();

    public HashMap<String, ProActiveDescriptor> getListPad();

    public HashMap<String, ArrayList<VirtualNode>> getDeployedVirtualNodeByPad();

    public ArrayList<IMNode> getListFreeIMNode();

    public ArrayList<IMNode> getListBusyIMNode();

    public ArrayList<IMNode> getListAllIMNode();

    public ProActiveDescriptor getDeployedPad(String padName);

	public IMState addIMEventListener(IMEventListener listener);

    //----------------------------------------------------------------------//
    // IS

    /**
     * @return true if the name of proactive descriptor <I>padName</I> exist
     * in the dataresource, else false.
     */
    public BooleanWrapper isDeployedPad(String padName);

    //----------------------------------------------------------------------//
    // REMOVE
    public void removePad(String padName);

    public void removeNode(String padName);

    public void removeNode(String padName, String vnName);

    public void removeNode(String padName, String[] vnNames);

    //----------------------------------------------------------------------//
    // FREE
    public void freeNode(Node node);

    public void freeNodes(NodeSet nodes);

    public void freeNodes(VirtualNode vnode);

    //----------------------------------------------------------------------//
    // GET NODE 

    public NodeSet getAtMostNodes(IntWrapper nb, VerifyingScript verifyingScript);

    public NodeSet getExactlyNodes(IntWrapper nb, VerifyingScript verifyingScript);

    public void nodeIsDown(IMNode imNode);
}
