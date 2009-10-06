package org.objectweb.proactive.extensions.webservices.cxf;

import org.objectweb.proactive.extensions.webservices.WebServicesFactory;
import org.objectweb.proactive.extensions.webservices.WebServicesFactorySPI;


public class CXFWebServicesFactorySPI implements WebServicesFactorySPI {

    public String getFrameWorkId() {
        return "cxf";
    }

    public Class<? extends WebServicesFactory> getFactoryClass() {
        return CXFWebServicesFactory.class;
    }

}
