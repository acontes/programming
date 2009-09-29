package org.objectweb.proactive.examples.webservices.helloWorld;

import java.lang.reflect.Method;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.extensions.webservices.WSConstants;
import org.objectweb.proactive.extensions.webservices.WebServices;


public class TestCXF {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.out.println("Please choose between the following classes:");
                System.out.println("1 - HelloWorld");
                System.out.println("2 - CXFHelloWorld");
            }
            if (args[0].equals("2")) {

                CXFHelloWorld cxfhw = (CXFHelloWorld) PAActiveObject.newActive(CXFHelloWorld.class.getName(),
                        null);
                cxfhw.setText("Bonjour ProActive Team !");
                System.out.println("getText() returned: " + cxfhw.getText());
                //            Method m1 = CXFHelloWorld.class.getMethod("getText", null);
                //            Method m2 = CXFHelloWorld.class.getMethod("setText", String.class);
                //            Method m3 = CXFHelloWorld.class.getMethod("setTextAndReturn", String.class);
                //            Method[] methods = new Method[] {m1, m2, m3};
                //            WebServices.exposeAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, cxfhw,
                //                    "http://localhost:8081/", "HelloWorld", new String[] {"setHello", "setTextAndReturn"});

                //            WebServices.exposeAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, cxfhw,
                //                    "http://localhost:8081/", "HelloWorld", methods);
                WebServices.exposeAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, cxfhw,
                        "http://localhost:8080/", "CXFHelloWorld");
            } else {
                HelloWorld hw = (HelloWorld) PAActiveObject.newActive(HelloWorld.class.getName(), null);
                WebServices.exposeAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, hw,
                        "http://localhost:8081/", "HelloWorld");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
