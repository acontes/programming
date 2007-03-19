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
package org.objectweb.proactive.core.runtime;

import org.objectweb.proactive.core.descriptor.data.VirtualNode;


/**
 * This interface provides a local view of the ProActiveRuntime. It contains methods that can only
 * be called locally.
 * @author  ProActive Team
 * @version 1.0
 * @since   ProActive 3.0
 */
public interface LocalProActiveRuntime {

    /**
     * Register the given VirtualNode on this local ProActiveRuntime.
     * @param vn the virtualnode to register
     * @param vnName the name of the VirtualNode to register
     */
    public void registerLocalVirtualNode(VirtualNode vn, String vnName);

    /**
     * This method adds a reference to the runtime that created this runtime.
     * It is called when a new runtime is created from another one.
     * @param parentPARuntime the creator of this runtime
     */
    public void setParent(ProActiveRuntime parentPARuntime);
}
