package org.objectweb.proactive.compi.control;

import java.util.Hashtable;

/**
 * User: emathias
 * Date: 02/04/2007
 * Time: 17:34:48
 */
public interface ProActiveMPICluster {

    /////////////////////////////////
    ////    REGISTERING METHODS  ////
    /////////////////////////////////

    void register(int jobID);

    void register(int jobID, int rank);

    void register(int jobID, int rank, ProActiveMPINode activeProxyComm);

    void unregister(int jobID, int rank);

    public boolean isReadyToRun();

    public boolean isReadyToCommunicate();

    public boolean isReadyToFinalize();


    ////////////////////////////////////////////
    //// COLLECTIVE MESSAGE PASSING METHODS ////
    ////////////////////////////////////////////

    void clusterReceiveFromMpi(ProActiveMPIData m_r);



    /////////////////////////////////
    /////   PROXY  METHODS   ////////
    /////////////////////////////////

    public void createClusterProxy();

    void notifyClusterProxy();

    void wakeUpClusterThread();



    /////////////////
    //// GETTERS ////
    /////////////////

    public int getMaxJobID();

    public ProActiveMPINode getNode(int rank);

    public ProActiveMPINode getNode(int jobID, int rank);

    ProActiveMPICluster getCluster(int jobID);

    public Hashtable<String, Object> getUserProxySpmdMap();

}
