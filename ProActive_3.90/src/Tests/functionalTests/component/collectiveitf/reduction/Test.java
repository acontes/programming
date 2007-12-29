/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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
 */
package functionalTests.component.collectiveitf.reduction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.component.exceptions.ReductionException;
import org.objectweb.proactive.core.component.type.annotations.multicast.Reduce;
import org.objectweb.proactive.core.component.type.annotations.multicast.ReduceMode;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;

import functionalTests.ComponentTest;


public class Test extends ComponentTest {

    /**
         *
         */
    private static final long serialVersionUID = 6353128567772870415L;
    public static final String MESSAGE = "-Main-";
    public static final int NB_CONNECTED_ITFS = 2;

    public Test() {
        super("Multicast reduction for components", "Multicast reduction for components");
    }

    /*
     * @see testsuite.test.FunctionalTest#action()
     */
    @org.junit.Test
    public void action() throws Exception {
        Factory f = org.objectweb.proactive.core.component.adl.FactoryFactory.getFactory();
        Map context = new HashMap();

        // test selection of unique value

        List<IntWrapper> l = new ArrayList<IntWrapper>();
        l.add(new IntWrapper(12));
        //		l.add(new StringWrapper("toti"));

        Object result = null;
        try {
            result = org.objectweb.proactive.core.component.type.annotations.multicast.ReduceMode.SELECT_UNIQUE_VALUE
                    .reduce(l);
        } catch (ReductionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //		System.out.println("result = " + result);
        Assert.assertEquals(new IntWrapper(12), result);

        Class<?> clazz = RequiredService.class.getMethod("method1", new Class[] { List.class })
                .getAnnotation(Reduce.class).customReductionMode();
        //		l.clear();

        System.out.println(clazz.getName());

        // test case: simple invocation on component with unicast - annotated interface
        Component simpleTestCase = (Component) f.newComponent(
                "functionalTests.component.collectiveitf.reduction.testcase", context);
        Fractal.getLifeCycleController(simpleTestCase).startFc();
        boolean result2 = ((TesterItf) simpleTestCase.getFcInterface("runTestItf")).runTest();
        Assert.assertTrue(result2);

        //        Component testcase = (Component) f.newComponent("functionalTests.component.collectiveitf.multicast.testcase",
        //                context);

        //        Fractal.getLifeCycleController(testcase).startFc();
        //        ((Tester) testcase.getFcInterface("runTestItf")).testConnectedServerMulticastItf();
        //        ((Tester) testcase.getFcInterface("runTestItf")).testOwnClientMulticastItf();
    }
}
