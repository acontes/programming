package unitTests.descriptorParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.objectweb.proactive.extra.gcmdeployment.GCMApplication.GCMApplicationParserImpl;


public class TestApplicationDescriptorParser {
    final static String TEST_APP_DIR = TestApplicationDescriptorParser.class.getClass()
                                                                            .getResource("/unitTests/descriptorParser/testfiles/application")
                                                                            .getFile();

    @Test
    public void test() throws IOException {
        for (File descriptor : getApplicationDescriptors()) {
            GCMApplicationParserImpl parser = new GCMApplicationParserImpl(descriptor);

            parser.getCommandBuilder();
            parser.getVirtualNodes();
            parser.getResourceProviders();
        }
    }

    private List<File> getApplicationDescriptors() {
        List<File> ret = new ArrayList<File>();
        File dir = new File(TEST_APP_DIR);

        for (String file : dir.list()) {
            ret.add(new File(dir, file));
        }
        return ret;
    }
}
