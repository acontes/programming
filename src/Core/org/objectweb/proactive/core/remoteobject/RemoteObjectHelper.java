/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.remoteobject;

import java.lang.reflect.Constructor;
import java.net.URI;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.remoteobject.adapter.Adapter;
import org.objectweb.proactive.core.remoteobject.exception.UnknownProtocolException;
import org.objectweb.proactive.core.util.URIBuilder;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class RemoteObjectHelper {
    static final private Logger logger = ProActiveLogger.getLogger(Loggers.REMOTEOBJECT);

    /**
     * returns an url for a object to be exposed on the current host for a given
     * protocol and name
     *
     * @param protocol
     * @return the default port number associated to the protocol
     * @throws UnknownProtocolException
     */
    public static URI generateUrl(String protocol, String name) throws UnknownProtocolException {
        RemoteObjectFactory rof = AbstractRemoteObjectFactory.getRemoteObjectFactory(protocol);
        return URIBuilder.buildURI(null, name, protocol, rof.getPort(), true);
    }

    /**
     * returns an url for a object to be exposed on the current host for the
     * default protocol and name
     *
     * @param name
     * @return the URI for the given name
     */
    public static URI generateUrl(String name) {
        try {
            RemoteObjectFactory rof = AbstractRemoteObjectFactory.getDefaultRemoteObjectFactory();
            URI baseURI = rof.getBaseURI();
            return URIBuilder.buildURI(baseURI, name);
        } catch (UnknownProtocolException e) {
            ProActiveLogger.logImpossibleException(logger, e);
            return null;
        }
    }

    /**
     * @param protocol
     * @return return the remote object factory for a given protocol
     * @throws UnknownProtocolException
     */
    public static RemoteObjectFactory getRemoteObjectFactory(String protocol) throws UnknownProtocolException {
        return AbstractRemoteObjectFactory.getRemoteObjectFactory(protocol);
    }

    /**
     * @param url
     * @return eturn the remote object factory for the protocol contained within the url
     * @throws UnknownProtocolException
     */
    public static RemoteObjectFactory getFactoryFromURL(URI url) throws UnknownProtocolException {
        url = expandURI(url);
        return getRemoteObjectFactory(url.getScheme());
    }

    /**
     * make the url 'absolute' by explicitly setting all the possibly not set default values
     * @param uri
     * @return the uri with all values set
     */
    public static URI expandURI(URI uri) {
        if (uri.getScheme() == null) {
            int port = uri.getPort();
            if (port == -1) {
                uri = URIBuilder.buildURIFromProperties(uri.getHost(), uri.getPath());
            } else {
                uri = URIBuilder.setProtocol(uri, PAProperties.PA_COMMUNICATION_PROTOCOL.getValue());
            }
        }
        return uri;
    }

    /**
     * register a remote object at the endpoint identified by the url
     * @param target the remote object to register
     * @param url the url where to register the remote object
     * @param replacePreviousBinding true if any previous binding as to be replaced
     * @return return a remote reference on the remote object (aka a RemoteRemoteObject)
     * @throws ProActiveException
     */
    public static RemoteRemoteObject register(RemoteObject<?> target, URI url, boolean replacePreviousBinding)
            throws ProActiveException {
        return getFactoryFromURL(url).register(new InternalRemoteRemoteObjectImpl(target), expandURI(url),
                replacePreviousBinding);
    }

    /**
     * perform a lookup on the url in order to retrieve a reference on the remote object identified by this url
     * @param url
     * @return a remote object adapter wrapping a remote reference to a remote object
     * @throws ProActiveException
     */
    public static RemoteObject lookup(URI url) throws ProActiveException {
        return getFactoryFromURL(url).lookup(expandURI(url));
    }

    /**
     * generate a couple stub + proxy on the given remote object and set the remote object as target of the proxy
     * @param ro the remote object
     * @return the couple stub + proxy on the given remote object
     * @throws ProActiveException if the stub generation has failed or if the remote object is no longer available
     */
    @SuppressWarnings("unchecked")
    public static <T> T generatedObjectStub(RemoteObject<T> ro) throws ProActiveException {
        try {
            T reifiedObjectStub = (T) MOP.createStubObject(ro.getClassName(), ro.getTargetClass(),
                    new Class[] {});

            ((StubObject) reifiedObjectStub).setProxy(new SynchronousProxy(null, new Object[] { ro }));

            Class<Adapter<T>> adapter = (Class<Adapter<T>>) ro.getAdapterClass();

            if (adapter != null) {
                Class<?>[] classArray = new Class<?>[] {};

                Constructor<?>[] c = adapter.getConstructors();

                Adapter<T> ad = adapter.getConstructor(classArray).newInstance();
                ad.setTargetAndCallConstruct(reifiedObjectStub);
                return (T) ad;
            } else {
                return reifiedObjectStub;
            }
        } catch (Exception e) {
            throw new ProActiveException(e);
        }
    }
}
