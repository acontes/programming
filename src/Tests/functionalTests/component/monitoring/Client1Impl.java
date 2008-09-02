package functionalTests.component.monitoring;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.util.wrapper.IntMutableWrapper;


public class Client1Impl implements Runner, BindingController {
    private static final int NB_ITERATIONS = 100;
    private Service1 service1;
    private Service3 service3;

    private void sleep() {
        try {
            Thread.sleep((int) (Math.random()*10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        int methodNum;
        for (int i = 0; i < NB_ITERATIONS; i++) {
            sleep();
            methodNum = ((int) (Math.random() * 10)) % 5;
            switch (methodNum) {
                case 0:
                    service1.getInt();
                    break;
                case 1:
                    service1.doSomething();
                    break;
                case 2:
                    service1.hello();
                    break;
                case 3:
                    service3.foo(new IntMutableWrapper(1));
                    break;
                case 4:
                    service3.executeAlone();
                    break;
                default:
                    break;
            }
        }
    }

    public void bindFc(String clientItfName, Object serverItf) throws NoSuchInterfaceException,
            IllegalBindingException, IllegalLifeCycleException {
        if ("service1".equals(clientItfName)) {
            service1 = (Service1) serverItf;
        } else if ("service3".equals(clientItfName)) {
            service3 = (Service3) serverItf;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public String[] listFc() {
        return new String[] { "service1", "service3" };
    }

    public Object lookupFc(String clientItfName) throws NoSuchInterfaceException {
        if ("service1".equals(clientItfName)) {
            return service1;
        } else if ("service3".equals(clientItfName)) {
            return service3;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public void unbindFc(String arg0) throws NoSuchInterfaceException, IllegalBindingException,
            IllegalLifeCycleException {
        throw new ProActiveRuntimeException("not implemented!");
    }
}
