package org.objectweb.proactive.extensions.webservices.axis2;

import org.objectweb.proactive.extensions.webservices.WebServicesFactory;
import org.objectweb.proactive.extensions.webservices.WebServicesFactorySPI;


public class Axis2WebServicesFactorySPI implements WebServicesFactorySPI {

    public String getFrameWorkId() {
        return "axis2";
    }

    public Class<? extends WebServicesFactory> getFactoryClass() {
        return Axis2WebServicesFactory.class;
    }
}
