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
package functionalTests.activeobject.lookupactive;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.util.ProActiveInet;
import org.objectweb.proactive.core.util.URIBuilder;

import functionalTests.FunctionalTest;


/**
 * Test register and lookup AOs
 */

public class TestLookupActive extends FunctionalTest {

    @Test
    public void action() throws Exception {
        A a = PAActiveObject.newActive(A.class, new Object[] { "toto" });
        String url = a.register();

        a = (A) PAActiveObject.lookupActive(A.class.getName(), url);

        assertTrue(a != null);
        assertEquals(a.getName(), "toto");

        // check listActive contains the previous lookup
        String host = URIBuilder.buildURIFromProperties(ProActiveInet.getInstance().getHostname(), "")
                .toString();
        String[] registered = PAActiveObject.listActive(url);
        assertNotNull(registered);

        for (int i = 0; i < registered.length; i++) {
            if (registered[i].substring(registered[i].lastIndexOf('/')).equals("/A")) {
                return;
            }
        }

        throw new Exception("Could not find registered object in list of objects");
    }

    @Test(expected = IOException.class)
    public void lookupNode() throws Exception {
        A a = PAActiveObject.newActive(A.class, new Object[] { "toto" });
        String nodeURL = PAActiveObject.getActiveObjectNodeUrl(a);
        PAActiveObject.lookupActive(A.class.getName(), nodeURL);
    }
}
