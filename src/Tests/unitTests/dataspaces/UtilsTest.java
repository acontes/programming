package unitTests.dataspaces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.objectweb.proactive.extra.dataspaces.Utils;
import org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException;


public class UtilsTest {
    @Test
    public void testGetLocalAccessURLMatchingHostname()
            throws org.objectweb.proactive.extra.dataspaces.exceptions.ConfigurationException {
        final String hostname = Utils.getHostname();
        assertEquals("file:///local", Utils.getLocalAccessURL("http://remote/", "/local", hostname));
    }

    @Test
    public void testGetLocalAccessURLNonMatchingHostname() throws ConfigurationException {
        final String hostname = Utils.getHostname() + "haha";
        assertEquals("http://remote/", Utils.getLocalAccessURL("http://remote/", "/local", hostname));
    }

    @Test
    public void testGetLocalAccessURLNoLocalPath() throws ConfigurationException {
        assertEquals("http://remote/", Utils.getLocalAccessURL("http://remote/", null, null));
    }

    @Test
    public void testAppendSubDirsNoBaseLocation() throws Exception {
        assertNull(Utils.appendSubDirs(null, "abc"));
    }

    @Test
    public void testAppendSubDirsNoBaseNoSubDir() throws Exception {
        assertNull(Utils.appendSubDirs(null));
    }

    @Test
    public void testAppendSubDirsBaseNoSlashNoSubDir() throws Exception {
        assertEquals("/abc", Utils.appendSubDirs("/abc"));
    }

    @Test
    public void testAppendSubDirsBaseSlash1SubDir() throws Exception {
        assertEquals("/abc/1", Utils.appendSubDirs("/abc/", "1"));
    }

    @Test
    public void testAppendSubDirsBaseNoSlash1SubDir() throws Exception {
        assertEquals("/abc/1", Utils.appendSubDirs("/abc", "1"));
    }

    @Test
    public void testAppendSubDirsBaseAsURL1SubDir() throws Exception {
        assertEquals("http://test.com/1", Utils.appendSubDirs("http://test.com/", "1"));
    }

    @Test
    public void testAppendSubDirsBaseNoSlash2SubDir() throws Exception {
        assertEquals("/abc/1/2", Utils.appendSubDirs("/abc", "1", "2"));
    }
}
