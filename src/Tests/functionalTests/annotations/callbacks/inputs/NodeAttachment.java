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
package functionalTests.annotations.callbacks.inputs;

import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extensions.annotation.NodeAttachmentCallback;


// correct signature "void method(Node, String virtualNodeName)"

// error
@NodeAttachmentCallback
public class NodeAttachment {

    // ok
    @NodeAttachmentCallback
    public void a(Node node, String p) {
    }

    // error
    @NodeAttachmentCallback
    public void a2(String p, String p2) {
    }

    // error
    @NodeAttachmentCallback
    public void a3(Node p) {
    }

    // error
    @NodeAttachmentCallback
    public void a4() {
    }
}
