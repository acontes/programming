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
package org.objectweb.proactive.mpi.control;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.VirtualNodeInternal;
import org.objectweb.proactive.core.group.spmd.ProSPMD;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.filetransfer.FileTransfer;
import org.objectweb.proactive.filetransfer.FileVector;
import org.objectweb.proactive.mpi.MPIResult;
import org.objectweb.proactive.mpi.MPISpmd;
import org.objectweb.proactive.mpi.MPISpmdImpl.LateDeploymentHelper;


public class ProActiveMPIManager implements Serializable {
    private final static Logger MPI_IMPL_LOGGER = ProActiveLogger.getLogger(Loggers.MPI_CONTROL_MANAGER);
    public final static String DEFAULT_LIBRARY_NAME = "libProActiveMPIComm.so";

    /** number of jobs */
    private static int currentJobNumber = 0;

    /** list of MPISpmd object */
    private ArrayList<MPISpmd> mpiSpmdList;

    /*  Hashtable<jobID, ProActiveCoupling []> */
    private Hashtable<Integer, ProActiveMPICoupling[]> proxyMap;

    /*  Hashtable<jobID, ProSPMD ProActiveMPICoupling> */
    private Hashtable<Integer, ProActiveMPICoupling> spmdProxyMap;

    /*  Hashtable<jobID, Hashtable<class, ProSPMD user class || user proxy array>> */
    private Hashtable<Integer, Hashtable<String, Object>> userProxyMap;

    /*  ackToStart[jobID] = number of proxy registered */
    private int[] ackToStart;

    /*  ackToRecvlist[jobID] = number of proxy ready to begin activities */
    private int[] ackToRecv;
    private boolean debugWaitForInit = false;

    public ProActiveMPIManager() {
    }

