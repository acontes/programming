package org.objectweb.proactive.extra.infrastructuremanager.dataresource.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
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


public class IMDataResourceImpl2 implements IMDataResource, Serializable {

	/**  */
	private static final long serialVersionUID = -3170872605593251201L;
	private static final int MAX_VERIF_TIMEOUT = 120000;
	private static final Logger logger = ProActiveLogger.getLogger(Loggers.IM_DATARESOURCE);

	// Attributes
	private HashMap<String, ProActiveDescriptor> listPad;
	private IMNodeManager nodeManager;

	// test EventListener
	private ArrayList<IMEventListener> listeners;

	//----------------------------------------------------------------------//
	// CONSTRUCTORS
	public IMDataResourceImpl2() {
		init();
	}

	public void init() {
		listeners = new ArrayList<IMEventListener>();
		nodeManager = new IMNodeManagerImpl();
		listPad = new HashMap<String, ProActiveDescriptor>();
	}

	//----------------------------------------------------------------------//
	// ACCESSORS
	public IntWrapper getSizeListFreeIMNode() {
		return new IntWrapper(nodeManager.getNbFreeIMNode());
	}

	public IntWrapper getSizeListBusyIMNode() {
		return new IntWrapper(nodeManager.getNbBusyIMNode() + nodeManager.getNbWaitingIMNode());
	}

	public IntWrapper getSizeListDownIMNode() {
		return new IntWrapper(nodeManager.getNbDownIMNode());
	}

	public IntWrapper getSizeListPad() {
		return new IntWrapper(listPad.size());
	}

	public IntWrapper getNbAllIMNode() {
		return new IntWrapper(nodeManager.getNbAllIMNode());
	}

	public ArrayList<IMNode> getListFreeIMNode() {
		return nodeManager.getListFreeIMNode();
	}

	public ArrayList<IMNode> getListBusyIMNode() {
		ArrayList<IMNode> result = nodeManager.getListBusyIMNode();
		result.addAll(nodeManager.getListWaitingIMNode());
		return result;
	}

	public ArrayList<IMNode> getListDownIMNode() {
		return nodeManager.getListDownIMNode();
	}

	public ArrayList<IMNode> getListAllIMNode() {
		return nodeManager.getListAllIMNode();
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
		if(logger.isDebugEnabled()) {
			logger.debug("Adding a node to IMNodeManager");
		}
		nodeManager.addIMNode(new IMNodeImpl(node, vnName, padName));
	}

	public void putPAD(String padName, ProActiveDescriptor pad) {
		listPad.put(padName, pad);
	}

	//----------------------------------------------------------------------//
	// REMOVE NODE BUSY AND FREE
	public void removePad(String padName) {
		// TODO anything else to do ? 
		// what do we do for the IMNodes corresponding ?
		listPad.remove(padName);
	}

	public void removeNode(String padName) {
		ListIterator<IMNode> iterator = nodeManager.getListAllIMNode().listIterator();
		ArrayList<IMNode> toRemove = new ArrayList<IMNode>();
		while (iterator.hasNext()) {
			IMNode imnode = iterator.next();
			if (imnode.getPADName().equals(padName)) {
				if (logger.isInfoEnabled()) {
					logger.info("remove node : " + imnode.getNodeName());
				}
				toRemove.add(imnode);
			}
		}
		nodeManager.removeIMNodes(toRemove);
	}

	public void removeNode(String padName, String vnName) {
		ListIterator<IMNode> iterator = nodeManager.getListAllIMNode().listIterator();
		ArrayList<IMNode> toRemove = new ArrayList<IMNode>();
		while (iterator.hasNext()) {
			IMNode imnode = iterator.next();
			if (imnode.getPADName().equals(padName) &&
					imnode.getVNodeName().equals(vnName)) {
				if (logger.isInfoEnabled()) {
					logger.info("remove node : " + imnode.getNodeName());
				}
				toRemove.add(imnode);
			}
		}
		nodeManager.removeIMNodes(toRemove);
	}

	public void removeNode(String padName, String[] vnNames) {
		for (String vnName : vnNames) {
			removeNode(padName, vnName);
		}
	}

	public IMState addIMEventListener(IMEventListener listener) {
		listeners.add(listener);
		return new IMStateImpl(getListFreeIMNode(), getListBusyIMNode(), getListDownIMNode());
	}

	public void freeNode(Node node) {
		ListIterator<IMNode> iterator = nodeManager.getListBusyIMNode().listIterator();
		String nodeName = null;
		try {
			nodeName = node.getNodeInformation().getName();
		} catch(RuntimeException e) {
			// node is down,
			// will be detected later
			return;
		}
		while (iterator.hasNext()) {
			IMNode imnode = iterator.next();
			// Si le noeud correspond
			if (imnode.getNodeName().equals(nodeName)) {
				imnode.clean(); // Nettoyage du noeud
				nodeManager.setFree(imnode);
				break;
			}
		}
	}

	public void freeNodes(NodeSet nodes) {
		for(Node node : nodes) freeNode(node);
	}

	public void freeNodes(VirtualNode vnode) {
		ListIterator<IMNode> iterator = nodeManager.getListBusyIMNode().listIterator();
		while (iterator.hasNext()) {
			IMNode imnode = iterator.next();
			if (imnode.getVNodeName().equals(vnode.getName())) {
				imnode.clean(); // Cleaning the node
				nodeManager.setFree(imnode);
			}
		}
	}

