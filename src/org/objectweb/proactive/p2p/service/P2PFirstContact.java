/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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
package org.objectweb.proactive.p2p.service;

import java.io.Serializable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.ProActiveInternalObject;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.p2p.service.util.P2PConstants;


/**
 *
 * Activity to contact a first group of peers in parallel of the P2PService activity.
 *
 * @author Alexandre di Costanzo
 *
 * Created on Jan 4, 2005
 */
public class P2PFirstContact implements Serializable, RunActive, P2PConstants,
    ProActiveInternalObject {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.P2P_FIRST_CONTACT);
    private Vector<String> peers;
    private P2PAcquaintanceManager acqGroup;
    private P2PService localP2pService;

    /**
     * The empty constructor for <code>ProActive.newActive()</code>.
     */
    public P2PFirstContact() {
        // The empty constructor
    }

    /**
     * Construct a new <code>P2PFirstContact</code>.
     * @param peers list of peers.
     * @param acquaintances ProActive group of acquaintances.
     * @param local local P2P service.
     */
    public P2PFirstContact(Vector<String> peers,
        P2PAcquaintanceManager acquaintances, P2PService local) {
        this.peers = peers;
        this.acqGroup = acquaintances;
        this.localP2pService = local;
    }

    /**
     * Sequancialy try to contact all peer and add them in the group of acquaintances.
     *
     * @see org.objectweb.proactive.RunActive#runActivity(org.objectweb.proactive.Body)
     */
    public void runActivity(Body body) {
        this.peers = StartP2PService.checkingPeersUrl(this.peers);
        // First coontact
        connectingPeer();
        while (body.isAlive()) {
            if (this.peers.size() != 0) {
                // We have some url and we know nobody
                connectingPeer();
            }
            try {
                Thread.sleep(Long.parseLong(System.getProperty(PROPERTY_TTU)));
            } catch (Exception e) {
            }
        }
    }

    /**
     * Try to connect the peer with all peers inside list.
     */
    private void connectingPeer() {
        int size = this.peers.size();

        //while(!this.peers.isEmpty()) {
        for (int i = 0; i < size; i++) {
            String peerUrl = urlAdderP2PNodeName((String) this.peers.remove(0));
            try {
                Node distNode = NodeFactory.getNode(peerUrl);
                P2PService peer = (P2PService) distNode.getActiveObjects(P2PService.class.getName())[0];

                if (!peer.equals(this.localP2pService) &&
                        !this.acqGroup.contains(peer).booleanValue()) {
                    // Send a message to the remote peer to register myself
                    logger.info("P2PFirstContact.connectingPeer() ----" +
                        peerUrl);
                   // String[] url = peer.register(this.localP2pService);
                    String[] url =  (String[]) peer.register(this.localP2pService).toArray(new String[] {});

                    logger.info("P2PFirstContact.connectingPeer() ----" + url);
                    //a null reply means we have been accepted
                    if (url.length == 0) {
                        logger.info(peerUrl +
                            " has accepted to be our new acquaintance ");

                        // Add the peer in my group of acquaintances
                        this.acqGroup.add(peer);
                    } else {
                        logger.info(peerUrl +
                            " refused the acquaintance request, maybe the peer is full ");

                        for (int j = 0; j < url.length; j++) {
                            logger.info("Adding " + url[j] +
                                " as new candidate");
                            this.peers.add(url[j]);
                        }
                        //put it back for later use
                        this.peers.add(peerUrl);
                    }
                }
            } catch (Exception e) {
                logger.info("The peer at " + peerUrl +
                    " couldn't be contacted", e);
                //put it back for later use
                this.peers.add(peerUrl);
            }
        }
        if (this.acqGroup.size().intValue() == 0) {
            logger.info("No peer could be found to join the network");
        }
    }

    /**
     * Add the default name of the P2P Node to a specified <code>url</code>.
     * @param url  the url.
     * @return the <code>url</code> with the name of the P2P Node.
     */
    private static String urlAdderP2PNodeName(String url) {
        if (url.endsWith("/")) {
            url += P2P_NODE_NAME;
        } else {
            url += ("/" + P2P_NODE_NAME);
        }

        return url;
    }
}
