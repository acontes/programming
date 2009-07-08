package functionalTests.dataspaces;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.xml.VariableContractType;
import org.objectweb.proactive.extensions.calcium.system.SkeletonSystemImpl;
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
    static public final String VAR_INPUT_DEFAULT_WITH_DIR_PATH = "INPUT_DEFAULT_WITH_DIR_PATH";
    File inputDefaultWithDirLocalHandle;
    static public final String VAR_INPUT_WITH_DIR_PATH = "INPUT_WITH_DIR_PATH";
    File inputWithDirLocalHandle;
    static public final String VAR_INPUT_WITH_FILE_PATH = "INPUT_WITH_FILE_PATH";
    File inputWithFileLocalHandle;
    static public final String VAR_OUTPUT_DEFAULT_WITH_DIR_PATH = "OUTPUT_DEFAULT_WITH_DIR_PATH";
    File outputDefaultWithDirLocalHandle;
    static public final String VAR_OUTPUT_WITH_DIR_PATH = "OUTPUT_WITH_DIR_PATH";
    File outputWithDirLocalHandle;
    static public final String VAR_OUTPUT_WITH_FILE_PATH = "OUTPUT_WITH_FILE_PATH";
    File outputWithFileLocalHandle;
    static public final String VAR_OUTPUT_WITH_NOTHING1_PATH = "OUTPUT_WITH_NOTHING1_PATH";
    File outputWithNothing1LocalHandle;
    static public final String VAR_OUTPUT_WITH_NOTHING2_PATH = "OUTPUT_WITH_NOTHING2_PATH";
    File outputWithNothing2LocalHandle;

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

    public GCMFunctionalTestDataSpaces(int hostCapacity, int vmCapacity) {
        super(dataSpacesApplicationDescriptor);
        this.hostCapacity = hostCapacity;
        this.vmCapacity = vmCapacity;
        vContract.setVariableFromProgram(VAR_HOSTCAPACITY, Integer.valueOf(hostCapacity).toString(),
                VariableContractType.DescriptorDefaultVariable);
        vContract.setVariableFromProgram(VAR_VMCAPACITY, Integer.valueOf(vmCapacity).toString(),
                VariableContractType.DescriptorDefaultVariable);

        rootTmpDir = new File(System.getProperty("java.io.tmpdir"), "ProActive-GCMFunctionalTestDataSpaces");
        inputDefaultWithDirLocalHandle = new File(rootTmpDir, "inputDefaultWithDir");
        inputWithDirLocalHandle = new File(rootTmpDir, "inputWithDir");
        inputWithFileLocalHandle = new File(rootTmpDir, "inputWithFile");
        outputDefaultWithDirLocalHandle = new File(rootTmpDir, "outputDefaultWithDir");
        outputWithDirLocalHandle = new File(rootTmpDir, "outputWithDir");
        outputWithFileLocalHandle = new File(rootTmpDir, "outputWithFile");
        outputWithNothing1LocalHandle = new File(rootTmpDir, "outputWithNothing1");
        outputWithNothing2LocalHandle = new File(rootTmpDir, "outputWithNothing2");

        vContract.setVariableFromProgram(VAR_INPUT_DEFAULT_WITH_DIR_PATH, inputDefaultWithDirLocalHandle
                .getAbsolutePath(), VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_INPUT_WITH_DIR_PATH, inputWithDirLocalHandle.getAbsolutePath(),
                VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_INPUT_WITH_FILE_PATH,
                inputWithFileLocalHandle.getAbsolutePath(), VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_OUTPUT_DEFAULT_WITH_DIR_PATH, outputDefaultWithDirLocalHandle
                .getAbsolutePath(), VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_OUTPUT_WITH_DIR_PATH,
                outputWithDirLocalHandle.getAbsolutePath(), VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_OUTPUT_WITH_FILE_PATH, outputWithFileLocalHandle
                .getAbsolutePath(), VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_OUTPUT_WITH_NOTHING1_PATH, outputWithNothing1LocalHandle
                .getAbsolutePath(), VariableContractType.ProgramVariable);
        vContract.setVariableFromProgram(VAR_OUTPUT_WITH_NOTHING2_PATH, outputWithNothing2LocalHandle
                .getAbsolutePath(), VariableContractType.ProgramVariable);
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
