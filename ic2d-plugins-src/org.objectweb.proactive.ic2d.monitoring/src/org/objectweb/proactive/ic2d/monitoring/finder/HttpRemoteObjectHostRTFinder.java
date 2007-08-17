/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.ic2d.monitoring.finder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.Constants;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.remoteobject.RemoteObject;
import org.objectweb.proactive.core.remoteobject.RemoteObjectFactory;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.ic2d.console.Console;
import org.objectweb.proactive.ic2d.monitoring.Activator;
import org.objectweb.proactive.ic2d.monitoring.data.HostObject;

public class HttpRemoteObjectHostRTFinder implements HostRTFinder {

	public List<ProActiveRuntime> findPARuntime(HostObject host) {
		
		Console console = Console.getInstance(Activator.CONSOLE_NAME);
		
		console.log("Exploring "+host+" with Http on port "+host.getPort());
		
		List<ProActiveRuntime> runtimes = new ArrayList<ProActiveRuntime>();
		
		//ProActiveRuntimeAdapterImpl adapter = null;
		try {
//			adapter = new ProActiveRuntimeAdapterImpl(new HttpProActiveRuntime(
//			        UrlBuilder.buildUrl(host.getHostName(), "", Constants.XMLHTTP_PROTOCOL_IDENTIFIER, host.getPort())));
//			

			
			
			URI url = new URI(host.getProtocol(),null,host.getHostName(),host.getPort(),"/",null,null);
			
			URI[] remoteObjectUris = RemoteObjectFactory.getRemoteObjectFactory(Constants.XMLHTTP_PROTOCOL_IDENTIFIER).list(url);
			
			for (int i = 0 ; i < remoteObjectUris.length; i++) {
				RemoteObject rro =  RemoteObjectFactory.getRemoteObjectFactory(Constants.XMLHTTP_PROTOCOL_IDENTIFIER).lookup(remoteObjectUris[i]);
				Object stub = rro.getObjectProxy();
				if (stub instanceof ProActiveRuntime) {
				runtimes.add((ProActiveRuntime ) stub);
			}

			}
			
//			System.out.println("RMIHostRTFinder.findPARuntime()  "  + stub);
		} catch (ProActiveException e) {
			// TODO Auto-generated catch block
			console.logException(e);
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		runtimes.add(adapter);
		return runtimes;
	}

}
