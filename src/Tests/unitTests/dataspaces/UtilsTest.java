package unitTests.dataspaces;

import static org.junit.Assert.assertEquals;

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
}
