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
package org.objectweb.proactive.extensions.masterworker;

import org.objectweb.proactive.extensions.masterworker.core.SpaceLookup;
import org.objectweb.proactive.extensions.masterworker.interfaces.JavaSpaceFactory;
import org.objectweb.proactive.core.config.PAProperties;

import net.jini.space.JavaSpace;


/**
 * EmptyMemoryFactory
 *
 * @author The ProActive Team
 */
public class ConstantJavaSpaceFactory implements JavaSpaceFactory {

    private JavaSpace space;

    public ConstantJavaSpaceFactory() {
        this.space = null;
        if (null != PAProperties.PA_MASTERWORKER_USEJAVASPACE.getValue()) {
            if (Boolean.parseBoolean(PAProperties.PA_MASTERWORKER_USEJAVASPACE.getValue())) {
                SpaceLookup finder = new SpaceLookup(JavaSpace.class);
                this.space = (JavaSpace) finder.getService();
                System.out.println("Using the JavaSpace...");
            }

        }
    }

    public ConstantJavaSpaceFactory(JavaSpace space) {
        this.space = space;
    }

    public JavaSpace newJavaSpaceInstance() {
        return this.space;
    }
}
