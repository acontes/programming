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
package org.objectweb.proactive.core.remoteobject.rmissh;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.objectweb.proactive.core.Constants;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.remoteobject.InternalRemoteRemoteObject;
import org.objectweb.proactive.core.remoteobject.InternalRemoteRemoteObjectImpl;
import org.objectweb.proactive.core.remoteobject.RemoteObject;
import org.objectweb.proactive.core.remoteobject.RemoteRemoteObject;
import org.objectweb.proactive.core.remoteobject.rmi.AbstractRmiRemoteObjectFactory;
import org.objectweb.proactive.core.remoteobject.rmi.RmiRemoteObject;
import org.objectweb.proactive.core.ssh.rmissh.SshRMIClientSocketFactory;
import org.objectweb.proactive.core.util.ProActiveInet;
import org.objectweb.proactive.core.util.URIBuilder;


public class RmiSshRemoteObjectFactory extends AbstractRmiRemoteObjectFactory {

    public RmiSshRemoteObjectFactory() {
        super(Constants.RMISSH_PROTOCOL_IDENTIFIER, RmiSshRemoteObjectImpl.class);
    }

    protected Registry getRegistry(URI url) throws RemoteException {
        return LocateRegistry.getRegistry(url.getHost(), url.getPort(), new SshRMIClientSocketFactory());
    }

    public InternalRemoteRemoteObject createRemoteObject(RemoteObject<?> remoteObject, String name)
            throws ProActiveException {
        URI uri = URIBuilder.buildURI(ProActiveInet.getInstance().getHostname(), name, this.getProtocolId());
        // register the object on the register
        InternalRemoteRemoteObject irro = new InternalRemoteRemoteObjectImpl(remoteObject, uri);
        RemoteRemoteObject rmo = register(irro, uri, true);
        RemoteRemoteObject rmoWrapped = new RmiSshConnectionPropertiesWrapper(rmo);
        irro.setRemoteRemoteObject(rmoWrapped);
        irro.setRemoteRemoteObject(rmo);
        return irro;
    }

    //  public RemoteRemoteObject register(InternalRemoteRemoteObject target, URI url,
    //        boolean replacePreviousBinding) throws ProActiveException {
    //   return new RmiSshConnectionPropertiesWrapper(super.register(target, url, replacePreviousBinding));
    //}
}
