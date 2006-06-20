package org.objectweb.proactive.p2p.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.p2p.service.P2PService;
import org.objectweb.proactive.p2p.service.node.P2PNodeLookup;
import org.objectweb.proactive.p2p.service.util.UniversalUniqueID;


public class Dumper {
    protected HashMap<String, P2PNode> senders = new HashMap<String, P2PNode>();
    protected HashMap<String, Link> links = new HashMap<String, Link>();

    //protected ArrayList<Link> links = new ArrayList<Link>();
    private int index;

    public Dumper() {
    }

    protected void addAsSender(AcquaintanceInfo i) {
        String s = i.getSender();
        P2PNode tmp = senders.get(s);
        if ((tmp == null) || (tmp.getIndex() == -1)) {
            senders.put(s,
                new P2PNode(s, index++, i.getCurrentNoa(), i.getNoa()));
        }
    }

    public void addAsSender(String s) {
        if (senders.get(s) == null) {
            senders.put(s, new P2PNode(s));
        }
    }

    /**
     * Receive a dump from a peer
     * The sender is put in an arrayList and some Links are created
     * @param info
     */
    public void receiveAcqInfo(AcquaintanceInfo info) {
        //use the size of the hashmap to give each sender a unique index
        System.out.println(">>>>");
        System.out.println(info.getSender() + " current Noa =  " +
            info.getCurrentNoa() + " max Noa= " + info.getNoa());
        this.addAsSender(info);
        //senders.put(info.getSender(), senders.size());
        String[] acq = info.getAcq();
        String source = info.getSender();
        for (int i = 0; i < acq.length; i++) {
            System.out.println(" Acquaintance: " + acq[i]);
            //check that the destination is in our list 
            //otherwise add them
            this.addAsSender(acq[i]);
            String dest = acq[i];

            addLink(source, dest);
        }
        System.out.println("    --- Awaiting ");
        String[] tmp = info.getAwaitedReplies();
        for (int i = 0; i < tmp.length; i++) {
            System.out.println(tmp[i]);
        }
        System.out.println("    ------------------");

        System.out.println("<<<<");
    }

	public void addLink(String source, String dest) {
		//our links are considered bi-directional
		//ie a->b and b->a will be a a<->b link
		//so we switch source/destination based on lexical order
		if (source.compareTo(dest) <= 0) {
		    links.put(source + dest, new Link(source, dest));
		} else {
		    links.put(dest + source, new Link(dest, source));
		}
	}

    /**
     * Dump the acqaintances list to use with Otter
     * The following format is used
     *   Node :   ? index name
     *   Link : L index sourceIndex destIndex
     */
    public void dumpAcqForOtter() {
        //first indicate the number of nodes and links
        System.out.println("t " + senders.size());
        System.out.println("T " + links.size());
        //color by number of acquaintances
        System.out.println("g 1 d 2 Metric ");
        System.out.println("f 1 NOA'max NOA");

        //   System.out.println("f 1 max NOA");
        //dump the nodes with their indexes
        Set<Map.Entry<String, P2PNode>> map = (Set<Map.Entry<String, P2PNode>>) senders.entrySet();
        Iterator it = map.iterator();
        while (it.hasNext()) {
            Map.Entry<String, P2PNode> entry = (Map.Entry<String, P2PNode>) it.next();

            // the node might have a -1 index because has never sent anything
            // we want to get rid of this
            if (entry.getValue().getIndex() == -1) {
                entry.getValue().setIndex(index++);
            }
            System.out.println("? " + entry.getValue().getIndex() + " " +
                entry.getKey());
            //and its associated noa
            System.out.println("v " + entry.getValue().getIndex() + " 1 " +
                entry.getValue().getNoa() + "'" + entry.getValue().getMaxNOA());
            // System.out.println("v " + entry.getValue().getIndex() + " 1 " + entry.getValue().getNoa());
        }

        //now dump the links
        int i = 0;

        Set<Map.Entry<String, Link>> map2 = (Set<Map.Entry<String, Link>>) links.entrySet();

        it = map2.iterator();
        while (it.hasNext()) {
            Link entry = ((Map.Entry<String, Link>) it.next()).getValue();
            //  System.out.println("---- looking for sender " + entry.getSource());
            System.out.println("L " + i++ + " " +
                senders.get(entry.getSource()).getIndex() + " " +
                senders.get(entry.getDestination()).getIndex());
        }
    }

    public void dumpAsText() {
    	   //now dump the links
        int i = 0;

        Set<Map.Entry<String, Link>> map2 = (Set<Map.Entry<String, Link>>) links.entrySet();

        Iterator it = map2.iterator();
        while (it.hasNext()) {
            Link entry = ((Map.Entry<String, Link>) it.next()).getValue();
            //  System.out.println("---- looking for sender " + entry.getSource());
            System.out.println(entry.getSource() + " <---> " + entry.getDestination());
        }
    }
    
    public HashMap getLinks() {
    	return this.links;
    }
    
    public static void requestAcquaintances(String ref) {
        P2PService p2p = null;
        Node distNode = null;
        try {
            distNode = NodeFactory.getNode(ref);
        } catch (NodeException e) {
            e.printStackTrace();
        }
        try {
            p2p = (P2PService) distNode.getActiveObjects(P2PService.class.getName())[0];
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        }

        System.out.println("Dumper ready to call!");
        p2p.dumpAcquaintances();
    }

    public static void requestAcquaintances(String ref, Dumper d) {
        P2PService p2p = null;
        Node distNode = null;
        try {
            distNode = NodeFactory.getNode(ref);
            p2p = (P2PService) distNode.getActiveObjects(P2PService.class.getName())[0];
            System.out.println("Dumper ready to call!");
            p2p.dumpAcquaintances(new DumpACQWithCallback(10,
                    UniversalUniqueID.randomUUID(), d));
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        }
    }

    public static void getNodes(String ref) {
        P2PService p2p = null;
        Node distNode = null;
        try {
            distNode = NodeFactory.getNode(ref);
        } catch (NodeException e) {
            e.printStackTrace();
        }
        try {
            p2p = (P2PService) distNode.getActiveObjects(P2PService.class.getName())[0];
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        }

        System.out.println("Dumper requesting nodes");
        P2PNodeLookup nol = p2p.getNodes(1, "blih", "blah");
        Vector v = nol.getNodes();
        for (Iterator iter = v.iterator(); iter.hasNext();) {
            Node element = (Node) iter.next();
            System.out.println(element.getNodeInformation().getName());
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage : " + Dumper.class.getName() + " <URL>" +
                "<descriptor>");
            System.exit(-1);
        }

        Dumper d = null;

        try {
            d = (Dumper) ProActive.newActive(Dumper.class.getName(), null);
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }

        //            requestAcquaintances(args[0]);
        requestAcquaintances(args[0], d);
        try {
            Thread.sleep(20000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        d.dumpAcqForOtter();

        //        getNodes(args[0]);
    }
}
