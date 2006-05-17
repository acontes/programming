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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.ProActiveInternalObject;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.BooleanMutableWrapper;
import org.objectweb.proactive.core.util.wrapper.IntMutableWrapper;
import org.objectweb.proactive.p2p.service.messages.Message;
import org.objectweb.proactive.p2p.service.util.NOAPowerLawGenerator;
import org.objectweb.proactive.p2p.service.util.P2PConstants;


/**
 * Updating the group of exportAcquaintances of the P2P service.
 *
 * @author Alexandre di Costanzo
 *
 */
public class P2PAcquaintanceManager implements InitActive, RunActive,
    Serializable, P2PConstants, ProActiveInternalObject {
    private final static Logger logger = ProActiveLogger.getLogger(Loggers.P2P_ACQUAINTANCES);

    //    static public final int NOA = Integer.parseInt(System.getProperty(
    //            P2PConstants.PROPERTY_NOA));
    static public final int NOA = new NOAPowerLawGenerator(3, 7, -3).nextInt();
    private P2PService localService = null;
    private P2PService acquaintancesActived = null;
    protected AcquaintancesWrapper acquaintances;

    //store the name of awaited replies for setting acquaintances
    protected HashMap<String,P2PService> awaitedReplies = new HashMap<String,P2PService>();
    
    //list of prefered acquaintances
    private Vector<String> peers = new Vector<String>();

    /**
     * The empty constructor for activating.
     */
    public P2PAcquaintanceManager() {
        // empty constructor
    }

    /**
     * Construct a new <code>P2PAcquaintanceManager</code>.
     * @param localService a reference to the local P2P service.
     */
    public P2PAcquaintanceManager(P2PService localService) {
        this.localService = localService;
    }

    /**
     * @see org.objectweb.proactive.InitActive#initActivity(org.objectweb.proactive.Body)
     */
    public void initActivity(Body body) {
        // String nodeUrl = body.getNodeURL();
        this.acquaintances = new AcquaintancesWrapper();

        // Create exportAcquaintances group
        //       try {
        //            this.acquaintances = (P2PService) ProActiveGroup.newGroup(P2PService.class.getName());
        //            ProActive.addNFEListenerOnGroup(this.acquaintances,
        //                FailedGroupRendezVousException.AUTO_GROUP_PURGE);
        //            this.groupOfAcquaintances = ProActiveGroup.getGroup(acquaintances);
        //          this.acquaintancesActived = (P2PService) ProActiveGroup.turnActiveGroup(acquaintances,
        //                   nodeUrl);
        //        } catch (ClassNotReifiableException e) {
        //            logger.fatal("Couldn't create the group of exportAcquaintances", e);
        //        } catch (ClassNotFoundException e) {
        //            logger.fatal("Couldn't create the group of exportAcquaintances", e);
        //        } catch (ActiveObjectCreationException e) {
        //            logger.fatal("Couldn't create the group of exportAcquaintances", e);
        //        } catch (NodeException e) {
        //            logger.fatal("Couldn't create the group of exportAcquaintances", e);
        //        }
        logger.debug("Group of exportAcquaintances successfuly created");
    }

    /**
     * @see org.objectweb.proactive.RunActive#runActivity(org.objectweb.proactive.Body)
     */
    public void runActivity(Body body) {
        Service service = new Service(body);

        while (body.isActive()) {
            if (this.acquaintances.size() > 0) {
                // Register the local P2P service in all exportAcquaintances
                logger.debug("Sending heart-beat");
                this.acquaintances.getAcquaintances().heartBeat();
                logger.debug("Heart-beat sent");
            }

            // How many peers ?
            if (this.getEstimatedNumberOfAcquaintances() < NOA) {
                // Looking for new peers
                logger.debug("NOA is " + NOA +
                    " - Size of P2PAcquaintanceManager is " +
                    this.getEstimatedNumberOfAcquaintances() +
                    " looking for new acquaintances through prefered ones");

                this.connectToPreferedAcquaintances();
            }

            // How many peers ?
            if (this.getEstimatedNumberOfAcquaintances() < NOA) {
                // Looking for new peers
                logger.debug("NOA is " + NOA +
                    " - Size of P2PAcquaintanceManager is " +
                    this.getEstimatedNumberOfAcquaintances() +
                    " looking for new acquaintances through exploration");

                // Sending exploring message
                //          System.out.println(">>>>>>>>>>>>>>>>> P2PAcquaintanceManager.runActivity()");
                //this.acquaintances.exploring(new ExplorationMessage(TTL,UniversalUniqueID.randomUUID(),this.localService));
                this.localService.explore();
                logger.debug("Explorating message sent");
            }

            if (this.acquaintances.size() > NOA) {
                //we should drop some here
                //do we go for all at once or just one at a time?
                logger.info("I have too many neighbors!");
            }
            //}

            waitTTU(service);
        }
    }

	protected void waitTTU(Service service) {
		// Waiting TTU & serving requests
		logger.debug("Waiting for " + P2PService.TTU + "ms");
		long endTime = System.currentTimeMillis() + P2PService.TTU;
		service.blockingServeOldest(P2PService.TTU);
		while (System.currentTimeMillis() < endTime) {
		    try {
		        service.blockingServeOldest(endTime -
		            System.currentTimeMillis());
		    } catch (ProActiveRuntimeException e) {
		        e.printStackTrace();
		        logger.debug("Certainly because the body is not active", e);
		    }
		}
		logger.debug("End waiting");
	}

    public void connectToPreferedAcquaintances() {
        int size = this.peers.size();
        int index = 0;

        //while(!this.peers.isEmpty()) {
        //     for (int i = 0; i < size; i++) {
      
        
        
        while ((index < size) && (this.getEstimatedNumberOfAcquaintances() < NOA)) {
            String peerUrl = urlAdderP2PNodeName((String) this.peers.remove(0));
            try {
                Node distNode = NodeFactory.getNode(peerUrl);
                P2PService peer = (P2PService) distNode.getActiveObjects(P2PService.class.getName())[0];
                if (!peer.equals(this.localService) &&
                        !this.contains(peer).booleanValue()) {
                    // Send a message to the remote peer to register myself
                    logger.info("P2PAcquaintanceManager.connectingPeer() ----" +
                         peerUrl);
                     peer.registerRequest(this.localService);
                     awaitedReplies.put(peerUrl, peer);
                     //to avoid deadlocks we don't check wether the answer is positive or not
                    
//                    //a null reply means we have been accepted
//                    if (url.length == 0) {
//                        logger.info(peerUrl +
//                            " has accepted to be our new acquaintance ");
//
//                        // Add the peer in my group of acquaintances
//                        this.add(peer);
//                    } else {
//                        logger.info(peerUrl +
//                            " refused the acquaintance request, maybe the peer is full ");
//
//                        for (int j = 0; j < url.length; j++) {
//                            logger.info("Adding " + url[j] +
//                                " as new candidate");
//                            this.peers.add(url[j]);
//                        }
//                        //put it back for later use
//                        this.peers.add(peerUrl);
//                    }
                }
            } catch (Exception e) {
                logger.info("The peer at " + peerUrl +
                    " couldn't be contacted", e);
                //put it back for later use
                this.peers.add(peerUrl);
            }
            index++;
        }
        if (this.size().intValue() == 0) {
            logger.info("No peer could be found to join the network");
        }
    }

    /**
     * @return An active object to make group method call.
     */
    public P2PService getActiveGroup() {
        return this.acquaintancesActived;
    }

    /**
     * Add a peer in the group of acquaintances
     * Add only if not already present and still some space left (NOA)
     * @param peer the peer to add.
     * @return add succesfull
     */
    public Vector<String> add(P2PService peer) {
         return this.add(ProActive.getActiveObjectNodeUrl(peer),peer);
    }
    public Vector<String> add(String peerUrl, P2PService peer) {
        boolean result = false;
        try {
            if ((this.acquaintances.size() < NOA) &&
                    !this.acquaintances.contains(peer)) {

                if (!peerUrl.matches(".*cannot contact the body.*")) {
                    result = this.acquaintances.add(peer, peerUrl);
                    logger.info("Acquaintance " + peerUrl + " " + result +
                        " added");
                }
                return new Vector<String>();
            }
        } catch (Exception e) {
            this.acquaintances.remove(peer);
            logger.debug("Problem when adding peer", e);
        }
        return this.getAcquaintancesURLs();
    }
    
    public void addFromReply(String url, P2PService peer) {
    	System.out.println("P2PAcquaintanceManager.addFromReply() got a reply from " + url);
    	this.add(url,peer);
    	//and we remove it from the awaited answers
    	this.removeFromAwaited(url);
    }
    
    
    public void removeFromReply(String url, Vector<String> s) {
    	System.out.println("P2PAcquaintanceManager.removeFromReply() " + url);
    	//this.removeFromReply(url,s);
    	this.removeFromAwaited(url);
    }
    
    public void removeFromAwaited(String url) {
    	System.out.println("Removing " + url + " from awaited peers");
    	awaitedReplies.remove(url);
    }
    
    public void remove(P2PService peer) {
        boolean result = this.acquaintances.remove(peer);
        if (result) {
            logger.info("Peer successfully removed");
        } else {
            logger.debug("Peer not removed");
        }
    }

    public void dumpAcquaintances() {
        //    	Iterator it = urlList.iterator();
        //    	logger.info("***********************");
        //     while (it.hasNext()) {
        //    	 logger.info(it.next());
        //     }
        // 	logger.info("***********************");
        acquaintances.dumpAcquaintances();
    }

    public Vector<String> getAcquaintancesURLs() {
    
    	return new Vector<String>(Arrays.asList(this.acquaintances.getAcquaintancesURLs()));
//        return new Vector(this.acquaintances.getAcquaintancesURLs());
    }

    /**
     * Returns the number of elements in this group.
     *
     * @return the number of elements in this group.
     */
    public IntMutableWrapper size() {
        return new IntMutableWrapper(this.acquaintances.size());
    }

    public int getEstimatedNumberOfAcquaintances() {
    	return this.acquaintances.size() + awaitedReplies.size(); 
    }
    
    
    /**
     * Returns <tt>true</tt> if this collection contains the specified
     * element.  More formally, returns <tt>true</tt> if and only if this
     * collection contains at least one element <tt>e</tt> such that
     * <tt>(o==null ? e==null : o.equals(e))</tt>.
     *
     * @param service element whose presence in this collection is to be tested.
     * @return <tt>true</tt> if this collection contains the specified
     *         element.
     */
    public BooleanMutableWrapper contains(P2PService service) {
        return new BooleanMutableWrapper(this.acquaintances.contains(service));
    }

    private Random randomizer = new Random();

    /**
     * @return a random acquaintance reference.
     */
    public P2PService randomPeer() {
        int random = this.randomizer.nextInt(this.acquaintances.size());
        return (P2PService) this.acquaintances.get(random);
    }

    /**
     * @return the list of current acquaintances.
     */
    public Vector getAcquaintanceList() {
        return new Vector(this.acquaintances.getAcquaintancesAsGroup());
    }

    public P2PService getAcquaintances() {
        return this.acquaintances.getAcquaintances();
    }

    /**
     * Calls the transmit() method of the message m
     * @param m
     */
    public void transmit(Message m) {
        m.transmit(this.acquaintances.getAcquaintances());
    }

    public int getMaxNOA() {
        return NOA;
    }

    public boolean shouldBeAcquaintance(P2PService remoteService) {
        if (this.contains(remoteService).booleanValue()) {
            logger.debug("The remote peer is already known");
            return false;
        }
        if (this.acquaintances.size() < NOA) {
            logger.debug("NOA not reached: I should be an acquaintance");
            return true;
        }
        //     int random = randomizer.nextInt(100);
        //     if (random < EXPL_MSG) {
        //        logger.debug("Random said: I should be an acquaintance");
        //       return true;
        //  }
        logger.debug("Random said: I should not be an acquaintance");
        return false;
    }

    public void setPreferedAcq(Vector<String> v) {
        this.peers = v;
    }

    /**
     * Add the given peer urls to the current 
     * prefered acquaintances list
     * @param v the list of acquaintances
     */
    public void addToPreferedAcq(Vector<String> v) {
    	this.peers.addAll(v);
    }
    
    /**
      * Add the default name of the P2P Node to a specified <code>url</code>.
      * @param url  the url.
      * @return the <code>url</code> with the name of the P2P Node.
      */
    private static String urlAdderP2PNodeName(String url) {
    	if (url.indexOf(P2P_NODE_NAME)<=0) {
    		url += ("/" + P2P_NODE_NAME);
    	}
        return url;
    }
}
