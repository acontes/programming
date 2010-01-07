/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of
 * 						   Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
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
 * If needed, contact us to obtain a release under GPL Version 2.
 *
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.extra.messagerouting.router;

import java.io.IOException;
import java.net.InetAddress;

import org.objectweb.proactive.annotation.PublicAPI;


/** A ProActive message router 
 * 
 *  
 * A router receives messages from client and forward them to another client.
 * 
 * @since ProActive 4.1.0
 */
@PublicAPI
public abstract class Router {

    static public Router createAndStart(RouterConfig config) throws IOException {
        // config is now immutable
        config.setReadOnly();

        RouterImpl r = new RouterImpl(config);

        Thread rThread = new Thread(r);
        rThread.setName("Router: select");
        if (config.isDaemon()) {
            rThread.setDaemon(config.isDaemon());
        }
        rThread.start();

        return r;
    }

    /** Returns the port on which the router is, or was, listening */
    abstract public int getPort();

    /** Returns the {@link InetAddress} on which the router is, or was, listening */
    abstract public InetAddress getInetAddr();

    /** Stops the router
     * 
     * Terminates all the threads and unbind all the sockets.
     */
    abstract public void stop();
}
