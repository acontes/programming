/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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
package org.objectweb.proactive.core.body.future;

import org.objectweb.proactive.core.UniqueID;


/**
 * <p>
 * An object implementing this interface if a place holder object for an upcomming result yet
 * to come.
 * </p><p>
 * <b>see <a href="../../../doc-files/FutureObjectCreation.html">active object creation doumentation</a></b>
 * </p>
 *
 * @author  ProActive Team
 * @version 1.0,  2001/10/23
 * @since   ProActive 0.9
 *
 */
public interface Future extends LocalFuture {
    public boolean isAwaited();

    public void waitFor();

    public Throwable getRaisedException();

    public Object getResult();

    /**
     * To set the sequence id of this future.
     */
    public void setID(long id);

    /**
     * To get the sequence id of this future.
     */
    public long getID();

    /**
     * To set the creatorID, ie the UniqueID of the body which create this future
     */
    public void setCreatorID(UniqueID i);

    /**
     * To get the creatorID.
     */
    public UniqueID getCreatorID();

    /**
     * To set the senderID, ie the UniqueID of the body that will send this future,
     * in case of automatic continuation.
     */
    public void setSenderID(UniqueID i);
}
