package org.objectweb.proactive.extensions.webservices.axis2;

import org.objectweb.proactive.extensions.webservices.AbstractWebServicesFactory;
import org.objectweb.proactive.extensions.webservices.WebServices;
import org.objectweb.proactive.extensions.webservices.WebServicesFactory;


public class Axis2WebServicesFactory extends AbstractWebServicesFactory implements WebServicesFactory {

    public Axis2WebServicesFactory() {
        super();
    }

    @Override
    public String getFrameWorkId() {
        return "axis2";
    }

    @Override
    public WebServices newWebServices(String url) {
        return new Axis2WebServices(url);
    }

}
