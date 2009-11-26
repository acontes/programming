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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.extra.messagerouting.exceptions;

import org.objectweb.proactive.core.ProActiveException;


/** Signals that an error of some sort has occurred.
 *
 * This class is the general class of exceptions produced by failed message sending.
 * 
 * @since ProActive 4.1.0
 */

public class MessageRoutingException extends ProActiveException {

    public MessageRoutingException() {
        super();
    }

    public MessageRoutingException(String message) {
        super(message);
    }

    public MessageRoutingException(Throwable cause) {
        super(cause);
    }

    public MessageRoutingException(String message, Throwable cause) {
        super(message, cause);
    }

}
