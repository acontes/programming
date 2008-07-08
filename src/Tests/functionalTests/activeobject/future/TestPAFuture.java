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
package functionalTests.activeobject.future;

import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.ProActiveTimeoutException;
import org.objectweb.proactive.core.ProActiveTimeoutException;

import functionalTests.FunctionalTest;


public class TestPAFuture extends FunctionalTest {

    @Test
    public void isAwaitedNoFuture() {
        Object o = new Object();
        boolean resp = PAFuture.isAwaited(o);
        Assert.assertFalse("O is not a future, should not been awaited", resp);
    }

    @Test(timeout = 500)
    public void waitForNoFuture() {
        Object o = new Object();
        PAFuture.waitFor(o);
    }

    @Test(timeout = 500)
    public void waitForWithTimeoutNoFuture() throws ProActiveTimeoutException {
        Object o = new Object();
        PAFuture.waitFor(o, 1000);
    }

    @Test
    public void waitForAny() {

    }

    @Test
    public void waitForAnyNoFture() {
        Vector<Object> v = new Vector<Object>();
        v.add(new Object());
        v.add(new Object());

        int index;
        index = PAFuture.waitForAny(v);
        v.remove(index);
        index = PAFuture.waitForAny(v);
        v.remove(index);
        Assert.assertTrue(v.isEmpty());
    }

    @Test(timeout = 500)
    public void waitForAllNoFture() {
        Vector<Object> v = new Vector<Object>();
        v.add(new Object());
        v.add(new Object());

        PAFuture.waitForAll(v);
    }

    @Test(timeout = 500)
    public void waitForAllWithTimeoutNoFture() throws ProActiveTimeoutException {
        Vector<Object> v = new Vector<Object>();
        v.add(new Object());
        v.add(new Object());

        PAFuture.waitForAll(v, 1000);
    }
}
