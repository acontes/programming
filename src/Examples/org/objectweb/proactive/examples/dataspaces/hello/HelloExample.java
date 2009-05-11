package org.objectweb.proactive.examples.dataspaces.hello;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.vfs.FileSystemException;
import org.apache.log4j.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ObjectForSynchro;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extra.dataspaces.BaseScratchSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.InputOutputSpaceConfiguration;
import org.objectweb.proactive.extra.dataspaces.NamingService;
import org.objectweb.proactive.extra.dataspaces.NamingServiceDeployer;
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
 * Goal of processing: count lines of documents from some sources (here as example - two Wikipedia
 * HTML pages accessed by HTTP) and store results in one file on the local disk.
 * <p>
 * Scenario:
 * <ol>
 * <li>GCM application is deployed, Data Spaces Naming Service is started, Data Spaces are
 * configured on deployed nodes. HTTP input resources are registered as named input data spaces.
 * Output file is registered as a default output data space.</li>
 * <li>Application-processing is delegated in {@link #exampleUsage()}. Two ActiveObjects start their
 * local processing in {@link ExampleProcessing#computePartials(String)} in parallel:
 * <ul>
 * <li>Read document content from specified named input data space</li>
 * <li>Count lines of a read document</li>
 * <li>Store partial result in a file within local scratch</li>
 * <li>Return URI of a file within local scratch containing partial results</li>
 * </ul>
 * </li>
 * <li>The deployer gathers partial URIs of files with partial results into a list and calls
 * {@link ExampleProcessing#gatherPartials(List)} method on one of the AOs, which aggregates
 * results. That method performs:
 * <ul>
 * <li>Read partial results from each specified scratch URI</li>
 * <li>Combine partial results as one list, a final results of the processing</li>
 * <li>Store final results in a file within the default output data space</li>
 * </ul>
 * </li>
 * <li>Data Spaces are deconfigured, GCM application stops.</li>
 * </ol>
 * <p>
 * Note: Data spaces are started manually on processing nodes through AO instances of
 * {@link DataSpacesInstaller} and helper methods of this class, to emulate future integration with
 * deployment mechanisms like GCM.
 * <p>
 * Data spaces are configured as follows:
 * <ul>
 * <li>Input data spaces are defined as HTTP resources
 * <li>Scratch data spaces are located in <code>{@link #SCRATCH_SPACE_PATH}</code> directory with
 * {@link #SCRATCH_SPACE_URL} remote access defined</li>
 * <li>Output data spaces are located in <code>{@link #OUTPUT_SPACE_PATH}</code> directory on the
 * deployer's host. Remote access is defined specifically to {@link #OUTPUT_SPACE_URL}</li>
 * </ul>
 */
public class HelloExample {

    private static final Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    // ### IN TARGET IMPLEMENTATION - ALL THESE CONSTS SHOULD BE IN XML ###

    // name of a host for output data space, here: the deployer host
    private static final String HOSTNAME = Utils.getHostname();

    // remote access protocol specific constants
    private static final String REMOTE_ACCESS_PROTO = "sftp://";
    private static final String USERNAME = System.getProperty("user.name");

    // input data spaces - HTTP resources to process
    private static final String INPUT_RESOURCE1_NAME = "wiki_proactive";
    private static final String INPUT_RESOURCE1_URL = "http://en.wikipedia.org/wiki/ProActive";
    private static final String INPUT_RESOURCE2_NAME = "wiki_grid_computing";
    private static final String INPUT_RESOURCE2_URL = "http://en.wikipedia.org/wiki/Grid_computing";

    // root path for output data space location
    private static final String OUTPUT_SPACE_PATH = System.getProperty("user.home") + "/tmp/output/";
    private static final String OUTPUT_SPACE_URL = REMOTE_ACCESS_PROTO + USERNAME + "@" + HOSTNAME +
        OUTPUT_SPACE_PATH;

    // path scratch data spaces location (used also by remote access protocol)
    private static final String SCRATCH_SPACE_PATH = System.getProperty("java.io.tmpdir") + "/dataspaces";
    private static final String SCRATCH_SPACE_URL = REMOTE_ACCESS_PROTO + USERNAME + "@#{hostname}" +
        SCRATCH_SPACE_PATH;

    // ### END OF XML-LIKE CONFIGURATION ###

    /**
     * @param args
     * @throws URISyntaxException
     * @throws ProActiveException
     * @throws NotConfiguredException
     * @throws IOException
     */
    public static void main(String[] args) throws NotConfiguredException, ProActiveException,
            URISyntaxException, IOException {
        if (args.length != 1) {
            System.out.println("Usage: java " + HelloExample.class.getName() +
                " <application descriptor filename>");
            System.exit(0);
        }

        new HelloExample().run(args[0]);
    }

    // in target implementation that should be job of XML parser
    private static SpaceInstanceInfo createDefaultOutputSpaceInfo(final long appId, final String localPath,
            final String accessURL) throws ConfigurationException {
        final InputOutputSpaceConfiguration localOutputConfiguration = InputOutputSpaceConfiguration
                .createOutputSpaceConfiguration(accessURL, localPath, HOSTNAME,
                        PADataSpaces.DEFAULT_IN_OUT_NAME);
        return new SpaceInstanceInfo(appId, localOutputConfiguration);
    }

    // in target implementation that should be job of XML parser
    private static SpaceInstanceInfo createInputSpaceInfo(final long appId, String name, String accessUrl)
            throws ConfigurationException {
        final InputOutputSpaceConfiguration configuration = InputOutputSpaceConfiguration
                .createInputSpaceConfiguration(accessUrl, null, null, name);
        return new SpaceInstanceInfo(appId, configuration);
    }

    private NamingServiceDeployer namingServiceDeployer;

    private List<Node> nodesDeployed;

    private List<DataSpacesInstaller> dataSpacesInstallers = new ArrayList<DataSpacesInstaller>();

    private GCMApplication gcmApplication;

    /**
     * Deploys application, configures Data Spaces together with NamingService, executes example,
     * and undeploys application and deconfigures everything.
     * 
     * @param descriptorPath
     *            path to deployment descriptor
     * @throws ProActiveException
     * @throws FileSystemException
     * @throws URISyntaxException
     */
    public void run(String descriptorPath) throws ProActiveException, FileSystemException, URISyntaxException {
        try {
            startGCM(descriptorPath);
            startNamingService();
            startDataSpaces();
            exampleUsage();
        } catch (Exception x) {
            logger.error("Error: ", x);
        } finally {
            stop();
        }
    }

    private void stop() {
        stopDataSpaces();
        stopNamingService();
        stopGCM();
        logger.info("Application stopped");
    }

    private void startGCM(String descriptorPath) throws ProActiveException {
        gcmApplication = PAGCMDeployment.loadApplicationDescriptor(new File(descriptorPath));
        gcmApplication.startDeployment();

        final GCMVirtualNode vnode = gcmApplication.getVirtualNode("Hello");
        vnode.waitReady();

        // grab nodes here
        nodesDeployed = vnode.getCurrentNodes();
        logger.info("Nodes started: " + nodesDeployed.size() + " nodes deployed");
    }

    private void stopGCM() {
        if (gcmApplication == null)
            return;
        gcmApplication.kill();
        PALifeCycle.exitSuccess();
    }

    // in target implementation that configuration should be loaded from XML and performed 
    // by GCM deployment engine or scheduler
    private void startDataSpaces() throws FileSystemException, ProActiveException, URISyntaxException {
        logger.debug("Configuring Data Spaces on deployed nodes through DataSpacesInstaller AO");

        // FIXME ugly hack to obtain hard coded application id
        final long appId = Utils.getApplicationId(null);

        // XML parser or GCM role...
        final Set<SpaceInstanceInfo> applicationSpaces = new HashSet<SpaceInstanceInfo>();
        applicationSpaces.add(createInputSpaceInfo(appId, INPUT_RESOURCE1_NAME, INPUT_RESOURCE1_URL));
        applicationSpaces.add(createInputSpaceInfo(appId, INPUT_RESOURCE2_NAME, INPUT_RESOURCE2_URL));
        applicationSpaces.add(createDefaultOutputSpaceInfo(appId, OUTPUT_SPACE_PATH, OUTPUT_SPACE_URL));
        final BaseScratchSpaceConfiguration scratchSpaceConfiguration = new BaseScratchSpaceConfiguration(
            SCRATCH_SPACE_URL, SCRATCH_SPACE_PATH);

        final String namingServiceURL = namingServiceDeployer.getNamingServiceURL();
        final NamingService namingServiceStub = NamingService.createNamingServiceStub(namingServiceURL);
        namingServiceStub.registerApplication(appId, applicationSpaces);

        final List<ObjectForSynchro> synchros = new ArrayList<ObjectForSynchro>();
        for (Node node : nodesDeployed) {
            final DataSpacesInstaller installer = (DataSpacesInstaller) PAActiveObject.newActive(
                    DataSpacesInstaller.class.getName(), null, node);
            dataSpacesInstallers.add(installer);

            final ObjectForSynchro synchro = installer.startDataSpaces(scratchSpaceConfiguration,
                    namingServiceURL);
            synchros.add(synchro);
        }
        PAFuture.waitForAll(synchros);
        logger.info("Data Spaces configured on deployed nodes");
    }

    private void stopDataSpaces() {
        try {
            for (DataSpacesInstaller installer : dataSpacesInstallers)
                installer.stopDataSpaces();
        } catch (NotConfiguredException e) {
            ProActiveLogger.logEatedException(logger, e);
        }
    }

    private void startNamingService() {
        namingServiceDeployer = new NamingServiceDeployer();
        logger.info("Naming Service successfully started on: " + namingServiceDeployer.getNamingServiceURL());
    }

    private void stopNamingService() {
        if (namingServiceDeployer != null) {
            try {
                namingServiceDeployer.terminate();
            } catch (ProActiveException x) {
                ProActiveLogger.logEatedException(logger, x);
            }
            namingServiceDeployer = null;
        }
    }

    // real processing
    private void exampleUsage() throws ActiveObjectCreationException, NodeException, DataSpacesException {
        checkEnoughRemoteNodesOrDie(2);
        final Node nodeA = nodesDeployed.get(0);
        final Node nodeB = nodesDeployed.get(1);

        final ExampleProcessing processingA = (ExampleProcessing) PAActiveObject.newActive(
                ExampleProcessing.class.getName(), null, nodeA);
        final ExampleProcessing processingB = (ExampleProcessing) PAActiveObject.newActive(
                ExampleProcessing.class.getName(), null, nodeB);

        // This can be turned into Set as PROACTIVE-663 will be resolved
        final List<StringWrapper> partialResults = new ArrayList<StringWrapper>();
        try {
            partialResults.add(processingA.computePartials(INPUT_RESOURCE1_NAME));
            partialResults.add(processingB.computePartials(INPUT_RESOURCE2_NAME));
        } catch (IOException x) {
            logger.error("Could not store partial results", x);
            return;
        }

        try {
            processingB.gatherPartials(partialResults);
        } catch (IOException x) {
            logger.error("Could not write final results file", x);
        }
    }

    private void checkEnoughRemoteNodesOrDie(int i) throws IllegalStateException {
        if (nodesDeployed.size() < i) {
            logger.error("Not enough nodes to run example");
            throw new IllegalStateException("Not enough nodes to run example");
        }
    }
}
