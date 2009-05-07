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
import org.objectweb.proactive.extra.dataspaces.exceptions.DataSpacesException;
import org.objectweb.proactive.extra.dataspaces.exceptions.NotConfiguredException;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


/**
 * Simple example of how to use Data Spaces in ProActive processing.
 * <p>
 * Goal of processing: count lines of documents from HTTP source (e.g. Wikipedia site) and store
 * results in one file on the local disk.
 * <p>
 * Scenario:
 * <ol>
 * <li>HTTP resources are registered as named input data spaces. Output file is registered as a
 * default output data space. All the registration is performed through
 * {@link NamingService#registerApplication(long, Set)} call.</li>
 * <li>Two ActiveObjects start their local processing
 * {@link ExampleProcessing#computePartials(String)} in parallel:
 * <ul>
 * <li>Read document content from specified named input data space</li>
 * <li>Count lines of a read document</li>
 * <li>Store partial result in a local scratch</li>
 * <li>Return URI of a local scratch containing file with partial results</li>
 * </ul>
 * </li>
 * <li>The deployer gathers scratch URIs with partial results into a set and calls
 * {@link ExampleProcessing#gatherPartials(Set)} method on one of the AOs. That method performs:
 * <ul>
 * <li>Read partial results from each specified scratch URI</li>
 * <li>Combine partial results as one list, a final results of the processing</li>
 * <li>Store final results in a file within the default output data space</li>
 * </ul>
 * </li>
 * </ol>
 * <p>
 * Note: Data spaces are started manually on processing nodes through AO instances of
 * {@link DataSpacesInstaller}.
 * <p>
 * Data spaces are configured as follows:
 * <ul>
 * <li>Input data spaces are defined as HTTP resources
 * <li>Scratch data spaces are located in <code>{@link #SCRATCH_DATA_SPACE_PATH}</code> directory
 * with {@link #REMOTE_ACCESS_PROTO} specific remote access defined</li>
 * <li>Output data spaces are located in <code>{@link #OUTPUT_DATA_SPACE_PATH}</code> directory on
 * the deployer's host. Remote access is defined specificly to {@link #REMOTE_ACCESS_PROTO}</li>
 * </ul>
 */
public class HelloExample {

    private static final Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    // input data spaces - HTTP resources to process
    private static final String HTTP_RESOURCE1_NAME = "wiki_proactive";
    private static final String HTTP_RESOURCE1_URL = "http://en.wikipedia.org/wiki/ProActive";
    private static final String HTTP_RESOURCE2_NAME = "wiki_grid_computing";
    private static final String HTTP_RESOURCE2_URL = "http://en.wikipedia.org/wiki/Grid_computing";

    // root path for output data space location
    private static final String OUTPUT_DATA_SPACE_PATH = System.getProperty("user.home") + "/tmp/output/";

    // path scratch data spaces location (used also by remote access protocol)
    private static final String SCRATCH_DATA_SPACE_PATH = System.getProperty("java.io.tmpdir") +
        "/dataspaces";

    // remote access protocol specific constants
    private static final String REMOTE_ACCESS_PROTO = "sftp://";
    private static final String USERNAME = System.getProperty("user.name");

    // name of a host for output data space, here: the deployer host
    private static final String HOSTNAME = Utils.getHostname();

    private static final int DESCRIPTOR_FILENAME_ARG = 0;

    private static final String NAMING_SERVICE_NAME = "DSnamingservice";

    // FIXME ugly hack to obtain hard coded application id
    private static final long applicationId = Utils.getApplicationId(null);

    private final Set<SpaceInstanceInfo> applicationSpaces = new HashSet<SpaceInstanceInfo>();

    private final BaseScratchSpaceConfiguration scratchSpaceConfiguration;

    private final File descriptorFile;

    private NamingService namingService;

    private RemoteObjectExposer<NamingService> roe;

    private List<Node> nodesGrabbed;

    private Map<Node, DataSpacesInstaller> nodesDSInstallers = new HashMap<Node, DataSpacesInstaller>();

    private GCMApplication applicationDescriptor;

    private void stop() {
        try {
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

    private void exampleUsage() throws ActiveObjectCreationException, NodeException, IOException,
            DataSpacesException {

        final Set<String> partialResults = new HashSet<String>();
        final Iterator<Node> nodes = nodesGrabbed.iterator();
        final Node nodeA = nodes.next();
        final Node nodeB = nodes.next();

        final ExampleProcessing processingA = (ExampleProcessing) PAActiveObject.newActive(
                ExampleProcessing.class.getName(), null, nodeA);

        final ExampleProcessing processingB = (ExampleProcessing) PAActiveObject.newActive(
                ExampleProcessing.class.getName(), null, nodeB);

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
        checkEnoughRemoteNodesOrDie(2);

        for (Node node : nodesGrabbed) {
            nodesDSInstallers.get(node).startDataSpaces(scratchSpaceConfiguration);
        }
    }

    private void checkEnoughRemoteNodesOrDie(int i) {
        if (nodesGrabbed.size() < i) {
            logger.error("Not enough nodes to run example");
            System.exit(0);
        }
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

        logger.info("Nodes with data spaces installers started: " + nodesGrabbed.size() + " nodes grabbed");
        return applicationDescriptor;
    }

    private String startNamingService() {
        namingService = new NamingService();

        roe = PARemoteObject.newRemoteObject(NamingService.class.getName(), namingService);
        roe.createRemoteObject(NAMING_SERVICE_NAME);

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
        descriptorFile = new File(args[DESCRIPTOR_FILENAME_ARG]);

        final String scratchLocalPath = SCRATCH_DATA_SPACE_PATH;
        final String scratchAccessURL = REMOTE_ACCESS_PROTO + USERNAME + "@#{hostname}" + scratchLocalPath;

        final String outputLocalPath = OUTPUT_DATA_SPACE_PATH;
        final String outputAccessURL = REMOTE_ACCESS_PROTO + USERNAME + "@" + HOSTNAME + outputLocalPath;

        scratchSpaceConfiguration = new BaseScratchSpaceConfiguration(scratchAccessURL, scratchLocalPath);
        addInput(HTTP_RESOURCE1_URL, HTTP_RESOURCE1_NAME);
        addInput(HTTP_RESOURCE2_URL, HTTP_RESOURCE2_NAME);
        addDefaultOutput(outputLocalPath, outputAccessURL);
    }

    private void addDefaultOutput(final String outputLocalPath, final String outputAccessURL)
            throws ConfigurationException {
        final InputOutputSpaceConfiguration localOutputConfiguration = InputOutputSpaceConfiguration
                .createOutputSpaceConfiguration(outputAccessURL, outputLocalPath, HOSTNAME,
                        PADataSpaces.DEFAULT_IN_OUT_NAME);

        final SpaceInstanceInfo localOutput = new SpaceInstanceInfo(applicationId, localOutputConfiguration);
        applicationSpaces.add(localOutput);
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
     * @throws URISyntaxException
     * @throws ProActiveException
     * @throws NotConfiguredException
     * @throws IOException
     */
    public static void main(String[] args) throws NotConfiguredException, ProActiveException,
            URISyntaxException, IOException {

        HelloExample hw = null;

        try {
            hw = new HelloExample(args);
            hw.start();
            hw.exampleUsage();
        } finally {
            if (hw != null)
                hw.stop();
        }
    }
}
