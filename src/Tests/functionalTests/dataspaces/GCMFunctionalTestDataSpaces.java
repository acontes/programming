package functionalTests.dataspaces;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.xml.VariableContractType;
import org.objectweb.proactive.extensions.calcium.system.SkeletonSystemImpl;
import org.objectweb.proactive.extra.vfsprovider.client.ProActiveFileName;
import org.objectweb.proactive.extra.vfsprovider.server.FileSystemServerDeployer;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

import functionalTests.FunctionalTest;
import functionalTests.GCMFunctionalTest;


/**
 * Base of functional tests for Data Spaces. This class deploys Data Spaces with Naming Service,
 * prepares input and output spaces:
 * <ul>
 * <li>default input - existing directory containing file {@link #INPUT_FILE_NAME}</li>
 * <li>named input {@link #INPUT_WITH_DIR_NAME} - existing directory containing file
 * {@link #INPUT_FILE_NAME} with content {@link #INPUT_FILE_CONTENT}</li>
 * <li>named input {@link #INPUT_WITH_FILE_NAME} - existing file with content
 * {@link #INPUT_FILE_CONTENT}</li>
 * <li>default output - existing empty directory</li>
 * <li>named output {@link #OUTPUT_WITH_DIR_NAME} - existing empty directory</li>
 * <li>named output {@link #OUTPUT_WITH_NOTHING_NAME} - non-existing file/directory, that should
 * have possibility to be created</li>
 * </ul>
 * Scratch space is also defined for each of available Node.
 * <p>
 * Test class uses local paths to access data spaces.
 */
@Ignore
public class GCMFunctionalTestDataSpaces extends GCMFunctionalTest {

    static final private URL dataSpacesApplicationDescriptor = FunctionalTest.class
            .getResource("/functionalTests/dataspaces/JunitAppDataSpaces.xml");

    static public final String VN_NAME = "nodes";
    static public final String INPUT_WITH_DIR_NAME = "input_with_dir";
    static public final String INPUT_WITH_FILE_NAME = "input_with_file";
    static public final String INPUT_FILE_NAME = "test.txt";
    static public final String INPUT_FILE_CONTENT = "toto";
    static public final String OUTPUT_WITH_DIR_NAME = "output_with_dir";
    static public final String OUTPUT_WITH_FILE_NAME = "output_with_file";
    static public final String OUTPUT_WITH_NOTHING1_NAME = "output_with_nothing1";
    static public final String OUTPUT_WITH_NOTHING2_NAME = "output_with_nothing2";

    static public final String VAR_DEPDESCRIPTOR = "deploymentDescriptor";
    static public final String VAR_JVMARG = "jvmargDefinedByTest";

    static public final String VAR_HOSTCAPACITY = "hostCapacity";
    int hostCapacity;

    static public final String VAR_VMCAPACITY = "vmCapacity";
    int vmCapacity;

    File rootTmpDir;
    static public final String VAR_INPUT_DEFAULT_WITH_DIR_URL = "INPUT_DEFAULT_WITH_DIR_URL";
    File inputDefaultWithDirLocalHandle;
    static public final String VAR_INPUT_WITH_DIR_URL = "INPUT_WITH_DIR_URL";
    File inputWithDirLocalHandle;
    static public final String VAR_INPUT_WITH_FILE_URL = "INPUT_WITH_FILE_URL";
    File inputWithFileLocalHandle;
    static public final String VAR_OUTPUT_DEFAULT_WITH_DIR_URL = "OUTPUT_DEFAULT_WITH_DIR_URL";
    File outputDefaultWithDirLocalHandle;
    static public final String VAR_OUTPUT_WITH_DIR_URL = "OUTPUT_WITH_DIR_URL";
    File outputWithDirLocalHandle;
    static public final String VAR_OUTPUT_WITH_FILE_URL = "OUTPUT_WITH_FILE_URL";
    File outputWithFileLocalHandle;
    static public final String VAR_OUTPUT_WITH_NOTHING1_URL = "OUTPUT_WITH_NOTHING1_URL";
    File outputWithNothing1LocalHandle;
    static public final String VAR_OUTPUT_WITH_NOTHING2_URL = "OUTPUT_WITH_NOTHING2_URL";
    File outputWithNothing2LocalHandle;

    private FileSystemServerDeployer fileSystemServerDeployer;
    private final String fileSystemServerRootURL;

    private static void createInputDirContent(File dir) throws IOException {
        assertTrue(dir.mkdirs());
        final File file = new File(dir, INPUT_FILE_NAME);
        createInputFileContent(file);
    }

