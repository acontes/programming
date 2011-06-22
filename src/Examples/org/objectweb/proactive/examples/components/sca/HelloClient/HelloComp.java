package org.objectweb.proactive.examples.components.sca.HelloClient;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;


public class HelloComp implements BindingController, Runner {
    public static final String HELLO_SERVICE_NAME = "HelloService";
    HelloService hservice;

    public void bindFc(String clientItfName, Object serverItf) throws NoSuchInterfaceException,
            IllegalBindingException, IllegalLifeCycleException {
        if (HELLO_SERVICE_NAME.equals(clientItfName)) {
            hservice = (HelloService) serverItf;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public String[] listFc() {
        return new String[] { HELLO_SERVICE_NAME };
    }

    public Object lookupFc(String clientItfName) throws NoSuchInterfaceException {
        if (HELLO_SERVICE_NAME.equals(clientItfName)) {
            return hservice;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public void unbindFc(String clientItfName) throws NoSuchInterfaceException, IllegalBindingException,
            IllegalLifeCycleException {
        if (HELLO_SERVICE_NAME.equals(clientItfName)) {
            hservice = null;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public void execute() throws Exception {
        hservice.print("cool");
    }
}
