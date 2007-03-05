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
package nonregressiontest.bnb;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.branchnbound.BranchNBoundFactory;
import org.objectweb.proactive.branchnbound.exception.BnBManagerException;
import org.objectweb.proactive.branchnbound.user.BnBManager;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;

import testsuite.test.Assertions;
import testsuite.test.FunctionalTest;


public class Test extends FunctionalTest {
    private BnBManager manager = null;
    private ProActiveDescriptor pad = null;

    public Test() {
        super("BnB", "Test most used branch and bound features");
    }

    @Override
    public void action() throws Exception {
        Assertions.assertNotNull(this.manager);
        Assertions.assertNotNull(this.pad);
        this.manager.deployAndAddResources(this.pad.getVirtualNode("Workers"));
        try {
            // TODO to remove
            Thread.sleep(5000);
        } catch (Exception e) {
        }
    }

    @Override
    public void endTest() throws Exception {
        this.manager.terminate();
        this.pad.killall(false);
    }

    @Override
    public void initTest() throws Exception {
        try {
            this.manager = BranchNBoundFactory.getBnBManager();
        } catch (BnBManagerException e) {
            logger.fatal("Cannot instatiate the BnBManager", e);
            throw e;
        }
        this.pad = ProActive.getProactiveDescriptor(Test.class.getResource(
                    "/nonregressiontest/bnb/Workers.xml").getPath());
    }
}
