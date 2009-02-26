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
package functionalTests.activeobject.replaceObject;

import static junit.framework.Assert.assertTrue;

import java.util.Arrays;
import java.util.Hashtable;

import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.RestoreManager;
import org.objectweb.proactive.core.node.NodeException;

import functionalTests.FunctionalTest;


/*
 * See PROACTIVE-229
 */
public class TestReplaceObject extends FunctionalTest {

    @Test
    public void test() throws ActiveObjectCreationException, NodeException {

        ObjectFrom of = new ObjectFrom();
        ObjectTo ot = new ObjectTo();
        RestoreManager rm = new RestoreManager();
        Hashtable<Integer, Object> ht = new Hashtable<Integer, Object>();
        Object newObject = MOP.changeObject(of, of, ot, rm, ht);

        assertTrue(newObject == ot);

        Object[] o = new Object[3];
        o[0] = of;
        o[1] = new ObjectFrom();
        o[2] = of;

        rm = new RestoreManager();
        ht = new Hashtable<Integer, Object>();
        Object[] newArray = (Object[]) MOP.changeObject(o, of, ot, rm, ht);

        System.out.println(Arrays.toString(newArray));
        System.out.println(Arrays.toString(o));

        try {
            System.out.println(Arrays.toString((Object[]) rm.restore(newArray)));
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}