package org.objectweb.proactive.examples.webservices.helloWorld;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.objectweb.proactive.extensions.webservices.cxf.WSConstants;


public class WSClientComponentCXF {

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

        ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
        factory.setServiceClass(HelloWorldItf.class);
        factory.setAddress(url + WSConstants.SERVICES_PATH + "server_hello-world");
        HelloWorldItf client = (HelloWorldItf) factory.create();

        try {

            String str = client.helloWorld("ProActive Team");
            System.out.println(str);

            client.setText("A text has been inserted");

            str = client.sayText();
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        factory = new ClientProxyFactoryBean();
        factory.setServiceClass(GoodByeWorldItf.class);
        factory.setAddress(url + WSConstants.SERVICES_PATH + "server_goodbye-world");
        GoodByeWorldItf client2 = (GoodByeWorldItf) factory.create();

        try {

            String res = client2.goodByeWorld("ProActive Team");
            System.out.println(res);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
