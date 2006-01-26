/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
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
package nonregressiontest.stub.stubinterface;

import org.objectweb.proactive.core.mop.MOP;

import testsuite.test.FunctionalTest;


/**
 * @author rquilici
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Test extends FunctionalTest {
    String result1;
    String result2;

    public Test() {
        super("StubInterface generation", "Test stub generation for interface");
    }

    /**
     * @see testsuite.test.FunctionalTest#action()
     */
    public void action() throws Exception {
        StringInterface i1 = (StringInterface) MOP.newInstance("nonregressiontest.stub.stubinterface.StringInterface",
                "nonregressiontest.stub.stubinterface.StringInterfaceImpl",
                new Object[] { "toto" },
                "nonregressiontest.stub.stubinterface.ProxyOne", new Object[0]);
        result1 = i1.getMyString();

        StringInterfaceImpl i2 = (StringInterfaceImpl) MOP.newInstance("nonregressiontest.stub.stubinterface.StringInterfaceImpl",
                new Object[] { "titi" },
                "nonregressiontest.stub.stubinterface.ProxyOne", new Object[0]);
        result2 = i2.getMyString();
    }

    /**
     * @see testsuite.test.AbstractTest#initTest()
     */
    public void initTest() throws Exception {
    }

    /**
     * @see testsuite.test.AbstractTest#endTest()
     */
    public void endTest() throws Exception {
    }

    public boolean postConditions() throws Exception {
        return (result1.equals("toto") && result2.equals("titi"));
    }
}
