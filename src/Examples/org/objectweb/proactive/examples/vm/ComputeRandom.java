package org.objectweb.proactive.examples.vm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.ProActiveInet;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extra.messagerouting.router.Router;
import org.objectweb.proactive.extra.messagerouting.router.RouterConfig;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.ow2.proactive.virtualizing.core.error.VirtualServiceException;


public class ComputeRandom {

    /**
     * @param args
     * @throws FileNotFoundException
     * @throws ServiceException
     * @throws VirtualServiceException
     * @throws ProActiveException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException,
            VirtualServiceException, ProActiveException, InterruptedException {

        GCMVirtualNode workers = null;
        GCMApplication gcmad = null;
        if (PAProperties.PA_COMMUNICATION_PROTOCOL.getValue().equals("pamr")) {
            System.out.println("protocol == pamr");
            RouterConfig config = new RouterConfig();
            PAProperties port = PAProperties.PA_NET_ROUTER_PORT;
            PAProperties address = PAProperties.PA_NET_ROUTER_ADDRESS;
            if (!port.isSet()) {
                port.setValue(37738);
                System.out.println("port was not set, now it is: " + 37738);
            } else {
                System.out.println("port is : " + port.getValue());
            }
            if (!address.isSet()) {
                address.setValue(ProActiveInet.getInstance().getInetAddress().getHostAddress());
                System.out.println("address was not set, now it is: " +
                    ProActiveInet.getInstance().getInetAddress());
            } else {
                System.out.println("address is: " + address.getValue());
            }
            config.setInetAddress(InetAddress.getByName(address.getValue()));
            config.setPort(port.getValueAsInt());
            Router.createAndStart(config);
        }
        gcmad = PAGCMDeployment.loadApplicationDescriptor(new File(args[0]));
        gcmad.startDeployment();
        workers = gcmad.getVirtualNode("Workers");
        Node firstNode = workers.getANode();
        Node secondNode = workers.getANode();
        Node computeNode = workers.getANode();

        // create active objects
        AOCompute ao1 = (AOCompute) PAActiveObject.newActive(AOCompute.class.getName(), null, firstNode);
        AOCompute ao2 = (AOCompute) PAActiveObject.newActive(AOCompute.class.getName(), null, secondNode);
        AOCompute ao3 = (AOCompute) PAActiveObject.newActive(AOCompute.class.getName(), null, firstNode);
        AOCompute ao4 = (AOCompute) PAActiveObject.newActive(AOCompute.class.getName(), null, secondNode);

        ao1.setRemote(ao2);
        ao2.setRemote(ao3);
        ao3.setRemote(ao4);
        ao4.setRemote(ao1);

        System.out.println("Begining test");

        // Compute
        for (int i = 0; i < 4; i++) {
            AORandom rand = (AORandom) PAActiveObject.newActive(AORandom.class.getName(), null, computeNode);
            ao1.setRandom(rand);
            ao2.setRandom(rand);
            ao3.setRandom(rand);
            ao4.setRandom(rand);
            System.out.println("Compute #" + i + " : " + ao1.compute(42, 1));
            PAActiveObject.terminateActiveObject(rand, false);
            Thread.sleep(1000);
        }

        System.out.println("Test done.");

        // Stop active objects
        PAActiveObject.terminateActiveObject(ao1, false);
        PAActiveObject.terminateActiveObject(ao2, false);
        PAActiveObject.terminateActiveObject(ao3, false);
        PAActiveObject.terminateActiveObject(ao4, false);

        gcmad.kill();

        PALifeCycle.exitSuccess();
    }

}
