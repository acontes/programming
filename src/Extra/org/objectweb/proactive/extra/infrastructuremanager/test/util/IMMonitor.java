package org.objectweb.proactive.extra.infrastructuremanager.test.util;

import java.net.URI;
import java.util.ArrayList;

import org.objectweb.proactive.extra.infrastructuremanager.IMFactory;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMAdmin;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.frontend.DynamicNSInterface;


/**
 * Console Monitoring for infrastructure manager
 * Not working for now
 * @author jmartin
 *
 */
public class IMMonitor {

    /**
     * TODO Not Yet implemented
     * @param args
     */
    public static void main(String[] args) throws Exception {
        IMAdmin admin = IMFactory.getAdmin(new URI("rmi://localhost:1242/"));
        //        IMUser user = IMFactory.getUser(new URI("rmi://localhost:1242/"));
        //        IMMonitoring monitor = IMFactory.getMonitoring(new URI(
        //                    "rmi://localhost:1242/"));
        System.out.println("infos :");
        ArrayList<DynamicNSInterface> dynNS = admin.getDynamicNodeSources();
        System.out.println("" + dynNS.size() + " dyn nodesources");
        //        while(false) {
        //        	
        //        }
    }
}
