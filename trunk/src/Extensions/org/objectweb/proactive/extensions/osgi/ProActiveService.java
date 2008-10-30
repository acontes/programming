/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.extensions.osgi;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.core.node.NodeException;


/**
 * This interface represents a ProActive Service.
 * Using this service, a bundle can use this service ( only one instance per OSGi platform )
 * When this service is first called , it  creates a ProActive runtime.
 * @author The ProActive Team
 *
 */
public interface ProActiveService {

    /**
     *
     * @param classname
     * @param constructorParameters
     * @return
     * @throws ActiveObjectCreationException
     * @throws NodeException
     */
    public Object newActive(String classname, Object[] constructorParameters)
            throws ActiveObjectCreationException, NodeException;

    /**
     *
     * @param obj
     * @param url
     * @throws java.io.IOException
     */
    public void register(Object obj, String url) throws java.io.IOException;

    public void terminate();

    public Object lookupActive(String className, String url);
}
