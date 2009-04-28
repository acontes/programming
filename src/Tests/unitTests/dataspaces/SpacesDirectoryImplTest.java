package unitTests.dataspaces;

import org.objectweb.proactive.extra.dataspaces.SpacesDirectory;
import org.objectweb.proactive.extra.dataspaces.SpacesDirectoryImpl;


public class SpacesDirectoryImplTest extends SpacesDirectoryAbstractBase {

    @Override
    protected SpacesDirectory getSource() {
        return new SpacesDirectoryImpl();
    }
}
