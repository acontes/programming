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

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.ProActiveInternalObject;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.core.Constants;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestFilter;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;
import org.objectweb.proactive.p2p.service.exception.P2POldMessageException;
import org.objectweb.proactive.p2p.service.messages.DumpAcquaintancesMessage;
import org.objectweb.proactive.p2p.service.messages.ExplorationMessage;
import org.objectweb.proactive.p2p.service.messages.Message;
import org.objectweb.proactive.p2p.service.node.P2PNode;
import org.objectweb.proactive.p2p.service.node.P2PNodeLookup;
import org.objectweb.proactive.p2p.service.node.P2PNodeManager;
import org.objectweb.proactive.p2p.service.util.P2PConstants;
import org.objectweb.proactive.p2p.service.util.UniversalUniqueID;


/**
 * <p>ProActive Peer-to-Peer Service.</p>
 * <p>This class is made to be actived.</p>
 *
 * @author Alexandre di Costanzo
 *
 */
public class P2PService implements InitActive, P2PConstants, Serializable,
    ProActiveInternalObject {

    /** Logger. */
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.P2P_SERVICE);

    /** ProActive Group of acquaintances. **/
 //   private P2PService acquaintances;

    /**
     * ProActive Group representing <code>acquaintances</code>.
     */
    public P2PAcquaintanceManager acquaintanceManager;

    /**
     * Reference to the current Node.
     */
    private Node p2pServiceNode = null;
    private static final int MSG_MEMORY = Integer.parseInt(System.getProperty(
                P2PConstants.PROPERTY_MSG_MEMORY));
//    private static final int NOA = Integer.parseInt(System.getProperty(
//                P2PConstants.PROPERTY_NOA));
    private static final int EXPL_MSG = Integer.parseInt(System.getProperty(
                P2PConstants.PROPERTY_EXPLORING_MSG)) - 1;
    static public final long ACQ_TO = Long.parseLong(System.getProperty(
                P2PConstants.PROPERTY_NODES_ACQUISITION_T0));


     static final long TTU = Long.parseLong(System.getProperty(
            P2PConstants.PROPERTY_TTU));
//static public final int NOA = Integer.parseInt(System.getProperty(
//            P2PConstants.PROPERTY_NOA));
 static final int TTL = Integer.parseInt(System.getProperty(
            P2PConstants.PROPERTY_TTL));
    
    /**
     * Randomizer uses in <code>shouldBeAcquaintance</code> method.
     */
    private static final Random randomizer = new Random();

    /**
     * Sequence number list of received messages.
     */
    private Vector<UniversalUniqueID> oldMessageList = new Vector<UniversalUniqueID>(MSG_MEMORY);
    public P2PNodeManager nodeManager = null;

    /**
     * A collection of not full <code>P2PNodeLookup</code>.
     */
    private Vector<P2PNodeLookup> waitingNodesLookup = new Vector<P2PNodeLookup>();
    private Vector<P2PNodeLookup> waitingMaximunNodesLookup = new Vector<P2PNodeLookup>();
    public P2PService stubOnThis = null;

    // For asking nodes
    public Service service = null;
    public RequestFilter filter = new RequestFilter() {

            /**
             * @see org.objectweb.proactive.core.body.request.RequestFilter#acceptRequest(org.objectweb.proactive.core.body.request.Request)
             */
            public boolean acceptRequest(Request request) {
                String requestName = request.getMethodName();
                if (requestName.compareToIgnoreCase("askingNode") == 0) {
                    return false;
                }
                return true;
            }
        };

    //--------------------------------------------------------------------------
    // Class Constructors
    //--------------------------------------------------------------------------

    /**
     * The empty constructor.
     *
     * @see org.objectweb.proactive.ProActive
     */
    public P2PService() {
        // empty
    }

    //--------------------------------------------------------------------------
    // Public Class methods
    // -------------------------------------------------------------------------

    /**
     * Contact all specified peers to enter in the existing P2P network.
     * @param peers a list of peers URL.
     */
    public void firstContact(Vector peers) {
        // Creating an active P2PFirstContact
//        Object[] params = new Object[3];
//        params[0] = peers;
//        params[1] = this.acquaintanceManager;
//        params[2] = this.stubOnThis;
//        try {
//            ProActive.newActive(P2PFirstContact.class.getName(), params,
//                this.p2pServiceNode);
//        } catch (ActiveObjectCreationException e) {
//            logger.warn("Couldn't active P2PFirstContact", e);
//        } catch (NodeException e) {
//            logger.warn("Couldn't active P2PFirstContact", e);
//        }
    	this.acquaintanceManager.setPreferedAcq(peers);
    }

    /**
     * Add the remote P2P service in the local acquaintances group if NOA is
     * not yet reached.
     * @param service the remote P2P service.
     */
