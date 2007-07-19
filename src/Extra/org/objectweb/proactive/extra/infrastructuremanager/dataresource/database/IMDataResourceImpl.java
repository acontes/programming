package org.objectweb.proactive.extra.infrastructuremanager.dataresource.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.dataresource.IMDataResource;
import org.objectweb.proactive.extra.infrastructuremanager.dataresource.IMNode;
import org.objectweb.proactive.extra.infrastructuremanager.dataresource.IMState;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMEventListener;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.NodeSet;
import org.objectweb.proactive.extra.scheduler.scripting.ScriptResult;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;


public class IMDataResourceImpl implements IMDataResource, Serializable {

    /**  */
    private static final long serialVersionUID = -3170872605593251201L;
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.IM_DATARESOURCE);

    // Attributes
    private ArrayList<IMNode> listFreeIMNode = new IMArrayListFree();
    private ArrayList<IMNode> listBusyIMNode = new IMArrayListBusy();
    private ArrayList<IMNode> listDownIMNode = new ArrayList<IMNode>();
    private HashMap<String, ProActiveDescriptor> listPad = new HashMap<String, ProActiveDescriptor>();

    // test EventListener
    private ArrayList<IMEventListener> listeners;
    
    //----------------------------------------------------------------------//
    // CONSTRUCTORS
    public IMDataResourceImpl() {
    }

    public void init() {
    	listeners = new ArrayList<IMEventListener>();
        listFreeIMNode = new IMArrayListFree();
        listBusyIMNode = new IMArrayListBusy();
        listDownIMNode = new ArrayList<IMNode>();
        listPad = new HashMap<String, ProActiveDescriptor>();
    }

    //----------------------------------------------------------------------//
    // ACCESSORS
    public IntWrapper getSizeListFreeIMNode() {
        return new IntWrapper(listFreeIMNode.size());
    }

    public IntWrapper getSizeListBusyIMNode() {
 		return new IntWrapper(listBusyIMNode.size());
    }

    public IntWrapper getSizeListDownIMNode() {
        return new IntWrapper(listDownIMNode.size());
    }

    public IntWrapper getSizeListPad() {
        return new IntWrapper(listPad.size());
    }

    public IntWrapper getNbAllIMNode() {
        return new IntWrapper(getSizeListFreeIMNode().intValue() +
            getSizeListBusyIMNode().intValue() +
            getSizeListDownIMNode().intValue());
    }

    public ArrayList<IMNode> getListFreeIMNode() {
        return listFreeIMNode;
    }

    public ArrayList<IMNode> getListBusyIMNode() {
        return listBusyIMNode;
    }

    public ArrayList<IMNode> getListDownIMNode() {
        return listDownIMNode;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IMNode> getListAllIMNode() {
        ArrayList<IMNode> listAllIMNode = (ArrayList<IMNode>) listFreeIMNode.clone();
        listAllIMNode.addAll(listBusyIMNode);
        return listAllIMNode;
    }

    public HashMap<String, ProActiveDescriptor> getListPad() {
        return listPad;
    }

    public HashMap<String, ArrayList<VirtualNode>> getDeployedVirtualNodeByPad() {
        if (logger.isInfoEnabled()) {
            logger.info("getDeployedVirtualNodeByPad");
        }
        HashMap<String, ArrayList<VirtualNode>> deployedVNodesByPadName = new HashMap<String, ArrayList<VirtualNode>>();

        for (String padName : listPad.keySet()) {
            if (logger.isInfoEnabled()) {
                logger.info("pad name : " + padName);
            }
            ProActiveDescriptor pad = listPad.get(padName);

            ArrayList<VirtualNode> deployedVNodes = new ArrayList<VirtualNode>();
            VirtualNode[] vns = pad.getVirtualNodes();
            if (logger.isInfoEnabled()) {
                logger.info("nb vnodes of this pad : " + vns.length);
            }
            for (VirtualNode vn : vns) {
                if (logger.isInfoEnabled()) {
                    logger.info("virtualnode " + vn.getName() + " is actif ? " +
                        vn.isActivated());
                }
                if (vn.isActivated()) {
                    deployedVNodes.add(vn);
                }
            }
            deployedVNodesByPadName.put(padName, deployedVNodes);
        }
        return deployedVNodesByPadName;
    }

    public ProActiveDescriptor getDeployedPad(String padName) {
        return listPad.get(padName);
    }

    //----------------------------------------------------------------------//
    // IS
    public BooleanWrapper isDeployedPad(String padName) {
        return new BooleanWrapper(listPad.containsKey(padName));
    }

    //----------------------------------------------------------------------//
    // ADD
    public void addNewDeployedNode(Node node, String vnName, String padName) {
        this.listFreeIMNode.add(new IMNodeImpl(node, vnName, padName));
        // TODO Event Handling
    }

    public void putPAD(String padName, ProActiveDescriptor pad) {
        listPad.put(padName, pad);
    }

    //----------------------------------------------------------------------//
    // REMOVE NODE BUSY AND FREE
    public void removePad(String padName) {
        listPad.remove(padName);
    }

    public void removeNode(String padName) {
        ListIterator<IMNode> iterator = listBusyIMNode.listIterator();
        while (iterator.hasNext()) {
            IMNode imnode = iterator.next();
            if (imnode.getPADName().equals(padName)) {
                if (logger.isInfoEnabled()) {
                    logger.info("remove node : " + imnode.getNodeName());
                }
                iterator.remove();
                // TODO Event Handling
            }
        }
        iterator = listFreeIMNode.listIterator();
        while (iterator.hasNext()) {
            IMNode imnode = iterator.next();
            if (imnode.getPADName().equals(padName)) {
                if (logger.isInfoEnabled()) {
                    logger.info("remove node : " + imnode.getNodeName());
                }
                iterator.remove();
                // TODO Event Handling
            }
        }
    }

    public void removeNode(String padName, String vnName) {
        ListIterator<IMNode> iterator = listBusyIMNode.listIterator();
        while (iterator.hasNext()) {
            IMNode imnode = iterator.next();
            if (imnode.getPADName().equals(padName) &&
                    imnode.getVNodeName().equals(vnName)) {
                if (logger.isInfoEnabled()) {
                    logger.info("remove node : " + imnode.getNodeName());
                }
                iterator.remove();
                // TODO Event Handling
            }
        }
        iterator = listFreeIMNode.listIterator();
        while (iterator.hasNext()) {
            IMNode imnode = iterator.next();
            if (imnode.getPADName().equals(padName) &&
                    imnode.getVNodeName().equals(vnName)) {
                if (logger.isInfoEnabled()) {
                    logger.info("remove node : " + imnode.getNodeName());
                }
                iterator.remove();
                // TODO Event Handling
            }
        }
    }

    public void removeNode(String padName, String[] vnNames) {
        for (String vnName : vnNames) {
            removeNode(padName, vnName);
            // TODO Event Handling
        }
    }

    //----------------------------------------------------------------------//
    // FREE NODE(S) - NODE : BUSY -> FREE
    public void freeNode(Node node) {
        ListIterator<IMNode> iterator = listBusyIMNode.listIterator();
        while (iterator.hasNext()) {
            IMNode imnode = iterator.next();
            if (imnode.getNodeName().equals(node.getNodeInformation().getName())) {
                try {
                    imnode.setFree();
                    imnode.clean();
                    listFreeIMNode.add(imnode);
                } catch (NodeException e) {
                    imnode.setDown(true);
                    listDownIMNode.add(imnode);
                }
                iterator.remove();
                break;
            }
        }
        // TODO Event Handling
    }

    public void freeNodes(NodeSet nodes) {
        for (Node node : nodes) {
            freeNode(node);
        }
    }

    public void freeNodes(VirtualNode vnode) {
        ListIterator<IMNode> iterator = listBusyIMNode.listIterator();
        while (iterator.hasNext()) {
            IMNode imnode = iterator.next();
            if (imnode.getVNodeName().equals(vnode.getName())) {
                try {
                    imnode.setFree();
                    imnode.clean();
                    listFreeIMNode.add(imnode);
                    // TODO Event Handling
                } catch (NodeException e) {
                    imnode.setDown(true);
                    listDownIMNode.add(imnode);
                    // TODO Event Handling
                }
                iterator.remove();
            }
        }
    }

    //----------------------------------------------------------------------//
    // GET NODE(S)
    public NodeSet getAtMostNodes(IntWrapper nb, VerifyingScript verifyingScript) {
        ArrayList<IMNode> nodesToSend = new ArrayList<IMNode>();
        ArrayList<IMNode> invalidNodes = new ArrayList<IMNode>();

        while (!listFreeIMNode.isEmpty() &&
                (nodesToSend.size() < nb.intValue())) {
            ScriptResult<Boolean> result = (verifyingScript == null) ? null
                                                                     : listFreeIMNode.get(0)
                                                                                     .executeScript(verifyingScript);
            if ((result != null) && result.errorOccured()) {
                // TODO Kill node
                logger.error("Error during script execution ",
                    result.getException());
            }
            if ((result == null) ||
                    (!result.errorOccured() && result.getResult())) {
                IMNode node = listFreeIMNode.remove(0);
                if (node.isDown()) {
                    listDownIMNode.add(node);
                } else {
                    nodesToSend.add(node);
                }
            } else {
                invalidNodes.add(listFreeIMNode.remove(0));
            }
        }

        if (logger.isDebugEnabled() && (nodesToSend.size() > 0)) {
            logger.debug("getAtMostNodes : " + nodesToSend.size() +
                " Nodes Reserved (" + nb.intValue() + " asked)");
        }

        listFreeIMNode.addAll(invalidNodes);

        ListIterator<IMNode> iterator = nodesToSend.listIterator();
        NodeSet ns = new NodeSet();
        while (iterator.hasNext()) {
            IMNode node = iterator.next();
            try {
                node.setBusy();
                ns.add(node.getNode());
            } catch (NodeException e) {
                node.setDown(true);
                listDownIMNode.add(node);
                iterator.remove();
            }
        }

        listBusyIMNode.addAll(nodesToSend);

        // TODO Event Handling
        return ns;
    }

    public NodeSet getExactlyNodes(IntWrapper nb,
        VerifyingScript verifyingScript) {
        ArrayList<IMNode> nodesToSend = new ArrayList<IMNode>();
        ArrayList<IMNode> invalidNodes = new ArrayList<IMNode>();

        while (!listFreeIMNode.isEmpty() &&
                (nodesToSend.size() < nb.intValue())) {
            ScriptResult<Boolean> result = (verifyingScript == null) ? null
                                                                     : listFreeIMNode.get(0)
                                                                                     .executeScript(verifyingScript);
            if ((result != null) && result.errorOccured()) {
                // TODO Kill node
                logger.error("Script invalid --> Error on script",
                    result.getException());
            }
            if ((result == null) ||
                    (!result.errorOccured() && result.getResult())) {
                IMNode node = listFreeIMNode.remove(0);
                if (node.isDown()) {
                    listDownIMNode.add(node);
                } else {
                    nodesToSend.add(node);
                }
            } else {
                invalidNodes.add(listFreeIMNode.remove(0));
            }
        }

        if (logger.isDebugEnabled() && (nodesToSend.size() > 0)) {
            logger.debug("getAtMostNodes : " + nodesToSend.size() +
                " Nodes Reserved (" + nb.intValue() + " asked)");
        }

        listFreeIMNode.addAll(invalidNodes);

        ListIterator<IMNode> iterator = nodesToSend.listIterator();
        NodeSet ns = new NodeSet();
        while (iterator.hasNext()) {
            IMNode node = iterator.next();
            try {
                node.setBusy();
                ns.add(node.getNode());
                node.clean();
            } catch (NodeException e) {
                node.setDown(true);
                listDownIMNode.add(node);
                iterator.remove();
            }
        }

        if (nodesToSend.size() != nb.intValue()) {
            listFreeIMNode.addAll(nodesToSend);
            return new NodeSet();
        } else {
            listBusyIMNode.addAll(nodesToSend);
            return ns;
        }
        // TODO Event Handling
    }

    public void nodeIsDown(IMNode imNode) {
        imNode.setDown(true);
        listDownIMNode.add(imNode);
        if (listBusyIMNode.contains(imNode)){
            listBusyIMNode.remove(imNode);
        } else {
            listFreeIMNode.remove(imNode);
        }
        // TODO Event Handling
        
    }

	public IMState addIMEventListener(IMEventListener listener) {
		listeners.add(listener);
		return new IMStateImpl(getListFreeIMNode(), getListBusyIMNode(), getListDownIMNode());
	}

    //----------------------------------------------------------------------//
    // PAD
}
