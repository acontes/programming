package functionalTests.component.collectiveitf.reduction.primitive;

import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;


public class ServerImpl implements OfferedService,
        functionalTests.component.collectiveitf.multicast.Identifiable {

    String id;

    public IntWrapper method1(Integer parameter) {
        return new IntWrapper(parameter);
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;

    }

}
