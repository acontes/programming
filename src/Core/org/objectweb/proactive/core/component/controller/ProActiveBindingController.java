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
package org.objectweb.proactive.core.component.controller;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.proactive.annotation.PublicAPI;


/**
 * Extension of the standard Fractal {@link org.objectweb.fractal.api.control.BindingController BindingController }
 *
 * @author The ProActive Team
 *
 */
@PublicAPI
public interface ProActiveBindingController extends BindingController {

    /**
     * Check if the current component's client interfaces are bounded.
     *
     * @return true if this component is bound on a client interface
     */
    public Boolean isBound();

    /**
     * Check if the current component is bound to an interface belonged to the given component.
     *
     * @param component A component
     * @return true if the current component is bound on this other component
     */
    public Boolean isBoundTo(Component component);
}
