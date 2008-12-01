/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extra.p2pTest.p2p;

import java.util.Hashtable;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.extra.p2p.monitoring.Dumper;
import org.objectweb.proactive.extra.p2p.service.P2PService;
import org.objectweb.proactive.extra.p2p.service.node.P2PNodeLookup;
import org.objectweb.proactive.extra.p2p.service.util.P2PConstants;
import org.objectweb.proactive.extra.p2p.service.util.UniversalUniqueID;
import org.objectweb.proactive.extra.p2pTest.messages.TracerWithCallbackMessage;


/**
 * @author The ProActive Team
 * Main and CallBack and TracerWithCallbackMessage
 * Main can: -starts the topology dump from an address
 *            -trace the number of acquaintance for all the p2p nodes
 *            -perform a request to get a ProActive node
 */
public class Tracer implements java.io.Serializable {
    public static final boolean DEBUG = true;
    private static final String USAGE = Tracer.class.getName() +
        " address[ex://fiacre.inria.fr/] opt[dump/trace/reqnode]";
    public static final int MAX_NODE = 30;
    public static final int TIME = 4000;
    public static final int TTL = 6;
    public Hashtable<String, Integer> fileTabPosition = new Hashtable<String, Integer>();
    public int currentTabPos = 0;
    public int[] cache = new int[MAX_NODE];

    //Pour ProActive
    public Tracer() {
    }

    /**
     * Dump the topology thanks to org.objectweb.proactive.extra.p2p.v2.monitoring
     * @param addr entry point address
     */
    public static void dumpP2PNetwork(String addr) {
        Dumper dumper = null;

        try {
            dumper = (Dumper) PAActiveObject.newActive(Dumper.class.getName(), new Object[] {});
            Dumper.requestAcquaintances(addr, dumper);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dumper.dumpPeersAsText();
    }

    /**
     * Sending to P2PService a TracerWithCallbackMessage with ProActive.getStubOnThis() for the callbacks
     * @param distP2PService distant P2PService
     */
    public void sendTrace(P2PService distP2PService) {
        try {
            for (int i = 0; i < MAX_NODE; i++) {
                cache[i] = 0;
            }

            distP2PService.dumpAcquaintances(new TracerWithCallbackMessage(TTL, UniversalUniqueID
                    .randomUUID(), (Tracer) PAActiveObject.getStubOnThis()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printCache() {
        int i = 0;

        while (i < MAX_NODE) {
            System.out.print(cache[i++] + " ");
        }

        System.out.println();
    }

    /**
     * Offers a way to retrieve or create an unique identifier for the node address, during all the runtime
     * @param addr node address
     * @return an unique identifier for the node address
     */
    public int getTabPositionOf(String addr) {
        int current;

        if (fileTabPosition.containsKey(addr)) {
            return fileTabPosition.get(addr);
        } else {
            current = currentTabPos++;
            fileTabPosition.put(addr, current);

            return current;
        }
    }

    /**
     * TracerWithCallbackMessage callbacks. Updates the number of neighbors
     * @param addr address of the node making the callback
     * @param nbAcquaintances number of acquaintance of the node making the callback
     */
    public void trace(String addr, int nbAcquaintances) {
        if (DEBUG) {
            System.out.println("addr [" + getTabPositionOf(addr) + "]:" + addr + " Acquaintances :" +
                nbAcquaintances);
        }

        cache[getTabPositionOf(addr)] = nbAcquaintances;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("USAGE:" + USAGE);

            return;
        }

        String addr = args[0] + P2PConstants.P2P_NODE_NAME;

        if (args[1].equalsIgnoreCase("dump")) {
            dumpP2PNetwork(addr);
        } else if (args[1].equalsIgnoreCase("trace")) {
            try {
                Tracer t = (Tracer) PAActiveObject.newActive(Tracer.class.getName(), null);

                Node distNode = NodeFactory.getNode(addr);
                P2PService p2p = (P2PService) distNode.getActiveObjects(P2PService.class.getName())[0];

                while (true) {
                    t.sendTrace(p2p);
                    Thread.sleep(TIME);
                    t.printCache();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (args[1].equalsIgnoreCase("reqnode")) {
            try {
                Node distNode = NodeFactory.getNode(addr);
                P2PService p2p = (P2PService) distNode.getActiveObjects(P2PService.class.getName())[0];
                System.out.println("DEBUT");

                P2PNodeLookup lookup = p2p.getNodes(1, "test0", "test1");
                System.out.println("FIN");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
