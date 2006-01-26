/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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
package org.objectweb.proactive.core.event;


/**
 * A class implementing this interface can produce NodeCreationEvent. It has also the ability to register
 * or remove NodeCreationEvent listener
 */
public interface NodeCreationEventProducer {

    /**
     * Adds a listener of NodeCreationEvent. The listener will receive event when
     * a node is created on a VirtualNode.
     * @param listener the listener to add
     */
    public void addNodeCreationEventListener(NodeCreationEventListener listener);

    /**
     * Removes the NodeCreationEventListener.
     * @param listener the listener to remove
     */
    public void removeNodeCreationEventListener(
        NodeCreationEventListener listener);
}
