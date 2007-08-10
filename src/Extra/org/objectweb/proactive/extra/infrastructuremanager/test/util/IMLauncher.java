package org.objectweb.proactive.extra.infrastructuremanager.test.util;

import java.io.File;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.IMFactory;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMAdmin;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMMonitoring;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMUser;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.NodeSet;


public class IMLauncher {

    /**
     *
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        IMFactory.startLocal();
        IMAdmin admin = IMFactory.getAdmin();
        IMUser user = IMFactory.getUser();
        IMMonitoring monitor = IMFactory.getMonitoring();
        admin.deployAllVirtualNodes(new File(
                "/user/jmartin/home/test.xml"),
            null);
        admin.deployAllVirtualNodes(new File(
        "/user/jmartin/home/SVN/ProActiveScheduler/descriptors/scheduler/deployment/Demo_descriptor.xml"),
    null);
//        PADNSInterface padInterface = admin.getPADNodeSource();
//        padInterface.
        Thread.sleep(10000);
//        System.out.println("Number of nodes : "+ monitor.getNumberOfAllResources().intValue());
//        
//        System.out.println("Asking for 2 nodes :");
//        NodeSet ns = user.getAtMostNodes(new IntWrapper(3), null);
//        System.out.println("Nodes obtained : "+ ns.size());
//        System.out.println("Free nodes : "+ monitor.getNumberOfFreeResource().intValue());
//        System.out.println("Free nodes : "+ monitor.getNumberOfFreeResource().intValue());
        System.in.read();
        try {
        	IMFactory.getAdmin().shutdown();
        } catch (Exception e) {
        	e.printStackTrace();
        	ProActive.exitFailure();
        }
    }
}
