package unitTests.descriptorParser;

import java.io.File;
import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.GCMDeploymentParserImpl;


public class TestDeploymentDescriptorParser {
    @Test
    public void test() throws IOException, XPathExpressionException {
        File descriptor = new File(getClass()
                                       .getResource("/unitTests/descriptorParser/testfiles/deployment.xml")
                                       .getFile());

        GCMDeploymentParserImpl parser = new GCMDeploymentParserImpl(descriptor);

        parser.parseEnvironment();
        parser.parseResources();
        parser.parseInfrastructure();
    }
}
