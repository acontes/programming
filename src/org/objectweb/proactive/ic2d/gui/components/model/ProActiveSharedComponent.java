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
package org.objectweb.proactive.ic2d.gui.components.model;

import org.objectweb.fractal.gui.model.BasicComponent;
import org.objectweb.fractal.gui.model.SharedComponent;
import org.objectweb.proactive.core.ProActiveRuntimeException;


/**
 * @author Matthieu Morel
 *
 */
public class ProActiveSharedComponent extends SharedComponent {
    protected String virtualNode;

    /**
     * @param arg0
     */
    public ProActiveSharedComponent(BasicComponent arg0) {
        super(arg0);
    }

    public String getVirtualNode() {
        return ((ProActiveComponent) masterComponent).getVirtualNode();
    }

    public void setVirtualNode(String virtualNode) {
        ((ProActiveComponent) masterComponent).setVirtualNode(virtualNode);
    }

    public void setExportedVirtualNodes(String exportedVirtualNodes) {
        throw new ProActiveRuntimeException("not yet implemented");
        //((ProActiveComponent)masterComponent).setExportedVirtualNodes(null);
    }

    public String getExportedVirtualNodes() {
        return ((ProActiveComponent) masterComponent).getExportedVirtualNodesAfterComposition();
    }
}
