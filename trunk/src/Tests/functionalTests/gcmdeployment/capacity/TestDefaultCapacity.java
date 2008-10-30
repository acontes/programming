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
package functionalTests.gcmdeployment.capacity;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.extensions.gcmdeployment.core.StartRuntime;

import functionalTests.FunctionalTest;


public class TestDefaultCapacity extends FunctionalTest {
    @Test
    public void testCapacityAutoDetection() throws InterruptedException {

        PAProperties.PA_RUNTIME_STAYALIVE.setValue(false);
        StartRuntime.main(new String[] {});

        ProActiveRuntimeImpl part = ProActiveRuntimeImpl.getProActiveRuntime();

        long cap = part.getVMInformation().getCapacity();
        long nproc = Runtime.getRuntime().availableProcessors();
        Assert.assertEquals(nproc, cap);
        Assert.assertEquals(nproc, part.getVMInformation().getCapacity());
    }
}
