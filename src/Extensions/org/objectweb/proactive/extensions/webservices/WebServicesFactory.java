package org.objectweb.proactive.extensions.webservices;

public interface WebServicesFactory {

    public WebServices newWebServices(String url);

    public WebServices getWebServices(String url);

    public String getFrameWorkId();

}
