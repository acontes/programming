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
package org.objectweb.proactive.core.component.group;

import org.objectweb.proactive.core.component.group.ProxyForComponentInterfaceGroup;
import org.objectweb.proactive.core.group.ProxyForGroup;
import org.objectweb.proactive.core.group.TaskFactory;
import org.objectweb.proactive.core.group.TaskFactoryFactory;


/**
 * A factory for component task factories. 
 * 
 * Indeed, groups dispatch parameters in configurable non-broadcast modes if and only if: 
 * they are instances of groups, and group parameters are tagged as "scatter" ( @link org.objectweb.proactive.core.group.TaskFactoryFactory ). 
 *
 * On the contrary, the component framework simply interprets annotations on the signatures of
 * classes / methods / arguments.
 * 
 * Hence the two distinct factories.
 * 
 * @author The ProActive Team
 *
 */
public class ComponentTaskFactoryFactory {

    public static TaskFactory getTaskFactory(ProxyForGroup<?> groupProxy) {
        if (groupProxy instanceof ProxyForComponentInterfaceGroup) {
            return new CollectiveItfsTaskFactory(groupProxy);
        } else {
            return TaskFactoryFactory.getTaskFactory(groupProxy);
        }
    }

}
