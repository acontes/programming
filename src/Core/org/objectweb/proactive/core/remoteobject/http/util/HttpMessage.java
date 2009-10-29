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
package org.objectweb.proactive.core.remoteobject.http.util;

import java.io.Serializable;

import org.objectweb.proactive.core.remoteobject.http.util.exceptions.HTTPRemoteException;


/**
 * This interface is used to encapsulate any kind of HTTP message.
 * @author The ProActive Team
 * @see java.io.Serializable
 */
public abstract class HttpMessage implements Serializable {
    protected Object returnedObject;
    protected String url;

    public HttpMessage(String url) {
        this.url = url;
    }

    /**
     * Processes the message.
     * @return an object as a result of the execution of the message
     */
    public abstract Object processMessage() throws Exception;

    public boolean isOneWay() {
        return false;
    }

    /**
     * @throws HTTPRemoteException
     */
    public final void send() throws HTTPRemoteException {
        HttpMessageSender hms = new HttpMessageSender(this.url);
        this.returnedObject = hms.sendMessage(this);
    }
}
