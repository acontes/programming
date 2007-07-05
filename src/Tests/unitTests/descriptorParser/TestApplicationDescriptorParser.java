package unitTests.descriptorParser;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.proactive.extra.gcmdeployment.GCMApplication.GCMApplicationParserImpl;

public class TestApplicationDescriptorParser {

    @Test
    public void test() {
        
        File descriptor = new File("/home/glaurent/workspace/GCMDeployment/src/Extra/org/objectweb/proactive/extra/ressourceallocator/testfiles/application_ProActive_MS_basic.xml");
        
        try {
            GCMApplicationParserImpl parser = new GCMApplicationParserImpl(descriptor);
            
            parser.getCommandBuilder();
            parser.getVirtualNodes();
            parser.getResourceProviders();
            
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
}
