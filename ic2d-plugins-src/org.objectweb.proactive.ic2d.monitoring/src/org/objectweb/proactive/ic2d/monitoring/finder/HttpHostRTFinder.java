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

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.Constants;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeAdapterImpl;
import org.objectweb.proactive.core.runtime.http.HttpProActiveRuntime;
import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.ic2d.console.Console;
import org.objectweb.proactive.ic2d.monitoring.Activator;
import org.objectweb.proactive.ic2d.monitoring.data.HostObject;

public class HttpHostRTFinder implements HostRTFinder {

	public List<ProActiveRuntime> findPARuntime(HostObject host) {
		
		Console console = Console.getInstance(Activator.CONSOLE_NAME);
		
		console.log("Exploring "+host+" with Http on port "+host.getPort());
		
		List<ProActiveRuntime> runtimes = new ArrayList<ProActiveRuntime>();
		
		ProActiveRuntimeAdapterImpl adapter = null;
		try {
			adapter = new ProActiveRuntimeAdapterImpl(new HttpProActiveRuntime(
			        UrlBuilder.buildUrl(host.getHostName(), "", Constants.XMLHTTP_PROTOCOL_IDENTIFIER, host.getPort())));
		} catch (ProActiveException e) {
			// TODO Auto-generated catch block
			console.logException(e);
			e.printStackTrace();
		}
		runtimes.add(adapter);
		return runtimes;
	}

}