    public void deploy(ArrayList<MPISpmd> spmdList) {
        this.mpiSpmdList = spmdList;
        this.proxyMap = new Hashtable<Integer, ProActiveMPICoupling[]>();
        this.spmdProxyMap = new Hashtable<Integer, ProActiveMPICoupling>();
        this.userProxyMap = new Hashtable<Integer, Hashtable<String, Object>>();
        this.ackToStart = new int[spmdList.size()];
        this.ackToRecv = new int[spmdList.size()];

        // loop on the MPISpmd object list
        try {
            for (int i = 0; i < spmdList.size(); i++) {
                VirtualNodeInternal vn = (VirtualNodeInternal) ((MPISpmd) spmdList.get(currentJobNumber)).getVn();
                Node[] allNodes = vn.getNodes();
                String remoteLibraryPath = ((MPISpmd) spmdList.get(currentJobNumber)).getRemoteLibraryPath();

                ClassLoader cl = this.getClass().getClassLoader();
                java.net.URL u = cl.getResource(
                        "org/objectweb/proactive/mpi/control/" +
                        DEFAULT_LIBRARY_NAME);

                if (remoteLibraryPath != null) {
                    File remoteDest = new File(remoteLibraryPath + "/" +
                            DEFAULT_LIBRARY_NAME);
                    File localSource = new File(u.getFile());

                    FileVector filePushed = FileTransfer.pushFile(allNodes[0],
                            localSource, remoteDest);
                    filePushed.waitForAll();
                }
                //else 
                // we assume local and remote path to reach the shared library were the same

                //Vv Where do we update the LD_LIBRARY_PATH ?
                ackToStart[i] = allNodes.length - 1;
                ackToRecv[i] = allNodes.length - 1;
                Object[][] params = new Object[allNodes.length][];

                // create parameters
                // "Comm" is the name of the JNI Library
                for (int j = 0; j < params.length; j++) {
                    params[j] = new Object[] {
                            "ProActiveMPIComm",
                            (ProActiveMPIManager) ProActive.getStubOnThis(),
                            currentJobNumber
                        };
                }

                MPI_IMPL_LOGGER.info("[MANAGER] Create SPMD Proxy for jobID: " +
                    currentJobNumber);
                ProActiveMPICoupling spmdCouplingProxy = (ProActiveMPICoupling) ProSPMD.newSPMDGroup(ProActiveMPICoupling.class.getName(),
                        params, vn);

                // create ProSPMD proxy
                this.spmdProxyMap.put(currentJobNumber, spmdCouplingProxy);

                MPI_IMPL_LOGGER.info("[MANAGER] Initialize remote environments");
                // initialize queues & semaphores and start thread
                Ack ack = spmdCouplingProxy.initEnvironment();
                ProActive.waitFor(ack);
                MPI_IMPL_LOGGER.info(
                    "[MANAGER] Activate remote thread for communication");
                // once environment is ready, start thread to get mpi process rank  
                spmdCouplingProxy.createRecvThread();
                // initialize joblist & and userProxyList table
                //TODO why would we need this proxyMap as we already have the spmdCouplingProxy ??
                this.proxyMap.put(currentJobNumber,
                    new ProActiveMPICoupling[allNodes.length]);

                this.userProxyMap.put(currentJobNumber,
                    new Hashtable<String, Object>());

                currentJobNumber++;
                //Vv Why don't we return a reference on the ProActiveMpiCoupling in order to avoid manipulate hazardous jobId ?
            }
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ClassNotReifiableException e) {
            e.printStackTrace();
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
    }

    public void register(int jobID, int rank) {
        // ack of corresponding job is null means that the 
        // job is ready to recv message from another job
        MPI_IMPL_LOGGER.info("[MANAGER] JobID #" + jobID + " rank " + rank +
            "has notified its mpi interface is ready (" +
            (this.proxyMap.get(jobID).length - ackToRecv[jobID]) + "/" +
            this.proxyMap.get(jobID).length + ")");

        // Mpi process of that rank has been initialised 
        if (ackToRecv[jobID] == 0) {
            for (int i = 0; i < currentJobNumber; i++) {
                // we wait for all jobs to finish Mpi initialisation
                if (ackToRecv[i] != 0) {
                    return;
                }
            }
            for (int i = 0; i < currentJobNumber; i++) {
                ((ProActiveMPICoupling) spmdProxyMap.get(i)).wakeUpThread();
            }
        } else {
            // we decrease the number of remaining ack to receive
            ackToRecv[jobID]--;
        }
    }

    // insert Comm Active Object at the correct location
    public void register(int jobID, int rank,
        ProActiveMPICoupling activeProxyComm) {
        if (jobID < currentJobNumber) {
            ProActiveMPICoupling[] mpiCouplingArray = ((ProActiveMPICoupling[]) this.proxyMap.get(jobID));

            mpiCouplingArray[rank] = activeProxyComm;

            // test if this job is totally registered
            boolean deployUserSpmdObject = true;
            for (int i = 0; i < mpiCouplingArray.length; i++) {
                if (mpiCouplingArray[i] == null) {
                    // not totally registered
                    deployUserSpmdObject = false;
                }
            }

            //  all proxy are registered
            if (deployUserSpmdObject) {
                // create a new array of nodes well ordered
                Node[] orderedNodes = new Node[mpiCouplingArray.length];
                for (int i = 0; i < orderedNodes.length; i++) {
                    try {
                        orderedNodes[i] = mpiCouplingArray[i].getNode();
                    } catch (NodeException e) {
                        e.printStackTrace();
                    }
                }
                Hashtable<String, Object> userProxyList = new Hashtable<String, Object>();
                try {
                    deployUserSpmdClasses(jobID, orderedNodes, userProxyList);
                    deployUserClasses(jobID, orderedNodes, userProxyList);
                } catch (ClassNotReifiableException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ActiveObjectCreationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NodeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            /* If all jobs have finished */
            for (int i = 0; i < currentJobNumber; i++) {
                int jobListLength = ((ProActiveMPICoupling[]) this.proxyMap.get(i)).length;
                for (int j = 0; j < jobListLength; j++) {
                    if (((ProActiveMPICoupling[]) this.proxyMap.get(i))[j] == null) {
                        return;
                    }
                }
            }

            for (int i = 0; i < currentJobNumber; i++) {
                // send the table of User ProSpmd object to all the Proxy 
                try {
                    //TODO replace by a group call on the spmd proxy object
                    ((ProActiveMPICoupling) spmdProxyMap.get(i)).notifyProxy(this.proxyMap,
                        this.spmdProxyMap, this.userProxyMap);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            this.debugWaitForInit = true;
        } else {
            throw new IndexOutOfBoundsException(" No MPI job exists with num " +
                jobID);
        }
    }

    public boolean waitForInit() {
        return !this.debugWaitForInit;
    }

    public void deployUserClasses(int jobID, Node[] orderedNodes,
        Hashtable<String, Object> userProxyList)
        throws ActiveObjectCreationException, NodeException {
        final HashMap<String, LateDeploymentHelper> userClassMap = mpiSpmdList.get(jobID)
                                                                              .getUserClassToDeploy();

        for (Iterator<Entry<String, LateDeploymentHelper>> iterator = userClassMap.entrySet()
                                                                                  .iterator();
                iterator.hasNext();) {
            final Entry<String, LateDeploymentHelper> e = iterator.next();
            LateDeploymentHelper d = null;
            if ((d = e.getValue()).isUserClass()) {
                final String classname = e.getKey();
                final Object[] proxyList = new Object[orderedNodes.length];
                final List<LateDeploymentHelper> list = d.getUserClassesRank();

                /* Iterate over user class to instantiate */
                for (Iterator<LateDeploymentHelper> rankIt = list.iterator();
                        rankIt.hasNext();) {
                    final LateDeploymentHelper userClassInfo = rankIt.next();
                    proxyList[userClassInfo.getRank()] = ProActive.newActive(classname,
                            (Object[]) userClassInfo.getParams(),
                            orderedNodes[userClassInfo.getRank()]);
                }
                userProxyList.put(classname, proxyList);
            }
        }

        this.userProxyMap.put(jobID, userProxyList);
    }

    public void deployUserSpmdClasses(int jobID, Node[] orderedNodes,
        Hashtable<String, Object> userProxyList)
        throws ClassNotReifiableException, ActiveObjectCreationException,
            NodeException, ClassNotFoundException {
        //  get the list of SPMD class to instantiate for this MPISpmd object and send it as parameter.
        final HashMap<String, LateDeploymentHelper> userClassMap = mpiSpmdList.get(jobID)
                                                                              .getUserClassToDeploy();

        for (Iterator<Entry<String, LateDeploymentHelper>> iterator = userClassMap.entrySet()
                                                                                  .iterator();
                iterator.hasNext();) {
            final Entry<String, LateDeploymentHelper> e = iterator.next();
            LateDeploymentHelper d = null;
            if ((d = e.getValue()).isSpmd()) {
                userProxyList.put(e.getKey(),
                    ProSPMD.newSPMDGroup(e.getKey(),
                        (Object[][]) d.getParams(), orderedNodes));
            }
        }

        this.userProxyMap.put(jobID, userProxyList);
    }

    public void notifyNativeInterfaceIsReady(int jobID) {
        // ack of job is null means we can start MPI application
        MPI_IMPL_LOGGER.info("[MANAGER] JobID #" + jobID +
            " has notified its native interface is ready (" +
            (this.proxyMap.get(jobID).length - ackToStart[jobID]) + "/" +
            this.proxyMap.get(jobID).length + ")");
        if (ackToStart[jobID] == 0) {
            MPISpmd mpiSpmd = (MPISpmd) mpiSpmdList.get(jobID);
            MPIResult res = mpiSpmd.startMPI();
            MPI_IMPL_LOGGER.info(
                "[MANAGER] Start MPI has been send for JobID #" + jobID);
            //System.out.println("[MANAGER] Start return value :" +res.getReturnValue());
            // the prinln generate a deadlock
            //System.out.println(mpiSpmd);
        } else {
            ackToStart[jobID]--;
        }
    }

    public void unregister(int jobID, int rank) {
        if (jobID < currentJobNumber) {
            ((ProActiveMPICoupling[]) this.proxyMap.get(jobID))[rank] = null;
            MPI_IMPL_LOGGER.info("[MANAGER] JobID #" + jobID +
                " unregister mpi process #" + rank);
            for (int i = 0; i < currentJobNumber; i++) {
                int jobListLength = ((ProActiveMPICoupling[]) this.proxyMap.get(i)).length;
                for (int j = 0; j < jobListLength; j++) {
                    if (((ProActiveMPICoupling[]) this.proxyMap.get(i))[j] != null) {
                        return;
                    }
                }
            }

            for (int i = 0; i < this.mpiSpmdList.size(); i++) {
                ((VirtualNodeInternal) ((MPISpmd) this.mpiSpmdList.get(i)).getVn()).killAll(false);
            }
            System.exit(0);
        } else {
            throw new IndexOutOfBoundsException(" No MPI job exists with num " +
                jobID);
        }
    }

    /***
     * @param buf
     * @param count
     * @param datatype
     * @param dest
     * @param tag
     * @param jobID
     */
    public int debugMPISend(byte[] buf, int count, int datatype, int dest,
        int tag, int jobID) {
        //TODO to remove
        //create Message to send and use the native method
        ProActiveMPIData m_r = new ProActiveMPIData();

        m_r.setData(buf);
        m_r.setCount(count);
        m_r.setDatatype(datatype);
        m_r.setDest(dest);
        m_r.setTag(tag);
        m_r.setJobID(jobID);
        if (jobID < proxyMap.size()) {
            ProActiveMPICoupling[] arrayComm = (ProActiveMPICoupling[]) proxyMap.get(jobID);
            if ((dest < arrayComm.length) && (arrayComm[dest] != null)) {
                return arrayComm[dest].receiveFromProActive(m_r);
            } else {
                throw new IndexOutOfBoundsException(
                    " ActiveProxyComm destinator " + dest + " is unreachable!");
            }
        } else {
            throw new IndexOutOfBoundsException(" No MPI job exists with num " +
                jobID);
        }
    }
    ////////////////////////////////////////////////////////////
    ////////// IF MANAGER IS USED AS A PROXY ///////////////////
    ////////////////////////////////////////////////////////////
    //    public void sendMessageToComm(int jobID, MessageRecv m_r) {
    //        int dest = m_r.getDest();
    //        if (jobID < proxyTabByJob.size()) {
    //            ProActiveMPICoupling[] tabOfComm = (ProActiveMPICoupling[]) proxyTabByJob.get(new Integer(
    //                        jobID));
    //            if ((dest < tabOfComm.length) && (tabOfComm[dest] != null)) {
    //                tabOfComm[dest].receiveFromMpi(m_r);
    //
    //                //                System.out.println(
    //                //                    "[JOBMANAGER]sendMessageToComm> One message received from : " +
    //                //                    m_r.getSrc() + " Destinator is :" + dest + " Job: " +
    //                //                    jobID);
    //                // System.out.println(" Message is :" + m_r);
    //            } else {
    //                throw new IndexOutOfBoundsException(
    //                    " ActiveProxyComm destinator " + dest + " is unreachable!");
    //            }
    //        } else {
    //            throw new IndexOutOfBoundsException(" No MPI job exists with num " +
    //                jobID);
    //        }
    //    }
    //
    //    public void allSendMessageToComm(int jobID, MessageRecv m_r) {
    //        if (jobID < proxyTabByJob.size()) {
    //            ProActiveMPICoupling[] allDest = (ProActiveMPICoupling[]) proxyTabByJob.get(new Integer(
    //                        jobID));
    //            for (int i = 0; i < allDest.length; i++) {
    //                if (allDest[i] != null) {
    //                    allDest[i].receiveFromMpi(m_r);
    //                } else {
    //                    System.out.println(
    //                        "[JOBMANAGER]allSendMessageToComm> on destinator is null  : " +
    //                        i + " Job: " + jobID);
    //                }
    //            }
    //            System.out.println("[JOBMANAGER]allSendMessageToComm>  to Job: " +
    //                jobID);
    //        } else {
    //            throw new IndexOutOfBoundsException(" No MPI job exists with num " +
    //                jobID);
    //        }
    //    }
    //    
}
