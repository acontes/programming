/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2004 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.core.component.representative;

import org.apache.log4j.Logger;

import org.objectweb.fractal.api.Component;

import org.objectweb.proactive.core.component.ComponentParameters;
import org.objectweb.proactive.core.mop.Proxy;


public class ProActiveComponentRepresentativeFactory {
    protected static Logger logger = Logger.getLogger(ProActiveComponentRepresentativeFactory.class.getName());
    private static ProActiveComponentRepresentativeFactory INSTANCE = null;

    private ProActiveComponentRepresentativeFactory() {
    }

    public static ProActiveComponentRepresentativeFactory instance() {
        if (INSTANCE == null) {
            return (INSTANCE = new ProActiveComponentRepresentativeFactory());
        } else {
            return INSTANCE;
        }
    }

    public Component createComponentRepresentative(
        ComponentParameters componentParameters, Proxy proxy) {
        return (Component) new ProActiveComponentRepresentativeImpl(componentParameters,
            proxy);
    }
}