	public NodeSet getAtMostNodes(IntWrapper nb, VerifyingScript verifyingScript) {
		ArrayList<IMNode> nodes = nodeManager.getNodesByScript(verifyingScript);
		String order = "";
		for( IMNode n : nodes) order += n.getHostName()+ " ";
		logger.info("Nodes = "+order);
		NodeSet result = new NodeSet();
		int found = 0;
		if(verifyingScript == null) {
			logger.info("No verif script");
			while(!nodes.isEmpty() && found < nb.intValue()) {
				IMNode imnode = nodes.remove(0);
				imnode.clean();
				try {
					result.add(imnode.getNode());
					nodeManager.setBusy(imnode);
					found++;
				} catch (NodeException e) {
					nodeManager.setDown(imnode);
				}
			}
		} else if(!verifyingScript.isDynamic()) {
			logger.info("Static verif script");
			while(!nodes.isEmpty() && found < nb.intValue()) {
				IMNode node = nodes.remove(0);
				if(node.getScriptStatus().containsKey(verifyingScript) &&
						node.getScriptStatus().get(verifyingScript).equals(IMNode.VERIFIED_SCRIPT)) {
					node.clean();
					try {
						result.add(node.getNode());
						nodeManager.setBusy(node);
						found++;
					} catch (NodeException e) {
						nodeManager.setDown(node);
					}
				} else break;
			}
			
			Vector<ScriptResult<Boolean>> scriptResults = new Vector<ScriptResult<Boolean>>();
			Vector<IMNode> nodeResults = new Vector<IMNode>();
			int launched = found;
			while(!nodes.isEmpty() && launched++ < nb.intValue()) {
				nodeResults.add(nodes.get(0));
				scriptResults.add(nodes.get(0).executeScript(verifyingScript));
				nodes.remove(0);
			}
			// Recupere les resultats
			while(!scriptResults.isEmpty() && !nodes.isEmpty() && found < nb.intValue()) {
				try {
					int idx = ProActive.waitForAny(scriptResults, MAX_VERIF_TIMEOUT);
					IMNode imnode = nodeResults.remove(idx);
					ScriptResult<Boolean> res = scriptResults.remove(idx);
					if(res.errorOccured()) {
						// nothing to do, just let the node in the free list
					} else if(res.getResult()){
						// Result OK
						nodeManager.setVerifyingScript(imnode, verifyingScript);
						imnode.clean();
						try {
							result.add(imnode.getNode());
							nodeManager.setBusy(imnode);
							found++;
						} catch (NodeException e) {
							nodeManager.setDown(imnode);
							// try on a new node
							nodeResults.add(nodes.get(0));
							scriptResults.add(nodes.remove(0).executeScript(verifyingScript));
						}
					} else {
						// result is false
						nodeManager.setNotVerifyingScript(imnode, verifyingScript);
						// try on a new node
						nodeResults.add(nodes.get(0));
						scriptResults.add(nodes.remove(0).executeScript(verifyingScript));
					}
				} catch (ProActiveException e) {
					// TODO Auto-generated catch block
					// Wait For Any Timeout... 
					// traitement special
					e.printStackTrace();
				}
			}

		} else {
			logger.info("Dynamic verif script");
			Vector<ScriptResult<Boolean>> scriptResults = new Vector<ScriptResult<Boolean>>();
			Vector<IMNode> nodeResults = new Vector<IMNode>();
			// lance la verif sur les nb premier
			int launched = 0;
			while(!nodes.isEmpty() && launched++ < nb.intValue()) {
				nodeResults.add(nodes.get(0));
				scriptResults.add(nodes.get(0).executeScript(verifyingScript));
				nodes.remove(0);
			}
			// Recupere les resultats
			while(!scriptResults.isEmpty() && !nodes.isEmpty() && found < nb.intValue()) {
				try {
//					int idx = ProActive.waitForAny(scriptResults, MAX_VERIF_TIMEOUT);
					int idx = ProActive.waitForAny(scriptResults);
					IMNode imnode = nodeResults.remove(idx);
					ScriptResult<Boolean> res = scriptResults.remove(idx);
					if(res.errorOccured()) {
						// nothing to do, just let the node in the free list
					} else if(res.getResult()){
						// Result OK
						nodeManager.setVerifyingScript(imnode, verifyingScript);
						imnode.clean();
						try {
							result.add(imnode.getNode());
							nodeManager.setBusy(imnode);
							found++;
						} catch (NodeException e) {
							nodeManager.setDown(imnode);
							// try on a new node
							nodeResults.add(nodes.get(0));
							scriptResults.add(nodes.remove(0).executeScript(verifyingScript));
						}
					} else {
						// result is false
						nodeManager.setNotVerifyingScript(imnode, verifyingScript);
						// try on a new node
						nodeResults.add(nodes.get(0));
						scriptResults.add(nodes.remove(0).executeScript(verifyingScript));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// Wait For Any Timeout... 
					// traitement special
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}

	public NodeSet getExactlyNodes(IntWrapper nb, VerifyingScript verifyingScript) {
		// TODO Auto-generated method stub
		return null;
	}

	public void nodeIsDown(IMNode imNode) {
		nodeManager.setDown(imNode);
	}

}
