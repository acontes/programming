package functionalTests.component.sca.components;

import org.objectweb.proactive.extensions.component.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.component.sca.control.IntentJoinPoint;


public class CIntententHandler implements IntentHandler {
    String msg;

    public CIntententHandler(String msg) {
        this.msg = msg;
    }

    @Override
    public Object invoke(IntentJoinPoint Ijpt) throws Throwable {
        System.err.println("Debugg  before " + System.currentTimeMillis() + "msg = " + msg);
        Thread.sleep(100);
        Object ret = Ijpt.proceed();
        System.err.println("Debugg after " + System.currentTimeMillis() + "msg = " + msg);
        return ret;
    }

}
