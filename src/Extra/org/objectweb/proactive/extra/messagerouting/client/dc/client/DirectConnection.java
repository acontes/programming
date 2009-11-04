/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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
 */
package org.objectweb.proactive.extra.messagerouting.client.dc.client;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.objectweb.proactive.extra.messagerouting.protocol.message.Message;


/**
 * Manages a single direct connection with a remote agent
 *
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public interface DirectConnection {

    /**
     * Connect to a remote agent
     * @param remote - the endpoint address of the Direct Connection server running on the remote agent
     * @return true if the connection is established
     * @throws IOException - if something bad happens during the connection attempt
     */
    public boolean connect(InetSocketAddress remote) throws IOException;

    /**
     * Send a message routing {@link Message} using this direct connection
     * @param msg the message to be sent
     * @throws IOException - if a transmission error occurs
     */
    public void push(Message msg) throws IOException;

    /**
     * Close this Direct Connection
     * @throws IOException - error while closing
     */
    public void close() throws IOException;

}
