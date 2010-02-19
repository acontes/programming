/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
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
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.component.adl.nodes;

import org.objectweb.proactive.annotation.PublicAPI;


/**
 * This interface adds a cardinality attribute to the virtual node (compared to the VirtualNode interface
 * from the standard Fractal ADL). This allows a strict definition of how to use
 * the components deployed on such a virtual node : in case of a multiple virtual node,
 * the reference that we have on a primitive component is actually a reference on a group
 * of primitive components.<br>
 * This implies that method invocations will return groups of results, and this has to be known
 * in advance.
 * The cardinality of a VirtualNode is used to know if a composition of VirtualNode is possible.
 *
 * @author The ProActive Team
 */
@PublicAPI
public interface VirtualNode extends org.objectweb.fractal.adl.nodes.VirtualNode {

    /**
     * A virtual node with a single cardinality contains only one real node.
     */
    public static String SINGLE = "single";

    /**
     * A virtual node with a single cardinality can contain many real node.
     */
    public static String MULTIPLE = "multiple";

    /**
     * Getter for the cardinality.
     *
     * @return the cardinality of the virtual node
     */
    String getCardinality();

    /**
     * Setter for the cardinality.
     *
     * @param cardinality the cardinality of the virtual node
     */
    void setCardinality(String cardinality);
}
