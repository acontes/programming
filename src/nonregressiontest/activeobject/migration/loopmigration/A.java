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
package nonregressiontest.activeobject.migration.loopmigration;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.core.body.migration.MigrationException;

import java.io.Serializable;


/**
 * @author rquilici
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class A implements Serializable, RunActive {
    public static final int MAX_MIG = 20;
    String node1;
    String node2;
    boolean exceptionThrown = false;
    int migrationCounter = 0;
    boolean isNode1 = true;

    public A() {
    }

    public A(String node1, String node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    protected boolean inNode1() {
        return isNode1;
    }

    protected void changeNode() {
        isNode1 = !isNode1;
        migrationCounter++;
    }

    public void runActivity(Body body) {
        if (migrationCounter < MAX_MIG) {
            try {
                if (inNode1()) {
                    changeNode();
                    ProActive.migrateTo(node2);
                } else {
                    changeNode();
                    ProActive.migrateTo(node1);
                }
            } catch (MigrationException e) {
                this.exceptionThrown = true;
                e.printStackTrace();
            }
        } else {
            Service service = new Service(body);
            service.blockingServeOldest();
        }
    }

    public boolean isException() {
        return exceptionThrown;
    }
}
