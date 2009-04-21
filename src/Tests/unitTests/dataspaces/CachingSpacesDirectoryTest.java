package unitTests.dataspaces;

import org.objectweb.proactive.extra.dataspaces.CachingSpacesDirectory;
import org.objectweb.proactive.extra.dataspaces.SpacesDirectory;
import org.objectweb.proactive.extra.dataspaces.SpacesDirectoryImpl;

public class CachingSpacesDirectoryTest extends SpacesDirectoryAbstractTest {

	@Override
	protected SpacesDirectory getSource() throws Exception {
		return new CachingSpacesDirectory(new SpacesDirectoryImpl());
	}
}
