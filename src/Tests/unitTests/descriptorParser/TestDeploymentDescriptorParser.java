package unitTests.descriptorParser;

import java.io.File;
import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.GCMDeploymentParserImpl;

public class TestDeploymentDescriptorParser {

    @Test
    public void test() {
        
        File descriptor = new File("/home/glaurent/workspace/GCMDeployment/src/Extra/org/objectweb/proactive/extra/ressourceallocator/testfiles/deployment.xml");
        
        try {
            GCMDeploymentParserImpl parser = new GCMDeploymentParserImpl(descriptor);

            parser.parseEnvironment();
            parser.parseResources();
            parser.parseInfrastructure();
            
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
