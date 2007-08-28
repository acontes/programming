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

import org.apache.log4j.Logger;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;

import java.net.UnknownHostException;


public class ProActiveMPIComm {
    private static Logger logger = ProActiveLogger.getLogger(Loggers.MPI_CONTROL_COUPLING);
    private String hostname = "NULL";
    private volatile boolean shouldRun = true;

    private ProActiveMPINode myNode;

    /* flag that determines whether process is ready to receive messages */
    private boolean readyToReceive = true;

    /* jobid received in the creation */
    private int jobID;

    /* rank of the wrapped mpi process */
    private int rank = -1;


    ////////////////////////
    //// NATIVE METHODS ////
    ////////////////////////

    private native int initRecvQueue();

    private native int initSendQueue();

    private native int sendJobNb(int jobNumber);

    private native int init(String userPath, int r);

    private native int closeQueue();

    private native int closeAllQueues();

    private native int sendRequest(ProActiveMPIData m_r, byte[] bs);

    private native byte[] recvRequest(ProActiveMPIData m_r);

    public native int proActiveSendRequest(ProActiveMPIData m_r, byte[] bs);

    ////////////////////////////////
    //// CONSTRUCTOR METHODS    ////
    ////////////////////////////////
    public ProActiveMPIComm() {
    }

    public ProActiveMPIComm(String libName, int uniqueID, int jobID) {
        try {
            hostname = java.net.InetAddress.getLocalHost().getHostName();
            logger.info("[MPI COMM] [" + this.hostname +
                    "] Constructor> : Loading library.");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

	if (logger.isInfoEnabled()) {
            logger.info(System.getProperty("java.library.path"));
        }

        System.loadLibrary(libName);

        logger.info("[MPI COMM] [" + this.hostname +
                "] Constructor> : Library loaded.");
        this.jobID = jobID;
        // initialize semaphores & log files
        this.init(uniqueID);
    }

    ////////////////////////////////
    //// INTERNAL METHODS ////
    ////////////////////////////////
    public void initQueues() {
        logger.info("[MPI COMM] [" + this.hostname +
                "] initQueues> : init receiving queue: " + initRecvQueue());

        logger.info("[MPI COMM] [" + this.hostname +
                "] initQueues> : init sending queue: " + initSendQueue());
    }

    public void closeQueues() {
        logger.info("[MPI COMM] [" + this.hostname +
                "] closeQueues> : closeQueue: " + closeQueue());
    }

    public void closeAllSRQueues() {
        logger.info("[MPI COMM] [" + this.hostname +
                "] closeAllSRQueues> : closeAllQueues: " + closeAllQueues());
    }

    public void createRecvThread() {
        Runnable r = new MessageRecvHandler();
        Thread t = new Thread(r, "Thread Message Recv");
        logger.info("[MPI COMM] [" + this.hostname +
                "]  creating recvThread");
        t.start();
    }

    public void sendJobNumberAndRegister() {
        sendJobNumber(this.jobID);
        logger.info("[MPI COMM] Registering: " + this.jobID + "|" + rank);
        this.myNode.register(this.jobID, rank);
    }

    public void wakeUpThread() {
        logger.info("[MPI COMM] [" + this.hostname +
                "] activeThread> : activate thread");
        this.readyToReceive = true;
    }

    public void asleepThread() {
        this.readyToReceive = false;
    }

    ////////////////////////////////
    //// INITIALIZATION METHODS ////
    ////////////////////////////////
    public void setMyNode(ProActiveMPINode myNode) {
        this.myNode = myNode;
    }

    public void init(int uniqueID) {
        logger.info("[MPI COMM] [" + this.hostname + "] init> : init: " + init(System.getProperty("user.home"), uniqueID));
        this.closeAllSRQueues();
        this.initQueues();
    }

    ////////////////////////////////
    //// COMMUNICATION METHODS  ////
    ////////////////////////////////
    public void receiveFromProActive(ProActiveMPIData m_r) {
        proActiveSendRequest(m_r, m_r.getData());
    }

    public void sendJobNumber(int jobNumber) {
        logger.info("[MPI COMM] [" + this.hostname +
                "] sendJobNumber> send job number " + sendJobNb(jobNumber));
    }

    public void receiveFromMpi(ProActiveMPIData m_r) {
        if (m_r.getData() == null) {
            throw new RuntimeException("[MPI COMM] !!! DATA are null ");
        }

        // byte[]
        sendRequest(m_r, m_r.getData());
    }

    public int getRank() {
        return rank;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n Class: ").append(this.getClass().getName()).append("\n Hostname: ").
                append(this.hostname).append("\n rank: ").append(this.rank);
        return sb.toString();
    }

    /**
     * This class reads all messages from the message queue C2S
     */
    protected class MessageRecvHandler implements Runnable {
        public MessageRecvHandler() {
        }

        public void run() {
            // signal the job manager that this daemon is ok to recv message
            logger.info("[MPI COMM] init registering");
            myNode.register();
            ProActiveMPIData m_r = new ProActiveMPIData();
            byte[] data;
            Ack ack = new Ack();
            int count = 0;
            while (shouldRun) {
                if (readyToReceive) {
                    try {
                        if ((data = recvRequest(m_r)) == null) {
                            throw new RuntimeException(
                                    "[MPI COMM]!!! ERROR data received are NULL from native method");
                        }
                        //check TAG1
                        if (m_r.getMsgType()  == ProActiveMPIConstants.COMM_MSG_INIT) {
                            rank = m_r.getSrc();
                            myNode.registerProcess(rank);
                            asleepThread();
                        } else if (m_r.getMsgType() == ProActiveMPIConstants.COMM_MSG_SEND) {
                            m_r.setData(data);
                            int jobRecver = m_r.getjobID();
                            m_r.setJobID(jobID);
                            count++;
                            if ((count % 1000) == 0) {
                                // wait for old acknowledge
                                ProActive.waitFor(ack);
                                // create new Acknowledge
                                ack = myNode.sendToMpi(jobRecver, m_r, false);
                            } else {
                                myNode.sendToMpi(jobRecver, m_r);
                            }
                        } else if (m_r.getMsgType() == ProActiveMPIConstants.COMM_MSG_SEND_PROACTIVE) {
                            //                      	        System.out.println("[" + hostname +
                            //                                  "] TREAD] RECVING MESSAGE-> SENDING");
                            m_r.setData(data);
                            int jobRecver = m_r.getjobID();
                            m_r.setJobID(jobID);
                            m_r.parseParameters();
                            myNode.sendToProActive(jobRecver, m_r);
                        } else if (m_r.getMsgType() == ProActiveMPIConstants.COMM_MSG_ALLSEND) {
                            m_r.setData(data);
                            int jobRecver = m_r.getjobID();
                            m_r.setJobID(jobID);
                            myNode.allSendToMpi(jobRecver, m_r);
                        } else if (m_r.getMsgType() == ProActiveMPIConstants.COMM_MSG_FINALIZE) {
                            closeQueues();
                            myNode.unregisterProcess(rank);
                            shouldRun = false;
                        } else {
                            logger.info("[MPI COMM] TAG UNKNOWN ");
                        }
                    } catch (Exception e) {
                        System.out.println("In Java:\n\t" + e);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
