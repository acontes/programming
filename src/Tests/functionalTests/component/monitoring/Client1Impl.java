package functionalTests.component.monitoring;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.ProActiveRuntimeException;

public class Client1Impl implements Runner, BindingController {
    Service1 service1;
    Service3 service3;
    
    public void run() {
        // TODO Auto-generated method stub

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
