package org.objectweb.proactive.examples.webservices.helloWorld;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.extensions.webservices.WSConstants;
import org.objectweb.proactive.extensions.webservices.WebServices;


public class TestCXF {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            CXFHelloWorld cxfhw = (CXFHelloWorld) PAActiveObject.newActive(CXFHelloWorld.class.getName(),
                    null);
            cxfhw.setText("Bonjour ProActive Team !");
            WebServices.exposeAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, cxfhw,
                    "http://localhost:8081/", "HelloWorld");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
