package org.objectweb.proactive.extensions.webservices.cxf;

import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.AbstractWebServicesFactory;
import org.objectweb.proactive.extensions.webservices.WebServices;
import org.objectweb.proactive.extensions.webservices.WebServicesFactory;


public class CXFWebServicesFactory extends AbstractWebServicesFactory implements WebServicesFactory {

    public CXFWebServicesFactory() {
        super();
    }

    @Override
    public String getFrameWorkId() {
        return "cxf";
    }

    @Override
    public WebServices newWebServices(String url) {
        return new CXFWebServices(url);
    }

}
