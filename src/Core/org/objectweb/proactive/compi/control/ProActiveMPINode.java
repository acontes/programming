package org.objectweb.proactive.compi.control;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;


/**
 * User: emathias
 * Date: 02/04/2007
 * Time: 16:55:10
 */
public interface ProActiveMPINode {
    /////////////////////////////////
    ////    REGISTERING METHODS  ////
    /////////////////////////////////
    void register();

    void register(int rank);

    void register(int jobID, int myRank);

    void registerProcess(int rank);

    void unregisterProcess(int rank);

    /////////////////////////////////
    //// MESSAGE PASSING METHODS ////
    /////////////////////////////////
    void receiveFromMpi(ProActiveMPIData m_r);

    void receiveFromProActive(ProActiveMPIData m_r);

    void sendToMpi(int jobID, ProActiveMPIData m_r) throws IOException;

    Ack sendToMpi(int jobID, ProActiveMPIData m_r, boolean b)
        throws IOException;

    void sendToProActive(int jobID, ProActiveMPIData m_r)
        throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SecurityException, NoSuchMethodException,
            ClassNotFoundException;

    void allSendToMpi(int jobID, ProActiveMPIData m_r);

    /////////////////////////////////
    //// WRAPPER CONTROL METHODS ////
    /////////////////////////////////
    Ack initEnvironment();

    void createRecvThread();

    public void notifyProxy(int nbOfClusters);

    void wakeUpThread();

    /////////////////
    //// GETTERS ////
    /////////////////
    Node getNode() throws NodeException;

    int getJobID();

    int getRank();
}
