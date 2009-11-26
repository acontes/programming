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
package org.objectweb.proactive.extensions.timitspmd.util.observing;

/**
 * This class encapsulates an Object ie the EventData.
 *
 * @author The ProActive Team
 *
 */
public interface EventData extends java.io.Serializable {

    /**
     * The data setter.
     */
    public void setData(Object object);

    /**
     * The name getter.
     */
    public String getName();

    /**
     * The data getter.
     */
    public Object getData();

    /**
     * Collapse the data with another one.
     */
    public Object collapseWith(EventData anotherData, int anotherRank);

    /**
     * Provide the final result
     */
    public Object getFinalized();

    /**
     * Called by a StatDataBag on each StatData
     */
    public String toString();
}
