package functionalTests.component.sca.components;

import org.objectweb.proactive.extensions.component.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.component.sca.control.IntentJoinPoint;


public class CIntententHandler implements IntentHandler {

    public CIntententHandler() {

    }

    @Override
    public Object invoke(IntentJoinPoint Ijpt) throws Throwable {
        System.err.println("Debugg haha before");
        Object ret = Ijpt.proceed();
        System.err.println("Debugg haha after");
        return ret;
    }

}
