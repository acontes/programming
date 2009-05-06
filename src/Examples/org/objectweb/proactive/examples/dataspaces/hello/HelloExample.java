package org.objectweb.proactive.examples.dataspaces.hello;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.api.PARemoteObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.remoteobject.RemoteObjectExposer;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extra.dataspaces.BaseScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.NamingService;
import org.objectweb.proactive.extra.dataspaces.Utils;
import org.objectweb.proactive.extra.dataspaces.exceptions.AlreadyConfiguredException;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


public class HelloExample {

    private static final int DESCRIPTOR_FILENAME_ARG = 0;
    private static final String NAME = "DSnamingservice";
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    // FIXME ugly hack to obtain hard coded application id
    private static final long applicationId = Utils.getApplicationId(null);

    private final File descriptorFile;
    private final BaseScratchSpaceConfiguration scratchSpaceConfiguration1;
    private RemoteObjectExposer<NamingService> roe;
    private NamingService namingService;
    private List<Node> nodesGrabbed;
    private Map<Node, DataSpacesInstaller> nodesDSInstallers = new HashMap<Node, DataSpacesInstaller>();
    private DataSpacesInstaller localInstaller;

    /**
     * @param args
     * @throws ProActiveException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException, ProActiveException {

        HelloExample hw = new HelloExample(args);
        hw.example();
    }

    private void example() {
        GCMApplication applicationDescriptor;
        NamingService namingServiceStub;

        final String nsURL = startNamingService();

        try {
            namingServiceStub = NamingService.createNamingServiceStub(nsURL);
        } catch (ProActiveException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }

        try {
            applicationDescriptor = startNodes(nsURL);
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }

        checkEnoughRemoteNodesOrDie(1);

        try {
            Node node;
            DataSpacesInstaller installer;
            final Iterator<Node> nodes = nodesGrabbed.iterator();

            node = nodes.next();
            installer = nodesDSInstallers.get(node);
            //            installer = localInstaller;
            installer.startDataSpaces(scratchSpaceConfiguration1);
            installer.stopDataSpaces();

        } catch (FileSystemException e) {
            e.printStackTrace();
        } catch (AlreadyConfiguredException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } catch (NotConfiguredException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        applicationDescriptor.kill();
        PALifeCycle.exitSuccess();

        try {
            stopNamingService();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }
    }

    private void checkEnoughRemoteNodesOrDie(int i) {
        if (nodesGrabbed.size() < i)
            throw new RuntimeException("Not enough nodes to run example");
    }

    private GCMApplication startNodes(String nsURL) throws ProActiveException {

        final GCMApplication applicationDescriptor = PAGCMDeployment
                .loadApplicationDescriptor(descriptorFile);

        final GCMVirtualNode vnode = applicationDescriptor.getVirtualNode("Hello");

        applicationDescriptor.startDeployment();
        vnode.waitReady();

        // grab nodes here
        nodesGrabbed = vnode.getCurrentNodes();
        for (Node n : nodesGrabbed) {
            final DataSpacesInstaller r = (DataSpacesInstaller) PAActiveObject.newActive(
                    DataSpacesInstaller.class.getName(), new Object[] { nsURL }, n);
            nodesDSInstallers.put(n, r);
        }

        localInstaller = (DataSpacesInstaller) PAActiveObject.newActive(DataSpacesInstaller.class.getName(),
                new Object[] { nsURL });

        logger.info("Nodes with data spaces installers started: " + nodesGrabbed.size() + " nodes + 1 local");
        return applicationDescriptor;
    }

    private String startNamingService() {
        namingService = new NamingService();

        roe = PARemoteObject.newRemoteObject(NamingService.class.getName(), namingService);
        roe.createRemoteObject(NAME);

        final String url = roe.getURL();
        logger.info("Naming Service successfully started on: " + url);

        return url;
    }

    private void stopNamingService() throws ProActiveException {
        if (roe != null) {
            roe.unregisterAll();
            roe = null;
        }
    }

    private HelloExample(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java " + this.getClass().getName() +
                " <application descriptor filename>");
            System.exit(0);
        }

        scratchSpaceConfiguration1 = null;
        descriptorFile = new File(args[DESCRIPTOR_FILENAME_ARG]);
    }
}
