package org.objectweb.proactive.extensions.webservices;

public abstract class AbstractWebServices implements WebServices {

    protected String url;

    public AbstractWebServices(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }
}
