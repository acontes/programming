package org.objectweb.proactive.examples.webservices.helloWorld;

import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.EndpointImpl;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.objectweb.proactive.extensions.webservices.WSConstants;


public class WSClientCXF {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {

        String url = "";
        if (args.length == 0) {
            url = "http://localhost:8080/";
        } else if (args.length == 1) {
            url = args[0];
        } else {
            System.out.println("Wrong number of arguments:");
            System.out.println("Usage: java WSClientCXF [url]");
            return;
        }

        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }

        if (!url.endsWith("/")) {
            url = url + "/";
        }

        ClientFactoryBean factory = new ClientFactoryBean();
        factory.setServiceClass(HelloWorld.class);
        factory.setAddress(url + WSConstants.SERVICES_PATH + "HelloWorld");
        factory.getServiceFactory().setQualifyWrapperSchema(false);
        boolean isQualify = factory.getServiceFactory().getQualifyWrapperSchema();
        Client client = factory.create();

        Object[] res;
        System.out.println("isQualify returns " + isQualify);
        try {

            //            Set<Entry<String,Object>> entries = client.getRequestContext().entrySet();
            //            for (Entry<String, Object> entry : entries) {
            //                System.out.println(entry.getKey());
            //            }
            res = client.invoke("sayText");
            String text = (String) res[0];
            System.out.println(text);

            client.invoke("putTextToSay", "Hello ProActive Team");

            res = client.invoke("sayText");
            System.out.println(res[0]);

            client.invoke("putHelloWorld");

            res = client.invoke("sayText");
            System.out.println(res[0]);

            res = client.invoke("putTextToSayAndConfirm", "Good Bye ProActive Team");
            System.out.println(res[0]);

            res = client.invoke("sayText");
            System.out.println(res[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
