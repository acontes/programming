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
package org.objectweb.proactive.compi.control;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.group.ProActiveGroup;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.node.NodeFactory;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.apache.log4j.Logger;


public class ProActiveMPINodeImpl implements Serializable, InitActive, ProActiveMPINode {
    private final static Logger MPI_IMPL_LOGGER = ProActiveLogger.getLogger(Loggers.MPI_CONTROL_MANAGER);

    // clusterItf where node is located
    private ProActiveMPICluster myCluster;

    // native process wrapper
    private ProActiveMPIComm target;
    
    // cache of node references
    private Hashtable<String, ProActiveMPINode> nodeCache;

    private int jobID;

    public ProActiveMPINodeImpl() {
    }

    public ProActiveMPINodeImpl(String libName, Integer jobNum) throws ActiveObjectCreationException, NodeException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.jobID = jobNum.intValue();
        this.nodeCache = new Hashtable<String, ProActiveMPINode>();
        this.target = new ProActiveMPIComm(libName, ProActive.getBodyOnThis().getID().hashCode(), this.jobID);
    }

    public void initActivity(Body body) {
        this.target.setMyNode((ProActiveMPINodeImpl) ProActive.getStubOnThis());
        this.target.createRecvThread();
    }

    /////////////////////////////
    //// REGISTERING METHODS ////
    /////////////////////////////

    public void register() {
        this.myCluster.register(this.jobID);
    }

    public void register(int rank) {
        this.myCluster.register(this.jobID, rank);
    }

    public void register(int jobID, int myRank) {
        this.myCluster.register(jobID, myRank);
    }

    public void registerProcess(int rank) {
        String hostname = "";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        MPI_IMPL_LOGGER.info("[MPI NODE] registering [" + hostname + "] as Rank# " + rank + " JobID#" + this.jobID);
        this.myCluster.register(this.jobID, rank,
                (ProActiveMPINode) ProActive.getStubOnThis());
    }

    public void unregisterProcess(int rank) {
        this.myCluster.unregister(this.jobID, rank);
    }



    /////////////////////////////////
    //// WRAPPER CONTROL METHODS ////
    /////////////////////////////////

    public Ack initEnvironment() {
        this.target.initQueues();
        return new Ack();
    }

    public void createRecvThread() {
        this.target.createRecvThread();
    }

    public void notifyProxy(int maxJobID){
        this.target.sendJobNumberAndRegister(maxJobID);
    }

    public void wakeUpThread() {
        this.target.wakeUpThread();
    }




    /////////////////////////////////
    //// MESSAGE PASSING METHODS ////
    /////////////////////////////////

    public void receiveFromMpi(ProActiveMPIData m_r) {
        this.target.receiveFromMpi(m_r);
    }

    public void receiveFromProActive(ProActiveMPIData m_r) {
        this.target.receiveFromProActive(m_r);
    }

    public void sendToMpi(int jobID, ProActiveMPIData m_r)
            throws IOException {
        int destRank = m_r.getDest();

        ProActiveMPINode destNode = this.nodeCache.get(""+jobID+"_"+destRank);
        if(destNode == null) {
        	destNode = myCluster.getNode(jobID, destRank);
        	this.nodeCache.put(""+jobID+"_"+destRank, destNode);
        }
        

        if (destNode != null) {
            //MPI_IMPL_LOGGER.info("[MPI NODE]"+destRank + "-" + jobID +   "was not null, so receive from");
            destNode.receiveFromMpi(m_r);
        } else {
            throw new IndexOutOfBoundsException(" destination " + destRank + " in the jobID " + jobID + " is unreachable!");
        }
    }

    public Ack sendToMpi(int jobID, ProActiveMPIData m_r, boolean b)
            throws IOException {
        this.sendToMpi(jobID, m_r);
        return new Ack();
    }

    public void MPISend(byte[] buf, int count, int datatype, int destRank,
                        int tag, int jobID) {
        //create Message to send and use the native method
        ProActiveMPIData m_r = new ProActiveMPIData();

        m_r.setData(buf);
        m_r.setCount(count);
        m_r.setDatatype(datatype);
        m_r.setDest(destRank);
        m_r.setTag(tag);
        m_r.setJobID(jobID);

        ProActiveMPINode destNode = this.nodeCache.get(""+jobID+"_"+destRank);
        if(destNode == null) {
        	destNode = myCluster.getNode(jobID, destRank);
        	this.nodeCache.put(""+jobID+"_"+destRank, destNode);
        }
        

        if (destNode != null) {
            destNode.receiveFromProActive(m_r);
        } else {
            throw new IndexOutOfBoundsException(" destination " + destRank + " in the jobID " + jobID + " is unreachable!");
        }
    }

    public void allSendToMpi(int jobID, ProActiveMPIData m_r) {
        ProActiveMPICluster destCluster;
        if (jobID < myCluster.getMaxJobID()) {
            destCluster = myCluster.getCluster(jobID);
            destCluster.clusterReceiveFromMpi(m_r);
        } else {
            throw new IndexOutOfBoundsException(" MPI job with such ID: " +
                    jobID + " doesn't exist");
        }
    }


    public void sendToProActive(int jobID, ProActiveMPIData m_r)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SecurityException, NoSuchMethodException,
            ClassNotFoundException {

        int dest = m_r.getDest();

        if (jobID < myCluster.getMaxJobID()) {

            Hashtable proSpmdByClasses = this.myCluster.getCluster(jobID).getUserProxySpmdMap();
            //(Hashtable) this.userProxyMap.get(new Integer(jobID));

            Object proSpmdGroup = proSpmdByClasses.get(m_r.getClazz());

            // if the corresponding object exists, its a -ProSpmd object- or a -proxy-
            if (proSpmdGroup != null) {
                Group g = ProActiveGroup.getGroup(proSpmdByClasses.get(m_r.getClazz()));

                // its a ProSpmd Object
                if (g != null) {
                    // extract the specified object from the group and call method on it
                    ((Method) g.get(dest).getClass()
                            .getDeclaredMethod(m_r.getMethod(),
                                    new Class[]{ProActiveMPIData.class})).invoke(g.get(dest),
                            new Object[]{m_r});
                } else {
                    if (((Object[]) proSpmdByClasses.get(m_r.getClazz()))[dest] != null) {
                        ((Method) ((Object[]) proSpmdByClasses
                                .get(m_r.getClazz()))[dest].getClass()
                                .getDeclaredMethod(m_r.getMethod(),
                                        new Class[]{ProActiveMPIData.class})).invoke(((Object[]) proSpmdByClasses.get(
                                m_r.getClazz()))[dest], new Object[]{m_r});
                    } else {
                        throw new ClassNotFoundException(
                                "The Specified User Class *** " + m_r.getClazz() +
                                        "*** doesn't exist !!!");
                    }
                }
            }
            // the specified class doesn't exist  
            else {
                throw new ClassNotFoundException(
                        "The Specified User Class *** " + m_r.getClazz() +
                                "*** doesn't exist !!!");
            }
        } else {
            throw new IndexOutOfBoundsException(" No MPI job exists with num " +
                    jobID);
        }
    }



    /////////////////////////
    ///   GETTERS/SETTERS ///
    /////////////////////////

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(target.toString());
        sb.append("\n MPIJobNum: " + this.jobID);
        return sb.toString();
    }

    public int getJobID() {
        return jobID;
    }

    public int getRank() {
        return target.getRank();
    }

    public Node getNode() throws NodeException {
        return NodeFactory.getNode(ProActive.getBodyOnThis().getNodeURL());
    }

    public void setManager(ProActiveMPICluster proActiveMPICluster) {
        this.myCluster = proActiveMPICluster;
    }


}
