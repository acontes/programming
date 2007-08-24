package org.objectweb.proactive.extra.infrastructuremanager.test.util;

import java.net.URI;
import java.util.ArrayList;

import org.objectweb.proactive.extra.infrastructuremanager.IMFactory;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMAdmin;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMMonitoring;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMUser;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.dynamic.DynamicNodeSource;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.frontend.DynamicNSInterface;


public class IMMonitor {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        IMAdmin admin = IMFactory.getAdmin(new URI("rmi://localhost:1242/"));
        IMUser user = IMFactory.getUser(new URI("rmi://localhost:1242/"));
        IMMonitoring monitor = IMFactory.getMonitoring(new URI(
                    "rmi://localhost:1242/"));
        System.out.println("infos :");
        ArrayList<DynamicNSInterface> dynNS = admin.getDynamicNodeSources();
        System.out.println("" + dynNS.size() + " dyn nodesources");
        //        while(false) {
        //        	
        //        }
    }
}
