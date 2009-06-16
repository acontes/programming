package unitTests.dataspaces;

import org.objectweb.proactive.extra.dataspaces.core.naming.SpacesDirectory;
import org.objectweb.proactive.extra.dataspaces.core.naming.SpacesDirectoryImpl;


public class SpacesDirectoryImplTest extends SpacesDirectoryAbstractBase {

    @Override
    protected SpacesDirectory getSource() {
        return new SpacesDirectoryImpl();
    }
}
