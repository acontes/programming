package org.objectweb.proactive.extra.infrastructuremanager.nodesource;

import java.util.ArrayList;
import java.util.Collections;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeComparator;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNodeManager;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.dynamic.DynamicNodeSource;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.pad.PADNodeSource;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;

/**
 * The IMNodeSourceManager is made to manage differents IMNodeSource,
 * and is also seen like a IMNodeSource from the outside.
     * This object use the <i>Composite</i> Design Pattern : it's allow
     * to manage different node sources as one.
 * @author proactive team
 *
 */
public class IMNodeSourceManager extends IMNodeSource {
	private String id;
    private PADNodeSource padNS;
    private ArrayList<DynamicNodeSource> dynNS;
    //private Map<IMNode, IMNodeSource> nodes;

    /**
     * Create a {@link PADNodeSource} at starting, on a given node (current one if null).
     */
    public IMNodeSourceManager(String sourceId, Node nodeIM)
        throws ActiveObjectCreationException, NodeException {
        padNS = (PADNodeSource) ProActive.newActive(PADNodeSource.class.getCanonicalName(),
                new Object[] {  }, nodeIM);
        
        dynNS = new ArrayList<DynamicNodeSource>();
        //nodes = new HashMap<IMNode, IMNodeSource>();
        this.id = sourceId;
    }

    // PAD NS MANAGMENT
    /**
     * @return the {@link PADNodeSource}.
     */
    public PADNodeSource getPADNodeSource() {
        return padNS;
    }

    // DYNAMIC NS MANAGMENT
    /**
     * Get the DynamicNodeSources.
     * @param dns
     */
    @SuppressWarnings("unchecked")
	public ArrayList<DynamicNodeSource> getDynamicNodeSources() {
    	return (ArrayList<DynamicNodeSource>) dynNS.clone();
    }
    
    /**
     * Add a new {@link DynamicNodeSource} to this manager.
     * @param dns
     */
    public void addDynamicNodeSource(DynamicNodeSource dns) {
    	dynNS.add(dns);
    }
    
    /**
     * Remove the given {@link DynamicNodeSource} from the manager.
     * @param dns
     */
    public void removeDynamicNodeSource(DynamicNodeSource dns) {
    	dynNS.remove(dns);
    }
    
    // METHODS FROM IMNODEMANAGER
    /**
     * @see IMNodeManager#getNodesByScript(VerifyingScript, boolean)
     */
    public ArrayList<IMNode> getNodesByScript(VerifyingScript script, boolean ordered) {
    	ArrayList<IMNode> res = new ArrayList<IMNode>();
    	res.addAll(padNS.getNodesByScript(script, false));
    	for(DynamicNodeSource dns : dynNS) {
    		 res.addAll(dns.getNodesByScript(script, false));
    	}
    	if(script != null && ordered) {
            Collections.sort(res, new IMNodeComparator(script));
    	}
    	return res;
    }

    public void setBusy(IMNode imnode) {
    	IMNodeSource ns = imnode.getNodeSource();
    	if(ns != null)
    		ns.setBusy(imnode);
    }

    public void setDown(IMNode imnode) {
    	IMNodeSource ns = imnode.getNodeSource();
    	if(ns != null)
    		ns.setDown(imnode);
    }

    public void setFree(IMNode imnode) {
    	IMNodeSource ns = imnode.getNodeSource();
    	if(ns != null)
    		ns.setFree(imnode);
    }

    public BooleanWrapper shutdown() {
    	Boolean res = padNS.shutdown().booleanValue();
    	for(DynamicNodeSource dns : dynNS)
    		res = res.booleanValue() && dns.shutdown().booleanValue();
    	return new BooleanWrapper(res);
    }

	@Override
	public String getSourceId() {
		return id;
	}

	public ArrayList<IMNode> getAllNodes() {
		ArrayList<IMNode> nodes = new ArrayList<IMNode>();
		nodes.addAll(padNS.getAllNodes());
    	for(DynamicNodeSource dns : dynNS)
    		nodes.addAll(dns.getAllNodes());
    	return nodes;
	}

	public ArrayList<IMNode> getBusyNodes() {
		ArrayList<IMNode> nodes = new ArrayList<IMNode>();
		nodes.addAll(padNS.getBusyNodes());
    	for(DynamicNodeSource dns : dynNS)
    		nodes.addAll(dns.getBusyNodes());
    	return nodes;
	}

	public ArrayList<IMNode> getDownNodes() {
		ArrayList<IMNode> nodes = new ArrayList<IMNode>();
		nodes.addAll(padNS.getDownNodes());
    	for(DynamicNodeSource dns : dynNS)
    		nodes.addAll(dns.getDownNodes());
    	return nodes;
	}

	public ArrayList<IMNode> getFreeNodes() {
		ArrayList<IMNode> nodes = new ArrayList<IMNode>();
		nodes.addAll(padNS.getFreeNodes());
    	for(DynamicNodeSource dns : dynNS)
    		nodes.addAll(dns.getFreeNodes());
    	return nodes;
	}

	public IntWrapper getNbAllNodes() {
		int nb = padNS.getNbAllNodes().intValue();
    	for(DynamicNodeSource dns : dynNS)
    		nb += dns.getNbAllNodes().intValue();
		return new IntWrapper(nb);
	}

	public IntWrapper getNbBusyNodes() {
		int nb = padNS.getNbBusyNodes().intValue();
    	for(DynamicNodeSource dns : dynNS)
    		nb += dns.getNbBusyNodes().intValue();
		return new IntWrapper(nb);
	}

	public IntWrapper getNbDownNodes() {
		int nb = padNS.getNbDownNodes().intValue();
    	for(DynamicNodeSource dns : dynNS)
    		nb += dns.getNbDownNodes().intValue();
		return new IntWrapper(nb);
	}

	public IntWrapper getNbFreeNodes() {
		int nb = padNS.getNbFreeNodes().intValue();
    	for(DynamicNodeSource dns : dynNS)
    		nb += dns.getNbFreeNodes().intValue();
		return new IntWrapper(nb);
	}
}
