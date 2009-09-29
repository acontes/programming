package org.objectweb.proactive.examples.webservices.helloWorld;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.objectweb.proactive.extensions.webservices.cxf.WSConstants;
import org.objectweb.proactive.extensions.webservices.cxf.deployer.PADeployer;
import org.objectweb.proactive.extensions.webservices.cxf.servicedeployer.ServiceDeployer;


public class CallCXFTest {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ClientFactoryBean factory = new ClientFactoryBean();
        factory.setServiceClass(CXFHelloWorld.class);
        factory.setAddress("http://localhost:8081/" + WSConstants.SERVICES_PATH + "HelloWorld");
        Client client = factory.create();

        Object[] res;
        res = client.invoke("getText", new Object[] {});
        System.out.println(res[0]);

        res = client.invoke("setTextAndReturn1", "setTextAndReturn argument");
        System.out.println(res[0]);

        res = client.invoke("getText", new Object[] {});
        System.out.println(res[0]);

        client.invoke("setText", "setText argument");

        res = client.invoke("getText", new Object[] {});
        System.out.println(res[0]);
    }

}
