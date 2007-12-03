package org.objectweb.proactive.compi.control;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.compi.MPISpmd;


/**
 * User: emathias
 * Date: Apr 22, 2007
 * Time: 3:00:15 AM
 */
public class ProActiveMPIClusterComp extends ProActiveMPIClusterImpl
    implements ProActiveMPICluster, BindingController {
    private Hashtable<String, Object> nodesItf = new Hashtable<String, Object>();
    private Hashtable<String, Object> clustersItf = new Hashtable<String, Object>();

    public ProActiveMPIClusterComp() {
    }

    public ProActiveMPIClusterComp(MPISpmd spmd, Integer numbOfNodes,
        Integer jobID, Integer maxJobID) {
        super(spmd, numbOfNodes, jobID, maxJobID);
    }

    public String[] listFc() {
        Vector<String> v = new Vector<String>();

        // nodesItf bound
        Enumeration<String> e = nodesItf.keys();
        while (e.hasMoreElements())
            v.add(e.nextElement());
        e = clustersItf.keys();
        while (e.hasMoreElements())
            v.add(e.nextElement());

        return v.toArray(new String[] {  });
    }

    public Object lookupFc(String itfName) throws NoSuchInterfaceException {
        if (itfName.startsWith("cluster2node")) {
            return nodesItf.get(itfName);
        } else if (itfName.startsWith("cluster2cluster")) {
            return clustersItf.get(itfName);
        }
        return null;
    }

    public void bindFc(String itfName, Object itf)
        throws NoSuchInterfaceException, IllegalBindingException,
            IllegalLifeCycleException {
        if (itfName.startsWith("cluster2node")) {
            System.out.println("binding node in clusterItf");
            this.nodesItf.put(itfName, itf);
            this.addNode((ProActiveMPINode) itf);
        } else if (itfName.startsWith("cluster2cluster")) {
            System.out.println("binding clusterItf in clusterItf");
            this.clustersItf.put(itfName, itf);
            this.addCluster((ProActiveMPICluster) itf);
        }
    }

    public void unbindFc(String itfName)
        throws NoSuchInterfaceException, IllegalBindingException,
            IllegalLifeCycleException {
        if (itfName.startsWith("cluster2node")) {
            this.nodesItf.remove(itfName);
            //to do what is needed to remove binding in the impl;
        } else if (itfName.startsWith("cluster2cluster")) {
            this.clustersItf.remove(itfName);
        }
    }
}
