package unitTests.descriptorParser;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.objectweb.proactive.extra.gcmdeployment.GCMApplication.GCMApplicationParserImpl;


public class TestApplicationDescriptorParser {
    @Test
    public void test() throws IOException {
        File descriptor = new File(getClass()
                                       .getResource("/unitTests/descriptorParser/testfiles/application_ProActive_MS_basic.xml")
                                       .getFile());

        GCMApplicationParserImpl parser = new GCMApplicationParserImpl(descriptor);

        parser.getCommandBuilder();
        parser.getVirtualNodes();
        parser.getResourceProviders();
    }
}
