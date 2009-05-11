/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import org.objectweb.proactive.api.PARemoteObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.remoteobject.RemoteObjectExposer;
import org.objectweb.proactive.core.remoteobject.RemoteObjectHelper;


/**
 * Deploys {@link NamingService} instance on the local runtime.
 */
public class NamingServiceDeployer {

    private static final String NAMING_SERVICE_DEFAULT_NAME = "defaultNamingService";

    /** URL of the remote object */
    final private String url;

    final private NamingService namingService;

    RemoteObjectExposer<NamingService> roe;

    public NamingServiceDeployer() {
        this(NAMING_SERVICE_DEFAULT_NAME);
    }

    public NamingServiceDeployer(String name) {
        namingService = new NamingService();

        roe = PARemoteObject.newRemoteObject(NamingService.class.getName(), this.namingService);
        roe.createRemoteObject(name);
        url = roe.getURL();
    }

    /** Get the local log collector */
    public NamingService getCollector() {
        return this.namingService;
    }

    /** Get the log collector as a remote object */
    public NamingService getRemoteObject() throws ProActiveException {
        return (NamingService) RemoteObjectHelper.generatedObjectStub(this.roe.getRemoteObject());
    }

    public String getNamingServiceURL() {
        return this.url;
    }

    /**
     * Unexport the remote object.
     * 
     * @throws ProActiveException
     */
    public void terminate() throws ProActiveException {
        if (roe != null) {
            roe.unexportAll();
            roe = null;
        }
    }
}
