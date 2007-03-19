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
package nonregressiontest.activeobject.request.immediateservice.terminateActiveObject;

import org.objectweb.proactive.ProActive;
import testsuite.test.FunctionalTest;


public class Test extends FunctionalTest {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5501538868583034371L;
	B b;

    public Test() {
        super("Active Object termination",
            "Test immediate termination of an active object");
    }

    @Override
	public void action() throws Exception {
        b = (B) ProActive.newActive(B.class.getName(), new Object[] { "blue" });
        b.changeColor("red");
        ProActive.terminateActiveObject(b, true);
    }

    @Override
	public void initTest() throws Exception {
    }

    @Override
	public void endTest() throws Exception {
    }

    @Override
	public boolean postConditions() {
        try {
            b.getColor();
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Test test = new Test();
        try {
            test.action();
            if (test.postConditions()) {
                System.out.println("TEST SUCCEEDED");
            } else {
                System.out.println("TEST FAILED");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
