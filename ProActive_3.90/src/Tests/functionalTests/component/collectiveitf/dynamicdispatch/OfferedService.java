package functionalTests.component.collectiveitf.dynamicdispatch;

import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;


public interface OfferedService {

    public Result execute(Task t);

}
