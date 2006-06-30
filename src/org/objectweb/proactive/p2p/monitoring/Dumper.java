package org.objectweb.proactive.p2p.monitoring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.p2p.service.P2PService;
import org.objectweb.proactive.p2p.service.node.P2PNodeLookup;
import org.objectweb.proactive.p2p.service.util.UniversalUniqueID;


public class Dumper {
//    protected HashMap<String, P2PNode> senders = new HashMap<String, P2PNode>();
//    protected HashMap<String, Link> links = new HashMap<String, Link>();

    //protected ArrayList<Link> links = new ArrayList<Link>();
//    private int index;
	
	protected P2PNetwork network = new P2PNetwork();

    public Dumper() {
    }

//    protected void addAsSender(AcquaintanceInfo i) {
//        String s = i.getSender();
//        P2PNode tmp = senders.get(s);
//        if ((tmp == null) || (tmp.getIndex() == -1)) {
//            senders.put(s,
//                new P2PNode(s, index++, i.getCurrentNoa(), i.getNoa()));
//        } else {
//        	if (tmp!=null) {
//        		tmp.setMaxNOA(i.getNoa());
//        		tmp.setNoa(i.getCurrentNoa());
//        	}
//        }
//    }
//
//    public void addAsSender(String s) {
//        if (senders.get(s) == null) {
//            senders.put(s, new P2PNode(s));
//        }
//    }
//    
//    public void addAsSender(String s, int noa, int maxNoa) {
//    	P2PNode p = senders.get(s);
//        if (p == null) {
//            p = new P2PNode(s);
//        	p.setNoa(noa);
//        	p.setMaxNOA(maxNoa);
//            senders.put(s, p);
//        } else {
//        	p.setNoa(noa);
//        	p.setMaxNOA(maxNoa);
//        }
//    }
    

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
   //     this.addAsSender(info);
        this.network.addAsSender(info);
        //senders.put(info.getSender(), senders.size());
        String[] acq = info.getAcq();
        String source = info.getSender();
        for (int i = 0; i < acq.length; i++) {
            System.out.println(" Acquaintance: " + acq[i]);
            //check that the destination is in our list 
            //otherwise add them
//            this.addAsSender(acq[i]);
            this.network.addAsSender(acq[i]);
            String dest = acq[i];

           this.network.addLink(source, dest);
        }
        System.out.println("    --- Awaiting ");
        String[] tmp = info.getAwaitedReplies();
        for (int i = 0; i < tmp.length; i++) {
            System.out.println(tmp[i]);
        }
        System.out.println("    ------------------");

        System.out.println("<<<<");
    }

//	public void addLink(String source, String dest) {
//		//our links are considered bi-directional
//		//ie a->b and b->a will be a a<->b link
//		//so we switch source/destination based on lexical order
//		if (source.compareTo(dest) <= 0) {
//		    links.put(source + dest, new Link(source, dest));
//		} else {
//		    links.put(dest + source, new Link(dest, source));
//		}
//	}

    /**
     * Dump the acqaintances list to use with Otter
     * The following format is used
     *   Node :   ? index name
     *   Link : L index sourceIndex destIndex
     */
    public void dumpAcqForOtter() {
    	HashMap<String, P2PNode> senders = network.getSenders();
    	HashMap<String, Link> links = network.getLinks();
    	int index = senders.size();
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
    	HashMap<String, Link> links = network.getLinks();
        Set<Map.Entry<String, Link>> map2 = (Set<Map.Entry<String, Link>>) links.entrySet();

        Iterator it = map2.iterator();
        while (it.hasNext()) {
            Link entry = ((Map.Entry<String, Link>) it.next()).getValue();
            //  System.out.println("---- looking for sender " + entry.getSource());
            System.out.println(entry.getSource() + " <---> " + entry.getDestination());
        }
    }
    
//    public HashMap getLinks() {
//    	return this.links;
//    }
//    
    
//    public HashMap getSenders() {
//    	return this.senders;
//    }
    
    
    public P2PNetwork getP2PNetwork() {
    	return this.network;
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
    
    
    public void createGraphFromFile2(String name) {
//        Dumper dump = new Dumper();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(new File(name)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String s = null;
        String current = null;

        //what we are reading now
        //  0 = nothing interesting
        //  1 = awaiting peer name
        //  2 = reading acquaintances name
        int readingStatus = 0;

        try {
            while ((s = in.readLine()) != null) {
                // System.out.println(s);
                if (s.indexOf(">>>") >= 0) {
                    //begining of a new Peer
                    readingStatus = 1;
                } else if (s.indexOf("<<<") >= 0) {
                    //end of a new Peer
                    readingStatus = 0;
                } else {
                    //reading some peer name 
                    switch (readingStatus) {
                    case 1: {
                        // example of string
                        // "trinidad.inria.fr:2410 current Noa =  1 max Noa= 3"
                        Pattern pattern = Pattern.compile(
                                "(.*) current .* =  (.*) max Noa= (.*)");
                        Matcher matcher = pattern.matcher(s);
                        boolean matchFound = matcher.find();

                        if (matchFound) {
                            // Get all groups for this match
                            //    for (int i=1; i<=matcher.groupCount(); i++) {
                            s = matcher.group(1);
                            //System.out.println(groupStr);
                            //  }
                            //}

                            //s= s.substring(0, s.indexOf("current")-1);
                            network.addAsSender(s,
                                Integer.parseInt(matcher.group(2)),
                                Integer.parseInt(matcher.group(3)));
                            current = s;
                            readingStatus = 2;
                        }
                        break;
                    }
                    case 2: {
                        //we are either reading a machine name or some garbage
                        if (s.indexOf("---") < 0) {
                            s = this.cleanURL(s);
                            System.out.println(s);
                            network.addAsSender(s);
                            network.addLink(current, s);
                            try {
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            readingStatus = 3;
                        }
                        break;
                    }
                    case 3: {
                        if (s.indexOf("---") >= 0) {
                            readingStatus = 2;
                        }
                        break;
                    }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.dumpAsText();
//        this.generateGraphNodes(dump);
//        this.generateGraphLinks(dump);
    }
    
    public String cleanURL(String s) {
        if (s.indexOf("Acquaintance:") > 0) {
            s = s.substring("Acquaintance:".length() + 2);
        }
        try {
            return UrlBuilder.getHostNameAndPortFromUrl(s);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return s;
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
