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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.compi.MPISpmd;
import org.objectweb.proactive.core.group.ProxyForGroup;
import org.objectweb.proactive.core.group.spmd.ProSPMD;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.mop.ConstructionOfReifiedObjectFailedException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class ProActiveMPIClusterImpl implements Serializable,
    ProActiveMPICluster, InitActive {
    private final static Logger MPI_IMPL_LOGGER = ProActiveLogger.getLogger(Loggers.MPI_CONTROL_MANAGER);
    public final static String DEFAULT_LIBRARY_NAME = "libProActiveMPIComm.so";
    private int jobID;
    private int maxJobID;
    private MPISpmd mpiSpmd;

    /* hash containing references to all of the clusters */
    private Hashtable<Integer, ProActiveMPICluster> clusters;

    /* ordered list of nodes within the clusterItf. Comes from the register of processes */
    private ProActiveMPINode[] nodes;

    /* non-ordered list of PAMPINodes. Comes from add node */
    private List<ProActiveMPINode> nodeList;

    /* Hashtable<class, ProSPMD user class || user proxy array>> */
    private Hashtable<String, Object> userProxySpmdMap;
    private Hashtable<String, Object[]> userProxyClassesMap;

    /* proxy to do group-calls in all the nodes of the given clusterItf */
    private ProActiveMPINode myClusterProxy;

    /* number of proxy registered */
    private int ackToStart;

    /* number of proxy ready to begin activities */
    private int ackToRecv;

    /* flags to synchronize init of execution*/
    boolean readyToInit = true;
    boolean readyToFinalize = false;
    boolean readyToRun = false;
    boolean readyToCommunicate = false;
    private boolean threadAwaked = false;
    private boolean proxyNotified = false;

    public ProActiveMPIClusterImpl() {
    }

    public ProActiveMPIClusterImpl(MPISpmd spmd, Integer numbOfNodes,
        Integer jobID, Integer maxJobID) {
        this.mpiSpmd = spmd;
        this.jobID = jobID;
        this.maxJobID = maxJobID;
        this.ackToStart = numbOfNodes - 1;
        this.ackToRecv = numbOfNodes - 1;
        this.nodes = new ProActiveMPINodeImpl[numbOfNodes];
        this.nodeList = new ArrayList<ProActiveMPINode>();
        this.clusters = new Hashtable<Integer, ProActiveMPICluster>();
        this.userProxyClassesMap = new Hashtable<String, Object[]>();
        this.userProxySpmdMap = new Hashtable<String, Object>();
    }

    public void initActivity(Body body) {
        ProActive.setImmediateService("isReadyToRun");
        ProActive.setImmediateService("isReadyToCommunicate");
        ProActive.setImmediateService("isReadyToFinalize");
    }

    public void register(int jobID) {
        // ack of job is null means we can start MPI application
        if (ackToStart == 0) {
            MPI_IMPL_LOGGER.info("[MPI CLUSTER] Starting MPI for jobID: " +
                jobID);
            mpiSpmd.startMPI();
        } else {
            ackToStart--;
        }
    }

    public void register(int jobID, int rank) {
        if (ackToRecv == 0) {
            this.readyToCommunicate = true;

            for (int i = 0; i < this.maxJobID; i++) {
                ProActiveMPICluster clusterIterator = this.getCluster(i);
                if (!clusterIterator.isReadyToCommunicate()) {
                    return;
                }
            }

            // if all the clusters have already registered their processes
            // unblocks wrapper to receive ranks and register
            for (int i = 0; i < this.maxJobID; i++) {
                ProActiveMPICluster clusterIterator = this.getCluster(i);
                clusterIterator.wakeUpClusterThread();
            }
        } else {
            ackToRecv--;
        }
    }

    // insert Comm Active Object at the correct location
    public void register(int jobID, int rank, ProActiveMPINode activeProxyComm) {
        if ((jobID < this.maxJobID) && (jobID == this.getJobID())) {
            MPI_IMPL_LOGGER.info("[MPI CLUSTER] JobID #" + jobID +
                " register mpi process #" + rank);

            this.nodes[rank] = activeProxyComm;

            // test if this job is totally registered
            this.readyToInit = true;
            for (ProActiveMPINode aProxyMap : this.nodes) {
                if (aProxyMap == null) {
                    this.readyToInit = false;
                }
            }

            if (this.readyToInit) {
                this.readyToRun = true;
                // create a new array of pa nodes well ordered
                Node[] orderedNodes = new Node[this.nodes.length];
                for (int i = 0; i < orderedNodes.length; i++) {
                    try {
                        orderedNodes[i] = this.nodes[i].getNode();
                    } catch (NodeException e) {
                        e.printStackTrace();
                    }
                }
                MPI_IMPL_LOGGER.info(
                    "[MPI CLUSTER] deploy user classes and spmd ");
                deployUserSpmdClasses(jobID, orderedNodes);
                deployUserClasses(jobID, orderedNodes);
            }

            // test if all the clusters have all the process registered
            for (int i = 0; i < this.maxJobID; i++) {
                ProActiveMPICluster clusterIterator = this.getCluster(i);
                if (clusterIterator != null) {
                    if (!clusterIterator.isReadyToRun()) {
                        return;
                    }
                }
            }

            // if all the process in all the clusters are registered,
            // unblocks native processes by sending jobNb
            for (int i = 0; i < this.maxJobID; i++) {
                ProActiveMPICluster cluster = this.getCluster(i);
                if (cluster != null) {
                    MPI_IMPL_LOGGER.info(
                        "[MPI CLUSTER] deploy notify cluster proxy ");
                    cluster.notifyClusterProxy();
                } else {
                    System.err.println("ERROR: No MPI job exists with num " +
                        jobID);
                }
            }
        }
    }

    public void unregister(int jobID, int rank) {
        if (jobID < this.maxJobID) {
            this.nodes[rank] = null;
            MPI_IMPL_LOGGER.info("[MPI CLUSTER] JobID #" + jobID +
                " unregister mpi process #" + rank);

            for (ProActiveMPINode aProxyMap : this.nodes) {
                if (aProxyMap != null) {
                    return;
                }
            }

            this.readyToFinalize = true;
        } else {
            throw new IndexOutOfBoundsException(" No MPI job exists with num " +
                jobID);
        }
    }

    public void deployUserClasses(int jobID, Node[] orderedNodes) {
        //    get the list of classes to instanciate for this MPISpmd object
        // 	  and send it as parameter.
        ArrayList classes = mpiSpmd.getClasses();
        if (!classes.isEmpty()) {
            MPI_IMPL_LOGGER.info("[MPI CLUSTER] JobID #" + jobID +
                " deploy user classes");
            // get the table of parameters
            Hashtable paramsTable = mpiSpmd.getClassesParams();
            Hashtable<String, Object[]> userProxyList = new Hashtable<String, Object[]>();
            for (Object aClass : classes) {
                String cl = (String) aClass;
                try {
                    Object[] parameters = (Object[]) paramsTable.get(cl);
                    Object[] proxyList = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        Object[] params = (Object[]) parameters[i];
                        if (params != null) {
                            proxyList[i] = ProActive.newActive(cl, params,
                                    orderedNodes[i]);
                        }
                    }
                    userProxyList.put(cl, proxyList);
                    this.userProxyClassesMap = userProxyList;
                } catch (ActiveObjectCreationException e) {
                    e.printStackTrace();
                } catch (NodeException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deployUserSpmdClasses(int jobID, Node[] orderedNodes) {
        //  get the list of SPMD class to instanciate for this MPISpmd object
        // 	  and send it as parameter.
        ArrayList classes = mpiSpmd.getSpmdClasses();
        if (!classes.isEmpty()) {
            MPI_IMPL_LOGGER.info("[MPI CLUSTER] JobID #" + jobID +
                " deploy user SPMD classes");
            // get the table of parameters
            Hashtable paramsTable = mpiSpmd.getSpmdClassesParams();
            Hashtable<String, Object> userProxyList = new Hashtable<String, Object>();
            for (Object aClass : classes) {
                String cl = (String) aClass;
                try {
                    ArrayList parameters = (ArrayList) paramsTable.remove(cl);

                    // simple array parameter
                    if (parameters.get(0) != null) {
                        Object[] params = (Object[]) parameters.get(0);
                        Object[][] p = new Object[orderedNodes.length][];
                        for (int i = 0; i < orderedNodes.length; i++) {
                            p[i] = params;
                        }
                        userProxyList.put(cl,
                            ProSPMD.newSPMDGroup(cl, p, orderedNodes));
                    } // matrix parameter 
                    else if (parameters.get(1) != null) {
                        Object[][] params = (Object[][]) parameters.get(1);
                        userProxyList.put(cl,
                            ProSPMD.newSPMDGroup(cl, params, orderedNodes));
                    } // no parameters 
                    else {
                        Object[][] params = new Object[orderedNodes.length][];
                        userProxyList.put(cl,
                            ProSPMD.newSPMDGroup(cl, params, orderedNodes));
                    }
                    this.userProxySpmdMap = userProxyList;
                } catch (ClassNotReifiableException e) {
                    e.printStackTrace();
                } catch (ActiveObjectCreationException e) {
                    e.printStackTrace();
                } catch (NodeException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } // end_try
            } // end_while
        } // end_if_classes
    }

    ////////////////////////////////
    //// SPMD  RELATED METHODS  ////
    ////////////////////////////////
    public void createClusterProxy() {
        try {
            MPI_IMPL_LOGGER.info("[MPI CLUSTER] Create SPMD Proxy for jobID: " +
                jobID);
            ProxyForGroup proxy = new ProxyForGroup(ProActiveMPINode.class.getName());
            proxy.addAll(nodeList);
            myClusterProxy = (ProActiveMPINode) proxy.getGroupByType();
        } catch (ConstructionOfReifiedObjectFailedException e) {
            e.printStackTrace();
        }
    }

    public void wakeUpClusterThread() {
        if (!threadAwaked) {
            this.myClusterProxy.wakeUpThread();
            this.threadAwaked = true;
        }
    }

    public void notifyClusterProxy() {
        if (!proxyNotified) {
            this.myClusterProxy.notifyProxy(this.getMaxJobID());
            this.proxyNotified = true;
        }
    }

    ////////////////////////////////////////////
    //// COLLECTIVE MESSAGE PASSING METHODS ////
    ////////////////////////////////////////////
    public void clusterReceiveFromMpi(ProActiveMPIData m_r) {
        this.myClusterProxy.receiveFromMpi(m_r);
    }

    ////////////////////////////////
    //// GETTER/SETTER METHODS  ////
    ////////////////////////////////
    public synchronized void addNode(ProActiveMPINode newNode) {
        this.nodeList.add(newNode);
    }

    public void addCluster(ProActiveMPICluster newCluster) {
        int numOfRegisteredClusters = clusters.size();
        clusters.put(numOfRegisteredClusters, newCluster);
        //numOfRegisteredClusters++;
    }

    public ProActiveMPICluster getCluster(int jobID) {
        return clusters.get(new Integer(jobID));
    }

    public ProActiveMPINode getNode(int rank) {
        return nodes[rank];
    }

    public ProActiveMPINode getNode(int jobID, int rank) {
        ProActiveMPICluster jobIDcluster;

        if (jobID == this.jobID) {
            return this.getNode(rank);
        } else if ((jobIDcluster = getCluster(jobID)) != null) {
            return jobIDcluster.getNode(rank);
        }
        return null;
    }

    public int getJobID() {
        return this.jobID;
    }

    public int getMaxJobID() {
        return this.maxJobID;
    }

    public int getAckToRecv() {
        return ackToRecv;
    }

    public int getAckToStart() {
        return ackToStart;
    }

    public ProActiveMPINode[] getNodes() {
        return this.nodes;
    }

    public Hashtable<String, Object[]> getUserProxyClassesMap() {
        return userProxyClassesMap;
    }

    public Hashtable<String, Object> getUserProxySpmdMap() {
        return userProxySpmdMap;
    }

    public boolean isReadyToRun() {
        return readyToRun;
    }

    public boolean isReadyToInit() {
        return readyToInit;
    }

    public boolean isReadyToFinalize() {
        return readyToFinalize;
    }

    public boolean isReadyToCommunicate() {
        return readyToCommunicate;
    }

    public boolean isThreadAwaked() {
        return this.threadAwaked;
    }

    public boolean isProxyNotified() {
        return this.proxyNotified;
    }
}