    private static void createInputFileContent(final File file) throws IOException {
        final File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            assertTrue(parentFile.mkdirs());
        }
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(INPUT_FILE_CONTENT);
        writer.close();
    }

    public GCMFunctionalTestDataSpaces(int hostCapacity, int vmCapacity) throws URISyntaxException,
            IOException {
        super(dataSpacesApplicationDescriptor);
        this.hostCapacity = hostCapacity;
        this.vmCapacity = vmCapacity;
        vContract.setVariableFromProgram(VAR_HOSTCAPACITY, Integer.valueOf(hostCapacity).toString(),
                VariableContractType.DescriptorDefaultVariable);
        vContract.setVariableFromProgram(VAR_VMCAPACITY, Integer.valueOf(vmCapacity).toString(),
                VariableContractType.DescriptorDefaultVariable);

        rootTmpDir = new File(System.getProperty("java.io.tmpdir"), "ProActive-GCMFunctionalTestDataSpaces");
        // hacks to get URL here
        tryStartFileSystemServer();
        fileSystemServerRootURL = ProActiveFileName.getServerVFSRootURL(fileSystemServerDeployer
                .getRemoteFileSystemServerURL());

        inputDefaultWithDirLocalHandle = new File(rootTmpDir, "inputDefaultWithDir");
        inputWithDirLocalHandle = new File(rootTmpDir, "inputWithDir");
        inputWithFileLocalHandle = new File(rootTmpDir, "inputWithFile");
        outputDefaultWithDirLocalHandle = new File(rootTmpDir, "outputDefaultWithDir");
        outputWithDirLocalHandle = new File(rootTmpDir, "outputWithDir");
        outputWithFileLocalHandle = new File(rootTmpDir, "outputWithFile");
        outputWithNothing1LocalHandle = new File(rootTmpDir, "outputWithNothing1");
        outputWithNothing2LocalHandle = new File(rootTmpDir, "outputWithNothing2");

        vContract.setVariableFromProgram(VAR_INPUT_DEFAULT_WITH_DIR_URL,
                getRootSubdirURL(inputDefaultWithDirLocalHandle), VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_INPUT_WITH_DIR_URL, getRootSubdirURL(inputWithDirLocalHandle),
                VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_INPUT_WITH_FILE_URL, getRootSubdirURL(inputWithFileLocalHandle),
                VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_OUTPUT_DEFAULT_WITH_DIR_URL,
                getRootSubdirURL(outputDefaultWithDirLocalHandle), VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_OUTPUT_WITH_DIR_URL, getRootSubdirURL(outputWithDirLocalHandle),
                VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_OUTPUT_WITH_FILE_URL,
                getRootSubdirURL(outputWithFileLocalHandle), VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_OUTPUT_WITH_NOTHING1_URL,
                getRootSubdirURL(outputWithNothing1LocalHandle), VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_OUTPUT_WITH_NOTHING2_URL,
                getRootSubdirURL(outputWithNothing2LocalHandle), VariableContractType.ProgramVariable);
    }

    @Before
    public void createInputOutputSpacesContent() throws IOException {
        createInputDirContent(inputDefaultWithDirLocalHandle);
        createInputDirContent(inputWithDirLocalHandle);
        createInputFileContent(inputWithFileLocalHandle);

        assertTrue(outputDefaultWithDirLocalHandle.mkdirs());
        assertTrue(outputWithDirLocalHandle.mkdirs());
        assertTrue(outputWithFileLocalHandle.createNewFile());
        assertFalse(outputWithNothing1LocalHandle.exists());
        assertFalse(outputWithNothing2LocalHandle.exists());
    }

    @After
    public void removeInputOutputSpacesContent() {
        if (rootTmpDir.exists())
            assertTrue(SkeletonSystemImpl.deleteDirectory(rootTmpDir));
    }

    @Before
    public void tryStartFileSystemServer() throws IOException {
        if (fileSystemServerDeployer == null) {
            rootTmpDir.mkdirs();
            assertTrue(rootTmpDir.exists());
            fileSystemServerDeployer = new FileSystemServerDeployer(
                "ProActive-GCMFunctionalTestDataSpaces/fileSystemServer", rootTmpDir.getAbsolutePath());
        }
    }

    @After
    public void tryStopFileSystemServer() throws ProActiveException {
        // we stop it before stopping GCM application, but it shouldn't cause problems
        if (fileSystemServerDeployer != null) {
            fileSystemServerDeployer.terminate();
            fileSystemServerDeployer = null;
        }
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

    protected String getRootSubdirURL(final File dir) {
        return fileSystemServerRootURL + dir.getName();
    }
}
