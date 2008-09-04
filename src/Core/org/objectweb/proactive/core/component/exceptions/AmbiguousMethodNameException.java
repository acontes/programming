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
package org.objectweb.proactive.core.component.exceptions;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.ProActiveRuntimeException;


/**
 * Exception thrown if the search of one method returns more than one method (ie. the returned methods have the same name but different parameters).
 *
 * @author The ProActive Team
 *
 */
@PublicAPI
public class AmbiguousMethodNameException extends ProActiveRuntimeException {
    public AmbiguousMethodNameException() {
        super();
    }

    public AmbiguousMethodNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public AmbiguousMethodNameException(String message) {
        super(message);
    }

    public AmbiguousMethodNameException(Throwable cause) {
        super(cause);
    }
}