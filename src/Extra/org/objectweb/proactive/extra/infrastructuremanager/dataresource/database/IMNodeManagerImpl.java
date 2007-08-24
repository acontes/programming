/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extra.infrastructuremanager.dataresource.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map.Entry;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeComparator;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager;
import org.objectweb.proactive.extra.scheduler.scripting.ScriptResult;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;


public class IMNodeManagerImpl implements IMNodeManager {
    // FIELDS
    /** Free Nodes **/
    private ArrayList<IMNode> freeNodes;

    /** Busy Nodes **/
    private ArrayList<IMNode> busyNodes;

    /** Down Nodes **/
    private ArrayList<IMNode> downNodes;

    /** Nodes waiting for Script ending **/
    private ArrayList<WaitingHandler> waitingNodes;

    public IMNodeManagerImpl() {
        freeNodes = new ArrayList<IMNode>();
        busyNodes = new ArrayList<IMNode>();
        downNodes = new ArrayList<IMNode>();
        waitingNodes = new ArrayList<WaitingHandler>();
    }

    /**
     * Adding an {@link IMNode} : this new Node goes directly to the free nodes list.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#addIMNode(org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode)
     */
    public void addIMNode(IMNode imnode) {
        freeNodes.add(imnode);
    }

    /**
     * Same that {@link #addIMNode(IMNode)}, but for many nodes.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#addIMNodes(java.util.Collection)
     */
    public void addIMNodes(Collection<IMNode> imnodes) {
        freeNodes.addAll(imnodes);
    }

    /**
     * The list of all {@link IMNode} in the Node Manager
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#getListAllIMNode()
     */
    public ArrayList<IMNode> getAllNodes() {
        ArrayList<IMNode> result = new ArrayList<IMNode>();
        result.addAll(freeNodes);
        result.addAll(busyNodes);
        result.addAll(downNodes);
        result.addAll(getListWaitingIMNode());
        return result;
    }

    /**
     * List of busy nodes : nodes used by someone.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#getListBusyIMNode()
     */
    @SuppressWarnings("unchecked")
    public ArrayList<IMNode> getBusyNodes() {
        return (ArrayList<IMNode>) busyNodes.clone();
    }

    /**
     * Down nodes are nodes not responding.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#getListDownIMNode()
     */
    @SuppressWarnings("unchecked")
    public ArrayList<IMNode> getDownNodes() {
        return (ArrayList<IMNode>) downNodes.clone();
    }

    /**
     * Free nodes are nodes that can be given by the IM.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#getListFreeIMNode()
     */
    @SuppressWarnings("unchecked")
    public ArrayList<IMNode> getFreeNodes() {
        return (ArrayList<IMNode>) freeNodes.clone();
    }

    /**
     * Waiting nodes are nodes that may respond.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#getListWaitingIMNode()
     */
    public ArrayList<IMNode> getListWaitingIMNode() {
        ArrayList<IMNode> res = new ArrayList<IMNode>();
        for (WaitingHandler wh : waitingNodes)
            res.add(wh.imnode);
        return res;
    }

    /**
     * return the number of nodes in the node manager, including
     * busy and waiting nodes.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#getNbAllIMNode()
     */
    public IntWrapper getNbAllNodes() {
        return new IntWrapper(freeNodes.size() + busyNodes.size() +
            downNodes.size() + waitingNodes.size());
    }

    /**
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#getNbBusyIMNode()
     */
    public IntWrapper getNbBusyNodes() {
        return new IntWrapper(busyNodes.size());
    }

    /**
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#getNbDownIMNode()
     */
    public IntWrapper getNbDownNodes() {
        return new IntWrapper(downNodes.size());
    }

    /**
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#getNbFreeIMNode()
     */
    public IntWrapper getNbFreeNodes() {
        return new IntWrapper(freeNodes.size());
    }

    /**
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#getNbWaitingIMNode()
     */
    public int getNbWaitingIMNode() {
        return waitingNodes.size();
    }

    /**
     * Return the nodes in a specific order :
     * - if there is no script to verify, just return the free nodes ;
     * - if there is a script, tries to give the nodes in an efficient order :
     *                 -> First the nodes that verified the script before ;
     *                 -> Next, the nodes that haven't been tested ;
     *                 -> Next, the nodes that have allready verified the script, but no longer ;
     *                 -> To finish, the nodes that don't verify the script.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#getNodesByScript(org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript)
     */
    public ArrayList<IMNode> getNodesByScript(VerifyingScript script,
        boolean ordered) {
        ArrayList<IMNode> result = getFreeNodes();
        verifyWaitingNodes();
        if ((script != null) && ordered) {
            Collections.sort(result, new IMNodeComparator(script));
        }
        return result;
    }

    /**
     * Search for responding nodes in the waiting list.
     * Depending on the result, updates the node verifyig status
     * about the corresponding script.
     *
     */
    private void verifyWaitingNodes() {
        ListIterator<WaitingHandler> iter = waitingNodes.listIterator();
        while (iter.hasNext()) {
            WaitingHandler wh = iter.next();
            if (!ProActive.isAwaited(wh.futureResult)) { // Arrived
                iter.remove();
                if (ProActive.isException(wh.futureResult)) {
                    // An error occured, independant of the script
                    // so the node is down
                    setDown(wh.imnode);
                } else if (wh.futureResult.errorOccured()) {
                    // error on script : can't do anything
                    // for now...
                    wh.futureResult.getException().printStackTrace();
                } else if (wh.futureResult.getResult()) {
                    // the node verify the script
                    setVerifyingScript(wh.imnode, wh.script);
                } else {
                    // the node not verify the script
                    setNotVerifyingScript(wh.imnode, wh.script);
                }
            }
        }
    }

