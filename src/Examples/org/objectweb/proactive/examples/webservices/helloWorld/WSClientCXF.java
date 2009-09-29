package org.objectweb.proactive.examples.webservices.helloWorld;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.objectweb.proactive.extensions.webservices.cxf.WSConstants;


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
        Client client = factory.create();

        Object[] res;
        try {

            res = client.invoke("sayText");
            System.out.println(res[0]);

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
