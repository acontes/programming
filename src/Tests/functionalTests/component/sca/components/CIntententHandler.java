package functionalTests.component.sca.components;

import org.objectweb.proactive.extensions.component.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.component.sca.control.IntentJoinPoint;


public class CIntententHandler implements IntentHandler {

    public CIntententHandler() {

    }

    @Override
    public Object invoke(IntentJoinPoint Ijpt) throws Throwable {
        return Ijpt.proceed();
    }

}
