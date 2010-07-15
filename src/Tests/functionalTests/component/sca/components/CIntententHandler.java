package functionalTests.component.sca.components;

import org.objectweb.proactive.extensions.component.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.component.sca.control.IntentJoinPoint;


public class CIntententHandler implements IntentHandler {

    public CIntententHandler() {

    }

    @Override
    public Object invoke(IntentJoinPoint Ijpt) throws Throwable {
        System.err.println("Debugg haha before " + System.currentTimeMillis());
        Thread.sleep(100);
        Object ret = Ijpt.proceed();
        System.out.println(ret);
        System.err.println("Debugg haha after " + System.currentTimeMillis());
        return ret;
    }

}
