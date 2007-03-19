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
package org.objectweb.proactive.ic2d.data;

import java.rmi.AlreadyBoundException;
import java.rmi.dgc.VMID;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.ic2d.event.CommunicationEventListener;
import org.objectweb.proactive.ic2d.event.SpyEventListener;
import org.objectweb.proactive.ic2d.event.VMObjectListener;
import org.objectweb.proactive.ic2d.spy.Spy;
import org.objectweb.proactive.ic2d.spy.SpyEvent;
import org.objectweb.proactive.ic2d.spy.SpyMessageEvent;
import org.objectweb.proactive.ic2d.util.MonitorThread;


/**
 * Holder class for the host data representation
 */
public class VMObject extends AbstractDataObject {
    static Logger log4jlogger = ProActiveLogger.getLogger(Loggers.IC2D);
    private static String SPY_LISTENER_NODE_NAME = "SpyListenerNode";
    private static Node SPY_LISTENER_NODE;
    private static int NOT_RESPONDING_MAX_TRIES = 3; // actually not used

    static {
        String currentHost;

        try {
            currentHost = UrlBuilder.getHostNameorIP(java.net.InetAddress.getLocalHost());
        } catch (java.net.UnknownHostException e) {
            currentHost = "localhost";
        }

        //System.out.println("current host: "+currentHost);
        try {
            SPY_LISTENER_NODE = NodeFactory.createNode(UrlBuilder.buildUrlFromProperties(
                        currentHost, SPY_LISTENER_NODE_NAME), true, null, null);
        } catch (NodeException e) {
            SPY_LISTENER_NODE = null;
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }

    private int notRespondingCounter = 0;
    private long firstNotRespondingTime = 0;
    private long lastNotRespondingTime = -1;
    protected Spy spy;
    protected VMID vmid;
    protected String protocolId;
    protected java.util.HashMap objectNodeMap;
    protected SpyListenerImpl activeSpyListener;
    protected VMObjectListener listener;

    //this node will be used to kill the vm
    protected Node baseNode;

    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    public VMObject(HostObject host, VMID vmid, Node node, String protocolId)
        throws ActiveObjectCreationException, NodeException {
        super(host);

        //System.out.println("nodeURL : "+node.getNodeInformation().getURL());
        if (log4jlogger.isDebugEnabled()) {
            log4jlogger.debug("VMObject.<init>");
        }

        this.vmid = vmid;
        this.protocolId = protocolId;
        this.objectNodeMap = new java.util.HashMap();
        this.baseNode = node;

        SpyListenerImpl spyListener = new SpyListenerImpl(new MySpyEventListener());

        if (log4jlogger.isDebugEnabled()) {
            log4jlogger.debug("VMObject.<init> creating activeSpyListener");
        }

        this.activeSpyListener = (SpyListenerImpl) ProActive.turnActive(spyListener,
                SPY_LISTENER_NODE);

        if (log4jlogger.isDebugEnabled()) {
            log4jlogger.debug("VMObject.<init> creating spy");
        }

        this.spy = (Spy) ProActive.newActive(Spy.class.getName(),
                new Object[] { activeSpyListener }, node);
        addNodeObject(node);
        controller.log("VMObject id=" + vmid + " created based on node " +
            node.getNodeInformation().getURL());

        notRespondingCounter = 0;
    }

    //
    // -- PUBLIC METHOD -----------------------------------------------
    //
    @Override
    public String toString() {
        return "VM id=" + vmid + "\n" + super.toString();
    }

    //
    // Event Listener
    //
    public void registerListener(VMObjectListener listener) {
        this.messageMonitoringListener = listener;
        this.listener = listener;

        // notify existing childs
        notifyListenerOfExistingChilds();
        sendEventsForAllActiveObjects();
    }

    //
    // Accessor methods
    //
    public void migrateTo(UniqueID objectID, String nodeTargetURL)
        throws MigrationException {
        try {
            spy.migrateTo(objectID, nodeTargetURL);
        } catch (MigrationException e) {
            throw e;
        } catch (Exception e) {
            recoverExceptionInSpy(e);
            throw new MigrationException("Problem contacting the Spy", e);
        }
    }

    public VMID getID() {
        return vmid;
    }

    public String getProtocolId() {
        return this.protocolId;
    }

    public int getActiveObjectsCount() {
        return objectNodeMap.size();
    }

    public String getSystemProperty(String key) {
        try {
            return spy.getSystemProperty(key);
        } catch (Exception e) {
            recoverExceptionInSpy(e);

            return "! Error occured";
        }
    }

    public long getUpdateFrequence() {
        try {
            return spy.getUpdateFrequence();
        } catch (Exception e) {
            recoverExceptionInSpy(e);

            return 0;
        }
    }

    public void setUpdateFrequence(long updateFrequence) {
        try {
            spy.setUpdateFrequence(updateFrequence);
        } catch (Exception e) {
            recoverExceptionInSpy(e);
        }
    }

    public void sendEventsForAllActiveObjects() {
        if (log4jlogger.isDebugEnabled()) {
            log4jlogger.debug("VMObject.sendEventForAllActiveObjects()");
        }

        try {
            spy.sendEventsForAllActiveObjects();
        } catch (Exception e) {
            recoverExceptionInSpy(e);
        }
    }

    //
    // Node related methods
    //
    public NodeObject addNodeObject(Node node) {
        if (log4jlogger.isDebugEnabled()) {
            log4jlogger.debug("VMObject: addNodeObject()");
        }

        String nodeName = node.getNodeInformation().getName();
        NodeObject nodeObject = (NodeObject) getChild(nodeName);

        if (nodeObject == null) {
            nodeObject = new NodeObject(this, node);
            putChild(nodeName, nodeObject);

            if (listener != null) {
                listener.nodeObjectAdded(nodeObject);
            }

            sendEventsForAllActiveObjects();
        }

        return nodeObject;
    }

    public NodeObject getNodeObject(String nodeName) {
        return (NodeObject) getChild(nodeName);
    }

    public NodeObject getNodeObject(UniqueID bodyID) {
        return (NodeObject) objectNodeMap.get(bodyID);
    }

    public void removeNodeObject(String nodeName) {
        // remove the node
        NodeObject nodeObject = (NodeObject) removeChild(nodeName);

        if (nodeObject == null) {
            controller.log("The node " + nodeName +
                " does not exist. Cannot remove it");
        } else {
            if (listener != null) {
                listener.nodeObjectRemoved(nodeObject);
            }
        }
    }

    public String getVMUrl() {
        return baseNode.getProActiveRuntime().getURL();
    }

    @Override
    public void destroyObject() {
        getTypedParent().removeVMObject(vmid);
    }

    public void killVM() {
        ProActiveRuntime part = null;

        try {
            part = baseNode.getProActiveRuntime();
            part.killRT(false);
        } catch (Exception e) {
            controller.log(" Virtual Machine " +
                part.getVMInformation().getVMID() + " on host " +
                UrlBuilder.getHostNameorIP(part.getVMInformation()
                                               .getInetAddress()) +
                " terminated!!!");
        }

        getTypedParent().removeVMObject(vmid);
    }

    public int getNotRespondingCounter() {
        return notRespondingCounter;
    }

    public long getFirstNotRespondingTime() {
        return firstNotRespondingTime;
    }

    //
    // -- PROTECTED METHOD -----------------------------------------------
    //
    protected void registerActiveObject(UniqueID id, NodeObject nodeObject) {
        objectNodeMap.put(id, nodeObject);
    }

    protected void unregisterActiveObject(UniqueID id) {
        objectNodeMap.remove(id);
    }

    @Override
    protected synchronized boolean destroy() {
        if (super.destroy()) {
            try {
                spy.terminate();
            } catch (Exception e) {
            }

            activeSpyListener.terminate();
            objectNodeMap.clear();
            spy = null;
            activeSpyListener = null;
            listener = null;

            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void monitoringMessageEventChanged(ActiveObject object,
        boolean value) {
        try {
            if (value) {
                spy.addMessageEventListener(object.getID());
            } else {
                spy.removeMessageEventListener(object.getID());
            }

            super.monitoringMessageEventChanged(object, value);
        } catch (Exception e) {
            recoverExceptionInSpy(e);
        }
    }

    protected HostObject getTypedParent() {
        return (HostObject) parent;
    }

    //
    // -- PRIVATE METHOD -----------------------------------------------
    //
    private void recoverExceptionInSpy(Exception e) {
        // *******************************************
        //controller.log("Exception occured while contacting Spy for VM " + vmid +
        //    ". Now removing the VM from IC2D.", e);
        controller.log("VM " + vmid + " is not responding ...");

        if (listener != null) {
            listener.vmNotResponding();
        }

        if (lastNotRespondingTime < 0) {
            firstNotRespondingTime = System.currentTimeMillis();
        }

        if ((System.currentTimeMillis() - lastNotRespondingTime) > (MonitorThread.getTtr() * 1000)) {
            notRespondingCounter++;
            lastNotRespondingTime = System.currentTimeMillis();

            //System.out.println("Spy lost #" + notRespondingCounter) ;
        }

        /*
           if (notRespondingCounter == NOT_RESPONDING_MAX_TRIES ){
                   controller.log("Now removing the VM from IC2D") ;
                   System.out.println ("remove " + vmid + " VM") ;
                   //getTypedParent().removeVMObject(vmid) ;
                   killVM() ;
                   //destroyObject();
                   destroy() ;
                   lastNotRespondingTime = -1 ;
           }
         */

        //****************************************
    }

    private ActiveObject findActiveObject(UniqueID id) {
        NodeObject nodeObject = getNodeObject(id);

        if (nodeObject == null) {
            controller.log("!! Event received for an unknown node, id=" + id);

            return null; // unknown node
        }

        ActiveObject ao = nodeObject.getActiveObject(id);

        if (ao == null) {
            controller.log(
                "!! Event received for an unknown active object, id=" + id);
        }

        return ao;
    }

    private synchronized void notifyListenerOfExistingChilds() {
        if (getChildObjectsCount() == 0) {
            return;
        }

        java.util.Iterator iterator = childsIterator();

        while (iterator.hasNext()) {
            NodeObject nodeObject = (NodeObject) iterator.next();
            listener.nodeObjectAdded(nodeObject);
        }
    }

    //  private String getNodeNameFromURL(String nodeURL) {
    //    int n = nodeURL.indexOf('/', 2); // looking for the end of the host
    //    if (n < 3) return nodeURL;
    //    return nodeURL.substring(n+1);
    //  }
    //
    // -- INNER CLASSES -----------------------------------------------
    //
    private class MySpyEventListener implements SpyEventListener {
        private CommunicationEventListener communicationEventListener;

        public MySpyEventListener() {
            communicationEventListener = ((IC2DObject) getTopLevelParent()).getCommunicationEventListener();
        }

        //
        // -- Implement SpyEventListener -----------------------------------------------
        //
        public void activeObjectAdded(UniqueID id, String nodeURL,
            String classname, boolean isActive) {
            //String nodeName = getNodeNameFromURL(nodeURL);
            String nodeName = UrlBuilder.getNameFromUrl(nodeURL);

            //System.out.println("NodeName "+nodeName+" AO id "+id);
            NodeObject nodeObject = getNodeObject(nodeName);

            if (nodeObject != null) {
                nodeObject.addActiveObject(classname, id, isActive);
            }
        }

        public void activeObjectChanged(UniqueID id, boolean isActive,
            boolean isAlive) {
            ActiveObject object = findActiveObject(id);

            //System.out.println("activeObjectChanged object="+object.getName()+" isActive="+isActive);
            if (object == null) {
                return;
            }

            if (!isAlive) {
                object.destroyObject();
            }
        }

        public void objectWaitingForRequest(UniqueID id, SpyEvent spyEvent) {
            if (!controller.isMonitoring()) {
                return;
            }

            ActiveObject object = findActiveObject(id);

            if (object == null) {
                return;
            }

            object.setServingStatus(ActiveObject.STATUS_WAITING_FOR_REQUEST);
            object.setRequestQueueLength(0);
            communicationEventListener.objectWaitingForRequest(object, spyEvent);
        }

        public void objectWaitingByNecessity(UniqueID id, SpyEvent spyEvent) {
            if (!controller.isMonitoring()) {
                return;
            }

            ActiveObject object = findActiveObject(id);

            if (object == null) {
                return;
            }

            object.setServingStatus((object.getServingStatus() == ActiveObject.STATUS_SERVING_REQUEST)
                ? ActiveObject.STATUS_WAITING_BY_NECESSITY_WHILE_SERVING
                : ActiveObject.STATUS_WAITING_BY_NECESSITY_WHILE_ACTIVE);
            communicationEventListener.objectWaitingByNecessity(object, spyEvent);
        }

        public void objectReceivedFutureResult(UniqueID id, SpyEvent spyEvent) {
            if (!controller.isMonitoring()) {
                return;
            }

            ActiveObject object = findActiveObject(id);

            if (object == null) {
                return;
            }

            switch (object.getServingStatus()) {
            case ActiveObject.STATUS_WAITING_BY_NECESSITY_WHILE_SERVING:
                object.setServingStatus(ActiveObject.STATUS_SERVING_REQUEST);
                break;
            case ActiveObject.STATUS_WAITING_BY_NECESSITY_WHILE_ACTIVE:
                object.setServingStatus(ActiveObject.STATUS_ACTIVE);
                break;
            }
        }

        public void requestMessageSent(UniqueID id, SpyEvent spyEvent) {
            if (!controller.isMonitoring()) {
                return;
            }

            ActiveObject object = findActiveObject(id);

            if (object == null) {
                return;
            }

            if (!object.isMonitoringRequestSender()) {
                return;
            }

            communicationEventListener.requestMessageSent(object, spyEvent);
        }

        public void replyMessageSent(UniqueID id, SpyEvent spyEvent) {
            if (!controller.isMonitoring()) {
                return;
            }

            ActiveObject object = findActiveObject(id);

            if (object == null) {
                return;
            }

            object.setRequestQueueLength(((SpyMessageEvent) spyEvent).getRequestQueueLength());
            object.setServingStatus(ActiveObject.STATUS_ACTIVE);

            if (!object.isMonitoringReplySender()) {
                return;
            }

            communicationEventListener.replyMessageSent(object, spyEvent);
        }

        public void requestMessageReceived(UniqueID id, SpyEvent spyEvent) {
            if (!controller.isMonitoring()) {
                return;
            }

            ActiveObject object = findActiveObject(id);

            if (object == null) {
                return;
            }

            object.setRequestQueueLength(((SpyMessageEvent) spyEvent).getRequestQueueLength());

            if (!object.isMonitoringRequestReceiver()) {
                return;
            }

            communicationEventListener.requestMessageReceived(object, spyEvent);
        }

        public void replyMessageReceived(UniqueID id, SpyEvent spyEvent) {
            if (!controller.isMonitoring()) {
                return;
            }

            ActiveObject object = findActiveObject(id);

            if (object == null) {
                return;
            }

            if (!object.isMonitoringReplySender()) {
                return;
            }

            communicationEventListener.replyMessageReceived(object, spyEvent);
        }

        public void voidRequestServed(UniqueID id, SpyEvent spyEvent) {
            if (!controller.isMonitoring()) {
                return;
            }

            ActiveObject object = findActiveObject(id);

            if (object == null) {
                return;
            }

            object.setRequestQueueLength(((SpyMessageEvent) spyEvent).getRequestQueueLength());
            object.setServingStatus(ActiveObject.STATUS_ACTIVE);

            if (!object.isMonitoringReplySender()) {
                return;
            }

            communicationEventListener.voidRequestServed(object, spyEvent);
        }

        public void servingStarted(UniqueID id, SpyEvent spyEvent) {
            if (!controller.isMonitoring()) {
                return;
            }

            ActiveObject object = findActiveObject(id);

            if (object == null) {
                return;
            }

            object.setRequestQueueLength(((SpyMessageEvent) spyEvent).getRequestQueueLength());
            object.setServingStatus(ActiveObject.STATUS_SERVING_REQUEST);
        }

        public void allEventsProcessed() {
            if (!controller.isMonitoring()) {
                return;
            }

            communicationEventListener.allEventsProcessed();
        }
    } // end inner class MySpyEventListener
}
