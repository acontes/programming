package functionalTests.dataspaces;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.xml.VariableContractType;
import org.objectweb.proactive.extensions.calcium.system.SkeletonSystemImpl;
import org.objectweb.proactive.extra.dataspaces.NamingServiceDeployer;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

import functionalTests.FunctionalTest;
import functionalTests.GCMFunctionalTest;


/**
 * Base of functional tests for Data Spaces. This class deploys Data Spaces with Naming Service,
 * prepares input and output spaces: default input, default output, named input (name
 * {@value #INPUT_NAME}) and named output (name {@value #OUTPUT_NAME}). Both output spaces are
 * empty, while both input spaces contain file {@value #INPUT_FILE_NAME} with
 * {@value #INPUT_FILE_CONTENT}. Scratch space is also defined for each of available Node.
 * <p>
 * Test class uses local paths to access data spaces.
 */
@Ignore
public class GCMFunctionalTestDataSpaces extends GCMFunctionalTest {

    static final private URL dataSpacesApplicationDescriptor = FunctionalTest.class
            .getResource("/functionalTests/_CONFIG/JunitAppDataSpaces.xml");

    static public final String VN_NAME = "nodes";
    static public final String INPUT_NAME = "named_input";
    static public final String INPUT_FILE_NAME = "test.txt";
    static public final String INPUT_FILE_CONTENT = "toto";
    static public final String OUTPUT_NAME = "named_output";

    static public final String VAR_DEPDESCRIPTOR = "deploymentDescriptor";
    static public final String VAR_JVMARG = "jvmargDefinedByTest";

    static public final String VAR_HOSTCAPACITY = "hostCapacity";
    int hostCapacity;

    static public final String VAR_VMCAPACITY = "vmCapacity";
    int vmCapacity;

    static public final String VAR_NAMING_SERVICE_URL = "NAMING_SERVICE_URL";
    NamingServiceDeployer namingServiceDeployer;

    File rootTmpDir;
    static public final String VAR_INPUT_DEFAULT_PATH = "INPUT_DEFAULT_PATH";
    File inputDefaultDir;
    static public final String VAR_INPUT_PATH = "INPUT_PATH";
    File inputDir;
    static public final String VAR_OUTPUT_DEFAULT_PATH = "OUTPUT_DEFAULT_PATH";
    File outputDefaultDir;
    static public final String VAR_OUTPUT_PATH = "OUTPUT_PATH";
    File outputDir;

    private static void createInputContent(File dir) throws IOException {
        assertTrue(dir.mkdirs());
        final File file = new File(dir, INPUT_FILE_NAME);
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(INPUT_FILE_CONTENT);
        writer.close();
    }

    public GCMFunctionalTestDataSpaces(int hostCapacity, int vmCapacity) {
        super(dataSpacesApplicationDescriptor);
        this.hostCapacity = hostCapacity;
        this.vmCapacity = vmCapacity;
        vContract.setVariableFromProgram(VAR_HOSTCAPACITY, Integer.valueOf(hostCapacity).toString(),
                VariableContractType.DescriptorDefaultVariable);
        vContract.setVariableFromProgram(VAR_VMCAPACITY, Integer.valueOf(vmCapacity).toString(),
                VariableContractType.DescriptorDefaultVariable);

        // hack: need to do that here to acquire proper URL
        tryStartNamingService();
        vContract.setVariableFromProgram(VAR_NAMING_SERVICE_URL, namingServiceDeployer.getNamingServiceURL(),
                VariableContractType.ProgramVariable);

        rootTmpDir = new File(System.getProperty("java.io.tmpdir"), "ProActive-GCMFunctionalTestDataSpaces");
        inputDefaultDir = new File(rootTmpDir, "inputDefault");
        inputDir = new File(rootTmpDir, "input");
        outputDefaultDir = new File(rootTmpDir, "outputDefault");
        outputDir = new File(rootTmpDir, "output");

        vContract.setVariableFromProgram(VAR_INPUT_DEFAULT_PATH, inputDefaultDir.getAbsolutePath(),
                VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_INPUT_PATH, inputDir.getAbsolutePath(),
                VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_OUTPUT_DEFAULT_PATH, outputDefaultDir.getAbsolutePath(),
                VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_OUTPUT_PATH, outputDir.getAbsolutePath(),
                VariableContractType.ProgramVariable);
    }

    @Before
    public void tryStartNamingService() {
        if (namingServiceDeployer == null)
            namingServiceDeployer = new NamingServiceDeployer();
    }

    @After
    public void killGCMAAndNamingService() throws ProActiveException {
        // COPIED AND MODIFIED FROM SUPERCLASS - to enforce appropriate order of actions   
        logger.info(GCMFunctionalTest.class.getName() + " @After: killDeployment");
        if (gcmad != null) {
            gcmad.kill();
            gcmad = null;
        }
        logger.info(GCMFunctionalTest.class.getName() + " @After: killDeployment");
        // END OF COPIED AND MODIFIED PART

        if (namingServiceDeployer != null) {
            namingServiceDeployer.terminate();
            namingServiceDeployer = null;
        }
    }

    @Before
    public void createInputOutputSpacesContent() throws IOException {
        createInputContent(inputDir);
        createInputContent(inputDefaultDir);

        assertTrue(outputDir.mkdirs());
        assertTrue(outputDefaultDir.mkdirs());
    }

    @Before
    public void removeInputOutputSpacesContent() {
        if (rootTmpDir.exists())
            assertTrue(SkeletonSystemImpl.deleteDirectory(rootTmpDir));
    }

    protected Node getANode() {
        checkDeploymentState();

        GCMVirtualNode vn = gcmad.getVirtualNode(VN_NAME);
        return vn.getANode();
    }

    private void checkDeploymentState() {
        if (gcmad == null || !gcmad.isStarted()) {
            throw new IllegalStateException("deployment is not started");
        }
    }
}
