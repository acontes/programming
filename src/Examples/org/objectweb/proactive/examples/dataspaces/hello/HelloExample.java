package org.objectweb.proactive.examples.dataspaces.hello;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.api.PARemoteObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.remoteobject.RemoteObjectExposer;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extra.dataspaces.BaseScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.InputOutputSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.NamingService;
import org.objectweb.proactive.extra.dataspaces.PADataSpaces;
import org.objectweb.proactive.extra.dataspaces.SpaceInstanceInfo;
import org.objectweb.proactive.extra.dataspaces.Utils;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;
import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


public class HelloExample {

    private static final String HTTP_RESOURCE1_NAME = "wiki_proactive";
    private static final String HTTP_RESOURCE1_URL = "http://en.wikipedia.org/wiki/ProActive";
    private static final String HTTP_RESOURCE2_NAME = "wiki_grid_computing";
    private static final String HTTP_RESOURCE2_URL = "http://en.wikipedia.org/wiki/Grid_computing";

    // FIXME ugly hack to obtain hard coded application id
    private static final long applicationId = Utils.getApplicationId(null);

    private static final String USERNAME = System.getProperty("user.name");
    private static final String USER_HOMEDIR = System.getProperty("user.home");
    private static final String TMP_PATH = "/tmp";
    private static final String SCRATCH_ACCESS_PROTO = "scp://";

    private static final int DESCRIPTOR_FILENAME_ARG = 0;
    private static final String NAME = "DSnamingservice";
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);
    private static final String HOSTNAME = Utils.getHostname();
    private static final String OUTPUT_ACCESS_URL = "";

    private final Set<SpaceInstanceInfo> applicationSpaces = new HashSet<SpaceInstanceInfo>();
    private final BaseScratchSpaceConfiguration scratchSpaceConfigurationRemote;
    private final BaseScratchSpaceConfiguration scratchSpaceConfigurationLocal;
    private final File descriptorFile;

    private NamingService namingService;
    private DataSpacesInstaller localInstaller;
    private RemoteObjectExposer<NamingService> roe;
    private List<Node> nodesGrabbed;
    private Map<Node, DataSpacesInstaller> nodesDSInstallers = new HashMap<Node, DataSpacesInstaller>();
    private GCMApplication applicationDescriptor;

    private void stop() {
        try {
            localInstaller.stopDataSpaces();

            for (Node node : nodesGrabbed) {
                nodesDSInstallers.get(node).stopDataSpaces();
            }
        } catch (NotConfiguredException e) {
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

    private void exampleUsage() throws ActiveObjectCreationException, NodeException, NotConfiguredException,
            IOException, MalformedURIException {

        final Set<String> partialResults = new HashSet<String>();
        final Iterator<Node> nodes = nodesGrabbed.iterator();
        final Node nodeA = nodes.next();

        final ExampleProcessing processingA = (ExampleProcessing) PAActiveObject.newActive(
                ExampleProcessing.class.getName(), null, nodeA);

        final ExampleProcessing processingB = (ExampleProcessing) PAActiveObject.newActive(
                ExampleProcessing.class.getName(), null);

        partialResults.add(processingA.computePartials(HTTP_RESOURCE1_NAME).stringValue());
        partialResults.add(processingB.computePartials(HTTP_RESOURCE2_NAME).stringValue());
        processingB.gatherPartials(partialResults);
    }

    private void start() throws NotConfiguredException, FileSystemException, ProActiveException,
            URISyntaxException {

        final String nsURL = startNamingService();
        final NamingService namingServiceStub = NamingService.createNamingServiceStub(nsURL);

        namingServiceStub.registerApplication(applicationId, applicationSpaces);

        applicationDescriptor = startNodes(nsURL);
        checkEnoughRemoteNodesOrDie(1);

        localInstaller.startDataSpaces(scratchSpaceConfigurationLocal);
        for (Node node : nodesGrabbed) {
            nodesDSInstallers.get(node).startDataSpaces(scratchSpaceConfigurationRemote);
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

    private HelloExample(String[] args) throws ConfigurationException {
        if (args.length != 1) {
            System.out.println("Usage: java " + this.getClass().getName() +
                " <application descriptor filename>");
            System.exit(0);
        }

        final String scratchAccessURL = SCRATCH_ACCESS_PROTO + USERNAME + "@#{hostname}" + TMP_PATH;

        scratchSpaceConfigurationRemote = new BaseScratchSpaceConfiguration(scratchAccessURL, USER_HOMEDIR +
            TMP_PATH + "/remote");
        scratchSpaceConfigurationLocal = new BaseScratchSpaceConfiguration(scratchAccessURL, USER_HOMEDIR +
            TMP_PATH + "/local");

        addInput(HTTP_RESOURCE1_URL, HTTP_RESOURCE1_NAME);
        addInput(HTTP_RESOURCE2_URL, HTTP_RESOURCE2_NAME);

        InputOutputSpaceConfiguration localOutputConfiguration = InputOutputSpaceConfiguration
                .createOutputSpaceConfiguration(OUTPUT_ACCESS_URL, USER_HOMEDIR + TMP_PATH + "/output",
                        HOSTNAME, PADataSpaces.DEFAULT_IN_OUT_NAME);

        SpaceInstanceInfo localOutput = new SpaceInstanceInfo(applicationId, localOutputConfiguration);
        applicationSpaces.add(localOutput);

        descriptorFile = new File(args[DESCRIPTOR_FILENAME_ARG]);
    }

    private void addInput(String http_resource1_url, String http_resource1_name)
            throws ConfigurationException {

        final InputOutputSpaceConfiguration http1Configuration = InputOutputSpaceConfiguration
                .createInputSpaceConfiguration(http_resource1_url, null, null, http_resource1_name);

        final SpaceInstanceInfo inputSpaceHTTP1 = new SpaceInstanceInfo(applicationId, http1Configuration);

        applicationSpaces.add(inputSpaceHTTP1);
    }

    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        HelloExample hw = null;

        try {
            hw = new HelloExample(args);
            hw.start();
            hw.exampleUsage();

            // wait 30 seconds
            Thread.sleep(1000 * 30);

            hw.stop();
        } catch (NotConfiguredException e1) {
            e1.printStackTrace();
        } catch (FileSystemException e1) {
            e1.printStackTrace();
        } catch (ProActiveException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
