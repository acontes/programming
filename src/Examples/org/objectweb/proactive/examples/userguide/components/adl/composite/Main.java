//@tutorial-start
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
//@snippet-start adl_composite_Main
package org.objectweb.proactive.examples.userguide.components.adl.composite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.component.adl.FactoryFactory;
import org.objectweb.proactive.examples.userguide.components.adl.composite.Runner;


/**
 * @author The ProActive Team
 */
public class Main {

    public static void main(String[] args) throws Exception {
        Factory factory = FactoryFactory.getFactory();

        Map<String, Object> context = new HashMap<String, Object>();
        Component composite = (Component) factory.newComponent(
                "org.objectweb.proactive.examples.userguide.components.adl.composite.adl.Composite", context);

        Fractal.getLifeCycleController(composite).startFc();

        Runner runner = (Runner) composite.getFcInterface("runner");
        List<String> arg = new ArrayList<String>();
        arg.add("hello");
        arg.add("world");
        runner.run(arg);

        Fractal.getLifeCycleController(composite).stopFc();

        System.exit(0);
    }
}
//@tutorial-end
//@snippet-end adl_composite_Main