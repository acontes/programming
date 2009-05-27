package functionalTests.dataspaces;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.xml.VariableContractType;
import org.objectweb.proactive.extra.dataspaces.NamingServiceDeployer;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

import functionalTests.FunctionalTest;
import functionalTests.GCMFunctionalTest;


@Ignore
public class GCMFunctionalTestDataSpaces extends GCMFunctionalTest {

    static final private URL dataSpacesApplicationDescriptor = FunctionalTest.class
            .getResource("/functionalTests/_CONFIG/JunitAppDataSpaces.xml");

    static public final String VN_NAME = "nodes";
    static public final String VAR_DEPDESCRIPTOR = "deploymentDescriptor";
    static public final String VAR_JVMARG = "jvmargDefinedByTest";

    static public final String VAR_HOSTCAPACITY = "hostCapacity";
    int hostCapacity;

    static public final String VAR_VMCAPACITY = "vmCapacity";
    int vmCapacity;

    static public final String VAR_NAMING_SERVICE_URL = "NAMING_SERVICE_URL";
    NamingServiceDeployer namingServiceDeployer;

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
    }

    @Before
    public void tryStartNamingService() {
        if (namingServiceDeployer == null)
            namingServiceDeployer = new NamingServiceDeployer();
    }

    @After
    public void tryStopNamingService() throws ProActiveException {
        if (namingServiceDeployer != null) {
            namingServiceDeployer.terminate();
            namingServiceDeployer = null;
        }
    }

    @Before
    public void createInputOutputSpacesContent() {
        // TODO
    }

    @Before
    public void removeInputOutputSpacesContent() {
        // TODO
    }

    public Node getANode() {
        return getANodeFrom(VN_NAME);
    }

    private Node getANodeFrom(String vnName) {
        if (gcmad == null || !gcmad.isStarted()) {
            throw new IllegalStateException("deployment is not started");
        }

        GCMVirtualNode vn = gcmad.getVirtualNode(vnName);
        return vn.getANode();
    }
}
