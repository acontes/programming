package org.objectweb.proactive.extensions.masterworker.core;

import java.io.IOException;
import java.rmi.RemoteException;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;

import org.objectweb.proactive.core.config.PAProperties;


/**
 * <i><font size="-1" color="#FF0000">**For internal use only** </font></i><br>
 * The Workers Active Objects are the workers in the Master/Worker API.<br>
 * They execute tasks needed by the master
 *
 * @author The ProActive Team
 */
public class SpaceLookup implements DiscoveryListener {
    private ServiceTemplate theTemplate;
    private LookupDiscovery theDiscoverer;

    private Object theProxy;

    /**
       @param aServiceInterface the class of the type of service you are
       looking for.  Class is usually an interface class.
     */
    public SpaceLookup(Class aServiceInterface) {
        Class[] myServiceTypes = new Class[] { aServiceInterface };
        theTemplate = new ServiceTemplate(null, myServiceTypes, null);
    }

    /**
       Having created a Lookup (which means it now knows what type of service
       you require), invoke this method to attempt to locate a service
       of that type.  The result should be cast to the interface of the
       service you originally specified to the constructor.

       @return proxy for the service type you requested - could be an rmi
       stub or an intelligent proxy.
     */
    public Object getService() {
        synchronized (this) {
            if (theDiscoverer == null) {

                try {
                    theDiscoverer = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
                    theDiscoverer.addDiscoveryListener(this);
                } catch (IOException anIOE) {
                    System.err.println("Failed to init lookup");
                    anIOE.printStackTrace(System.err);
                }
            }
        }

        return waitForProxy();
    }

    /**
       Location of a service causes the creation of some threads.  Call this
       method to shut those threads down either before exiting or after a
       proxy has been returned from getService().
     */
    void terminate() {
        synchronized (this) {
            if (theDiscoverer != null)
                theDiscoverer.terminate();
        }
    }

    /**
       Caller of getService ends up here, blocked until we find a proxy.

       @return the newly downloaded proxy
     */
    private Object waitForProxy() {
        synchronized (this) {
            while (theProxy == null) {

                try {
                    wait();
                } catch (InterruptedException anIE) {
                }
            }

            return theProxy;
        }
    }

    /**
       Invoked to inform a blocked client waiting in waitForProxy that
       one is now available.

       @param aProxy the newly downloaded proxy
     */
    private void signalGotProxy(Object aProxy) {
        synchronized (this) {
            if (theProxy == null) {
                theProxy = aProxy;
                notify();
            }
        }
    }

    /**
       Everytime a new ServiceRegistrar is found, we will be called back on
       this interface with a reference to it.  We then ask it for a service
       instance of the type specified in our constructor.
     */
    public void discovered(DiscoveryEvent anEvent) {
        synchronized (this) {
            if (theProxy != null)
                return;
        }
        Object myProxy = null;
        ServiceRegistrar[] myRegs = null;

        if (null != PAProperties.PA_MASTERWORKER_JAVASPACE_SERVER_ADDRESS.getValue()) {
            String hostname = PAProperties.PA_MASTERWORKER_JAVASPACE_SERVER_ADDRESS.getValue();
            String portString = "4160";
            if (null != PAProperties.PA_MASTERWORKER_JAVASPACE_SERVER_PORT.getValue()) {
                Integer port = Integer
                        .parseInt(PAProperties.PA_MASTERWORKER_JAVASPACE_SERVER_PORT.getValue());
                portString = "" + port;
            }
            String URL = "jini://" + hostname + ":" + portString;
            try {
                LookupLocator locator = new LookupLocator(URL);
                if (null != locator) {
                    ServiceRegistrar myReg = (ServiceRegistrar) locator.getRegistrar();
                    myProxy = myReg.lookup(theTemplate);

                    if (myProxy != null) {
                        signalGotProxy(myProxy);
                        return;
                    }
                }
            } catch (Exception anRE) {
                System.err
                        .println("Get JavaSpace error, please check hostname and port, default port should be 4160");
                anRE.printStackTrace(System.err);
            }
        } else {
            myRegs = anEvent.getRegistrars();

            for (int i = 0; i < myRegs.length; i++) {
                ServiceRegistrar myReg = myRegs[i];

                try {
                    myProxy = myReg.lookup(theTemplate);

                    if (myProxy != null) {
                        signalGotProxy(myProxy);
                        break;
                    }
                } catch (RemoteException anRE) {
                    System.err.println("ServiceRegistrar barfed");
                    anRE.printStackTrace(System.err);
                }
            }
        }
    }

    /**
       When a ServiceRegistrar ��disappears�� due to network partition etc.
       we will be advised via a call to this method - as we only care about
       new ServiceRegistrars, we do nothing here.
     */
    public void discarded(DiscoveryEvent anEvent) {
    }
}