    /**
     * remove a node from the structure.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#removeIMNode(org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode)
     */
    public void removeIMNode(IMNode imnode) {
        removeFromAllLists(imnode);
    }

    /**
     * remove many nodes from the structure.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#removeIMNodes(java.util.Collection)
     */
    public void removeIMNodes(Collection<IMNode> imnodes) {
        for (IMNode imnode : imnodes)
            removeIMNode(imnode);
    }

    /**
     * Set the busy state, and move the node to the internal busy list.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#setBusy(org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode)
     */
    public void setBusy(IMNode imnode) {
        removeFromAllLists(imnode);
        busyNodes.add(imnode);
        try {
            imnode.setBusy();
        } catch (NodeException e1) {
            // A down node shouldn't by busied...
            e1.printStackTrace();
        }
    }

    /**
     * Set the down state, and move the node to the internal down list.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#setDown(org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode)
     */
    public void setDown(IMNode imnode) {
        removeFromAllLists(imnode);
        downNodes.add(imnode);
        imnode.setDown(true);
    }

    /**
     * Set the free state, and move the node to the internal free list.
     * Update the node status.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#setFree(org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode)
     */
    public void setFree(IMNode imnode) {
        removeFromAllLists(imnode);
        freeNodes.add(imnode);
        try {
            imnode.setFree();
        } catch (NodeException e) {
            // A down node shouldn't by busied...
            e.printStackTrace();
        }
        HashMap<VerifyingScript, Integer> verifs = imnode.getScriptStatus();
        for (Entry<VerifyingScript, Integer> entry : verifs.entrySet()) {
            if (entry.getKey().isDynamic() &&
                    (entry.getValue() == IMNode.VERIFIED_SCRIPT)) {
                entry.setValue(IMNode.ALREADY_VERIFIED_SCRIPT);
            }
        }
    }

    /**
     * Update the node status.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#setNotVerifyingScript(org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode, org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript)
     */
    public void setNotVerifyingScript(IMNode imnode, VerifyingScript script) {
        HashMap<VerifyingScript, Integer> verifs = imnode.getScriptStatus();
        if (verifs.containsKey(script)) {
            int status = verifs.remove(script);
            if (status == IMNode.NOT_VERIFIED_SCRIPT) {
                verifs.put(script, IMNode.NOT_VERIFIED_SCRIPT);
            } else {
                verifs.put(script, IMNode.NO_LONGER_VERIFIED_SCRIPT);
            }
        } else {
            verifs.put(script, IMNode.NOT_VERIFIED_SCRIPT);
        }
    }

    /**
     * Update the node status.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#setVerifyingScript(org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode, org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript)
     */
    public void setVerifyingScript(IMNode imnode, VerifyingScript script) {
        HashMap<VerifyingScript, Integer> verifs = imnode.getScriptStatus();
        if (verifs.containsKey(script)) {
            verifs.remove(script);
        }
        verifs.put(script, IMNode.VERIFIED_SCRIPT);
    }

    /**
     * The node is still busy, and we are waiting for the script result.
     * @see org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager#setWaitingForScriptResult(org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode, org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript, org.objectweb.proactive.extra.scheduler.scripting.ScriptResult)
     */
    public void setWaitingForScriptResult(IMNode imnode,
        VerifyingScript script, ScriptResult<Boolean> futureResult) {
        removeFromAllLists(imnode);
        waitingNodes.add(new WaitingHandler(imnode, script, futureResult));
        try {
            imnode.setBusy();
        } catch (NodeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Remove the imnode from all the lists it can appears.
     * @param imnode
     * @return
     */
    private boolean removeFromAllLists(IMNode imnode) {
        // Free
        boolean free = freeNodes.remove(imnode);

        // Busy
        boolean busy = busyNodes.remove(imnode);

        // Down
        boolean down = downNodes.remove(imnode);

        // Waiting
        boolean waiting = false;
        ListIterator<WaitingHandler> li = waitingNodes.listIterator();
        while (li.hasNext()) {
            WaitingHandler wh = li.next();
            if (wh.imnode.equals(imnode)) {
                li.remove();
                waiting = true;
            }
        }

        return free || busy || down || waiting;
    }

    /**
     * Internal task for Node in the 'waiting' state.
     * @author ProActive Team
     * @version 1.0, Jul 12, 2007
     * @since ProActive 3.2
     */
    private class WaitingHandler {
        public IMNode imnode;
        public VerifyingScript script;
        public ScriptResult<Boolean> futureResult;

        public WaitingHandler(IMNode imnode, VerifyingScript script,
            ScriptResult<Boolean> futureResult) {
            this.imnode = imnode;
            this.script = script;
            this.futureResult = futureResult;
        }
    }

    public BooleanWrapper shutdown() {
        // Something to do ?
        return new BooleanWrapper(true);
    }
}
