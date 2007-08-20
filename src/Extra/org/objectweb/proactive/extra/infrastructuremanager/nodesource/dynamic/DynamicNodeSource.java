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
package org.objectweb.proactive.extra.infrastructuremanager.nodesource.dynamic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.EndActive;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeComparator;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.IMNodeSource;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.frontend.DynamicNSInterface;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;


public abstract class DynamicNodeSource extends IMNodeSource
    implements DynamicNSInterface, Serializable, InitActive, RunActive, EndActive {
	
	private Map<IMNode, Long> nodes;
    private ArrayList<IMNode> freeNodes;
    private ArrayList<IMNode> busyNodes;
    private ArrayList<IMNode> downNodes;
    private String stringId;
    private int nbMax;
    private int nice;
    private int ttr;

    public DynamicNodeSource(String id) {
        this.stringId = id;
    }

    public DynamicNodeSource() {
    }

    public void initActivity(Body body) {
        freeNodes = new ArrayList<IMNode>();
        busyNodes = new ArrayList<IMNode>();
        downNodes = new ArrayList<IMNode>();
        nodes = new HashMap<IMNode, Long>();
    }

	public void runActivity(Body body) {
		Service service = new Service(body);
		
		while(true /* TODO running */) {
			service.blockingServeOldest(3000 /* TODO TTR */);
			// fait ce que t'as a faire periodiquement
			
		}
	}

	public void endActivity(Body body) {
		// TODO
		// Si pas shut down => shut down
	}

    @Override
    public String getSourceId() {
        return stringId;
    }

    public int getNbMaxNodes() {
        return nbMax;
    }

    public int getNiceTime() {
        return nice;
    }

    public int getTimeToRelease() {
        return ttr;
    }

    public void setNbMaxNodes(int nb) {
        this.nbMax = nb;
    }

    public void setNiceTime(int nice) {
        this.nice = nice;
    }

    public void setTimeToRelease(int ttr) {
        this.ttr = ttr;
    }

    public ArrayList<IMNode> getAllNodes() {
        ArrayList<IMNode> res = new ArrayList<IMNode>();
        res.addAll(freeNodes);
        res.addAll(busyNodes);
        res.addAll(downNodes);
        return res;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IMNode> getBusyNodes() {
        return (ArrayList<IMNode>) busyNodes.clone();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IMNode> getDownNodes() {
        return (ArrayList<IMNode>) downNodes.clone();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<IMNode> getFreeNodes() {
        return (ArrayList<IMNode>) freeNodes.clone();
    }

    public IntWrapper getNbAllNodes() {
        return new IntWrapper(freeNodes.size() + busyNodes.size() +
            downNodes.size());
    }

    public IntWrapper getNbBusyNodes() {
        return new IntWrapper(busyNodes.size());
    }

    public IntWrapper getNbDownNodes() {
        return new IntWrapper(downNodes.size());
    }

    public IntWrapper getNbFreeNodes() {
        return new IntWrapper(freeNodes.size());
    }

    public ArrayList<IMNode> getNodesByScript(VerifyingScript script,
        boolean ordered) {
    	// TODO un peu de nettoyage sur les free nodes
    	
        ArrayList<IMNode> result = getFreeNodes();
        if ((script != null) && ordered) {
            Collections.sort(result, new IMNodeComparator(script));
        }
        return result;
    }

    public void setBusy(IMNode imnode) {
    	// TODO 
    	// juste mettre dans busy en théorie...
        removeFromAllLists(imnode);
        busyNodes.add(imnode);
        try {
            imnode.setBusy();
        } catch (NodeException e1) {
            // A down node shouldn't by busied...
            e1.printStackTrace();
        }
    }

    public void setDown(IMNode imnode) {
        //TODO 
    	// ca depends des cas :
    	// peut etre rendre directement le noeud a la source dynamique ?
        removeFromAllLists(imnode);
        downNodes.add(imnode);
        imnode.setDown(true);
    }

    public void setFree(IMNode imnode) {
        //TODO 
    	// verifier que le noeud n'est pas a rendre, sinon le rendre;
    	//if(isNodeToRelease())
    	// remettre dans free;
    }

    public BooleanWrapper shutdown() {
        // TODO
    	// rendre tous les noeuds a la source
        return null;
    }

    /**
     * Remove the imnode from all the lists it can appears.
     * @param imnode
     * @return
     */
    protected boolean removeFromAllLists(IMNode imnode) {
        // Free
        boolean free = freeNodes.remove(imnode);

        // Busy
        boolean busy = busyNodes.remove(imnode);

        // Down
        boolean down = downNodes.remove(imnode);

        return free || busy || down;
    }
    
    // obtain new nodes
    
    // update nodes status
    
    // release node
    protected boolean isNodeToRelease(IMNode node) {
    	// TODO
    	return false;
    }
    
    protected abstract void releaseNode(IMNode node) ;
    
    
}