//    public String[] register(P2PService service) {
//        try {
//            if (!this.stubOnThis.equals(service)) {
//         //       if (this.acquaintanceManager.size().intValue() < this.acquaintanceManager.getMaxNOA()) {
//                if (this.acquaintanceManager.add(service)) {
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("Remote peer localy registered: " +
//                            ProActive.getActiveObjectNodeUrl(service));
//                    }
//                    System.out.println("P2PService.register() we have a new peer: " + ProActive.getActiveObjectNodeUrl(service));
//                    // Wake up all node accessor, because new peers are know
//                    this.wakeUpEveryBody();
//                    return null;
//                }
//            }
//        } catch (Exception e) {
//        	e.printStackTrace();
//            logger.debug("The remote P2P service is certainly down", e);
//        }
//        return this.acquaintanceManager.getAcquaintancesURLs();
//    }

    public Vector register(P2PService service) {
//        try {
            if (!this.stubOnThis.equals(service)) {
         //       if (this.acquaintanceManager.size().intValue() < this.acquaintanceManager.getMaxNOA()) {
              return this.acquaintanceManager.add(service);
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("Remote peer localy registered: " +
//                            ProActive.getActiveObjectNodeUrl(service));
//                    }
//                    System.out.println("P2PService.register() we have a new peer: " + ProActive.getActiveObjectNodeUrl(service));
                    // Wake up all node accessor, because new peers are know
               //     this.wakeUpEveryBody();
//                    return null;
//                }
//            }
//        } catch (Exception e) {
//        	e.printStackTrace();
//            logger.debug("The remote P2P service is certainly down", e);
//        }
//        return this.acquaintanceManager.getAcquaintancesURLs();
            }
            return new Vector();
    }
    
    
    
    /**
     * Just to test if the peer is alive.
     */
    public void heartBeat() {
        logger.debug("Heart-beat message received");
    }

    public void  dumpAcquaintances() {
    	DumpAcquaintancesMessage m = new DumpAcquaintancesMessage(10,this.generateUuid(), this.stubOnThis);
    this.dumpAcquaintances(m);
    }
    
    public void dumpAcquaintances(Message m) {
    	m.setSender(this.stubOnThis);
    	//execute locally
    	m.execute(this);
    	//start the flooding
    	m.transmit(this.acquaintanceManager.getAcquaintances());
    }
    
    /**
     * Start the exploration process
     * Build an exploration message and send it to the current acquaintances
     *
     */
    public void explore() {
    	ExplorationMessage m = new ExplorationMessage(10,this.generateUuid(),this.stubOnThis);
    	//m.transmit(this.acquaintanceManager.getAcquaintances());
    	this.acquaintanceManager.transmit(m);
    }
    
    public void requestNodes(Message m) {
    	m.execute(this);
    	//m.transmit(this.acquaintanceManager.getAcquaintances());
    	this.acquaintanceManager.transmit(m);
    }
    
    public void message(Message message) {
    	  UniversalUniqueID uuid = message.getUuid();
    	  int ttl = message.getTTL();
          P2PService remoteService = message.getSender();
          if (uuid != null) {
              logger.debug("Message " + message + "  received with #" + uuid);
              ttl--;
              message.setTTL(ttl);
          }

          boolean broadcast;
          try {
              broadcast = broadcaster(ttl, uuid, remoteService);
          } catch (P2POldMessageException e) {
        	  logger.debug("P2PService.message() received an old message");
              return;
          }
          message.execute(this);
    //      System.out.println("P2PService.message() broadcast " + broadcast);
          if (broadcast) {
        	//  message.transmit(this.acquaintanceManager.getAcquaintances());
        	  this.acquaintanceManager.transmit(message);
          }
    }
    

    /**
     * <b>Method called on Load Balanced enviroments.</b>
     * <p>Booking a free node.</p>
     * @param ttl Time to live of the message, in number of hops.
     * @param uuid UUID of the message.
     * @param remoteService The original sender.
     * @param numberOfNodes Number of asked nodes.
     * @param lookup The P2P nodes lookup.
     * @param vnName Virtual node name.
     * @param jobId
     * @param underloadedOnly determines if it replies with normal "askingNode" method or discard the call
     */
//    public void askingNode(int ttl, UniversalUniqueID uuid,
//        P2PService remoteService, int numberOfNodes, P2PNodeLookup lookup,
//        String vnName, String jobId, boolean underloadedOnly) {
//        if (!underloadedOnly || !amIUnderloaded(0)) {
//            return;
//        }
//
//        this.askingNode(ttl, uuid, remoteService, numberOfNodes, lookup,
//            vnName, jobId, null);
//    }

//    public void askingNode(Message message) {
//    	 UniversalUniqueID uuid = message.getUuid();
//   	     int ttl = message.getTTL();
//         P2PService remoteService = message.getSender();
//         boolean broadcast;
//         
//         if (uuid != null) {
//             logger.debug("AskingNode message received with #" + uuid);
//             ttl--;
//             try {
//                 broadcast = broadcaster(ttl, uuid, remoteService);
//             } catch (P2POldMessageException e) {
//                 return;
//             }
//         } else {
//        	 message.setUuid(this.generateUuid());
//             broadcast = true;
//         }
//         message.execute(this);
//         if (broadcast) {
//        	 message.transmit(this.acquaintances);
//         }
//    }
    
    /** Put in a <code>P2PNodeLookup</code>, the number of asked nodes.
     * @param numberOfNodes the number of asked nodes.
     * @param nodeFamilyRegexp the regexp for the famili, null or empty String for all.
     * @param vnName Virtual node name.
     * @param jobId of the vn.
     * @return the number of asked nodes.
     */
    public P2PNodeLookup getNodes(int numberOfNodes, String nodeFamilyRegexp,
        String vnName, String jobId) {
        Object[] params = new Object[5];
        params[0] = new Integer(numberOfNodes);
        params[1] = this.stubOnThis;
        params[2] = vnName;
        params[3] = jobId;
        params[4] = nodeFamilyRegexp;

        P2PNodeLookup lookup = null;
        try {
            lookup = (P2PNodeLookup) ProActive.newActive(P2PNodeLookup.class.getName(),
                    params, this.p2pServiceNode);
            ProActive.enableAC(lookup);
            if (numberOfNodes == MAX_NODE) {
                this.waitingMaximunNodesLookup.add(lookup);
            } else {
                this.waitingNodesLookup.add(lookup);
            }
        } catch (ActiveObjectCreationException e) {
            logger.fatal("Couldn't create an active lookup", e);
            return null;
        } catch (NodeException e) {
            logger.fatal("Couldn't connect node to creat", e);
            return null;
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Couldn't enable AC for a nodes lookup", e);
            }
        }

        if (logger.isInfoEnabled()) {
            if (numberOfNodes != MAX_NODE) {
                logger.info("Asking for " + numberOfNodes + " nodes");
            } else {
                logger.info("Asking for maxinum nodes");
            }
        }
        return lookup;
    }

    /** Put in a <code>P2PNodeLookup</code>, the number of asked nodes.
     * @param numberOfNodes the number of asked nodes.
     * @param vnName Virtual node name.
     * @param jobId of the vn.
     * @return the number of asked nodes.
     */
    public P2PNodeLookup getNodes(int numberOfNodes, String vnName, String jobId) {
        return this.getNodes(numberOfNodes, ".*", vnName, jobId);
    }

    /**
     * For asking a single node to the p2p infrastructure.
     * There no warranties that a node will be returned.
     * @param vnName the virtual node name.
     * @param jobId the job ID.
     * @return a free node.
     */
//    public Node getANode(String vnName, String jobId) {
//        return this.stubOnThis.getANode(vnName, jobId, this.stubOnThis);
//    }

    /**
     * <b>***For internal use only***
     * @param vnName the virtual node name.
     * @param jobId the job ID.
     * @param service a stub on the requester
     * @return a free node.
     */
//    public Node getANode(String vnName, String jobId, P2PService service) {
//        if (service.equals(this.stubOnThis)) {
//            return this.acquaintanceManager.randomPeer().getANode(vnName,
//                jobId, service);
//        }
//        P2PNode askedNode = this.nodeManager.askingNode(null);
//        Node nodeAvailable = askedNode.getNode();
//
//        if (nodeAvailable != null) {
//            if (vnName != null) {
//                try {
//                    nodeAvailable.getProActiveRuntime().registerVirtualNode(vnName,
//                        true);
//                } catch (Exception e) {
//                    logger.warn("Couldn't register " + vnName + " in the PAR", e);
//                }
//            }
//            if (jobId != null) {
//                nodeAvailable.getNodeInformation().setJobID(jobId);
//            }
//            return nodeAvailable;
//        }
//        return this.acquaintanceManager.randomPeer().getANode(vnName, jobId,
//            service);
//    }
    
//    public void getANode(RequestSingleNodeMessage m) {
//    	m.execute(this);
//    	m.transmit(this.acquaintances);
//    }

    /**
     * Put in a <code>P2PNodeLookup</code> all available nodes during all the
     * time where it is actived.
     * @param vnName Virtual node name.
     * @param jobId
     * @return an active object where nodes are received.
     */
    public P2PNodeLookup getMaximunNodes(String vnName, String jobId) {
        return this.getNodes(P2PConstants.MAX_NODE, vnName, jobId);
    }

    /**
     * For load balancing.
     * @return URL of the node where the P2P service is running.
     */
    public StringWrapper getAddress() {
        return new StringWrapper(this.p2pServiceNode.getNodeInformation()
                                                    .getURL());
    }

    /**
       /**
     * Remove a no more waiting nodes accessor.
     * @param accessorToRemove the accessor to remove.
     */
    public void removeWaitingAccessor(P2PNodeLookup accessorToRemove) {
        this.waitingNodesLookup.remove(accessorToRemove);
        logger.debug("Accessor succefuly removed");
    }

    /**
     * @return the list of current acquaintances.
     */
    public Vector getAcquaintanceList() {
        return this.acquaintanceManager.getAcquaintanceList();
    }
    
    public P2PAcquaintanceManager getAcquaintanceManager () {
    	return this.acquaintanceManager;
    }
    
    public P2PService randomPeer() {
    	return this.acquaintanceManager.randomPeer();
    }
    // -------------------------------------------------------------------------
    // Private class method
    // -------------------------------------------------------------------------

    /**
     * <b>* ONLY FOR INTERNAL USE *</b>
     * Generates a UUID and mark it as already received
     * @return a random UUID for sending message.
     */
    private UniversalUniqueID generateUuid() {
        UniversalUniqueID uuid = UniversalUniqueID.randomUUID();
        oldMessageList.add(uuid);
        logger.debug(" UUID generated with #" + uuid);
        return uuid;
    }

    /**
     * If not an old message and ttl > 1 return true else false.
     * @param ttl TTL of the message.
     * @param uuid UUID of the message.
     * @param remoteService P2PService of the first service.
     * @return true if you should broadcats, false else.
     */
    private boolean broadcaster(int ttl, UniversalUniqueID uuid,
        P2PService remoteService) throws P2POldMessageException {
        // is it an old message?
        boolean isAnOldMessage = this.isAnOldMessage(uuid);

        String remoteNodeUrl = null;
        try {
            remoteNodeUrl = ProActive.getActiveObjectNodeUrl(remoteService);
        } catch (Exception e) {
            isAnOldMessage = true;
        }
        String thisNodeUrl = this.p2pServiceNode.getNodeInformation().getURL();

        if (!isAnOldMessage && !remoteNodeUrl.equals(thisNodeUrl)) {
            if (ttl > 0) {
                logger.debug("Forwarding message request");
                return true;
            }
            return false;
        }

        // it is an old message: nothing to do
        // NO REMOVE the isDebugEnabled message
        if (logger.isDebugEnabled()) {
            if (isAnOldMessage) {
                logger.debug("Old message request with #" + uuid);
            } else {
                logger.debug("The peer is me: " + remoteNodeUrl);
            }
        }

        throw new P2POldMessageException();
    }

    /**
     * If number of acquaintances is less than NOA return <code>true</code>, else
     * use random factor.
     * @param remoteService the remote service which is asking acquaintance.
     * @return <code>true</code> if this peer should be an acquaintance, else
     * <code>false</code>.
     */
    public boolean shouldBeAcquaintance(P2PService remoteService) {
       return this.acquaintanceManager.shouldBeAcquaintance(remoteService);
    }

    /**
     * If ti's not an old message add the sequence number in the list.
     * @param uuid the uuid of the message.
     * @return <code>true</code> if it was an old message, <code>false</code> else.
     */
    private boolean isAnOldMessage(UniversalUniqueID uuid) {
        if (uuid == null) {
            return false;
        }
        if (oldMessageList.contains(uuid)) {
            return true;
        }
        if (oldMessageList.size() == MSG_MEMORY) {
            oldMessageList.remove(0);
        }
        oldMessageList.add(uuid);
        return false;
    }

    /**
     * Wake up all node lookups.
     */
    private void wakeUpEveryBody() {
        for (int i = 0; i < this.waitingNodesLookup.size(); i++) {
            ((P2PNodeLookup) this.waitingNodesLookup.get(i)).wakeUp();
        }
    }

    //--------------------------------------------------------------------------
    // Active Object methods
    //--------------------------------------------------------------------------

    /**
     * @see org.objectweb.proactive.InitActive#initActivity(org.objectweb.proactive.Body)
     */
    public void initActivity(Body body) {
        logger.debug("Entering initActivity");

        this.service = new Service(body);

        try {
            // Reference to my current p2pServiceNode
            this.p2pServiceNode = NodeFactory.getNode(body.getNodeURL());
        } catch (NodeException e) {
            logger.fatal("Couldn't get reference to the local p2pServiceNode", e);
        }

        logger.debug("P2P Service running in p2pServiceNode: " +
            this.p2pServiceNode.getNodeInformation().getURL());

        this.stubOnThis = (P2PService) ProActive.getStubOnThis();

        Object[] params = new Object[1];
        params[0] = this.stubOnThis;
        try {
            // Active acquaintances
            this.acquaintanceManager = (P2PAcquaintanceManager) ProActive.newActive(P2PAcquaintanceManager.class.getName(),
                    params, this.p2pServiceNode);
            logger.debug("P2P acquaintance manager activated");

            // Get active group
    //        this.acquaintances = this.acquaintanceManager.getActiveGroup();
            logger.debug("Got active group reference");

            // Active Node Manager
            this.nodeManager = (P2PNodeManager) ProActive.newActive(P2PNodeManager.class.getName(),
                    null, this.p2pServiceNode);
            logger.debug("P2P node manager activated");
        } catch (ActiveObjectCreationException e) {
            logger.fatal("Couldn't create one of managers", e);
        } catch (NodeException e) {
            logger.fatal("Couldn't create one the managers", e);
        }
        logger.debug("Exiting initActivity");
    }

  

    public static P2PService getLocalP2PService() throws Exception {
        UniversalBody body = (UniversalBody) ProActiveRuntimeImpl.getProActiveRuntime()
                                                                 .getActiveObjects(P2P_NODE_NAME,
                P2PService.class.getName()).get(0);
        return (P2PService) MOP.newInstance(P2PService.class.getName(),
            (Object[]) null, Constants.DEFAULT_BODY_PROXY_CLASS_NAME,
            new Object[] { body });
    }

    /**
     * Ask to the Load Balancer object if the state is underloaded
     * @param ranking
     * @return <code>true</code> if the state is underloaded, <code>false</code> else.
     */
    public boolean amIUnderloaded(double ranking) {
//        if (ranking >= 0) {
//            return p2pLoadBalancer.AreYouUnderloaded(ranking);
//        }
//        return p2pLoadBalancer.AreYouUnderloaded();
    	//TEST FAb
    	return true;
    }
}
