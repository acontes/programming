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
package nonregressiontest.activeobject.migration.strategy;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.core.body.migration.Migratable;
import org.objectweb.proactive.ext.migration.MigrationStrategy;
import org.objectweb.proactive.ext.migration.MigrationStrategyImpl;
import org.objectweb.proactive.ext.migration.MigrationStrategyManager;
import org.objectweb.proactive.ext.migration.MigrationStrategyManagerImpl;

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
    int counter = 0;
    private MigrationStrategyManager migrationStrategyManager;
    private MigrationStrategy migrationStrategy;

    public A() {
    }

    public A(String[] nodesUrl) {
        migrationStrategy = new MigrationStrategyImpl();
        int i;
        for (i = 0; i < nodesUrl.length; i++) {
            migrationStrategy.add(nodesUrl[i], "arrived");
        }
    }

    public void runActivity(Body body) {
        if (counter == 0) {
            try {
                migrationStrategyManager = new MigrationStrategyManagerImpl((Migratable) body);
                migrationStrategyManager.onDeparture("leaving");
                migrationStrategyManager.setMigrationStrategy(this.migrationStrategy);
                migrationStrategyManager.startStrategy(body);
            } catch (Exception e) {
                e.printStackTrace();
            }
            counter++;
        }
        org.objectweb.proactive.Service service = new org.objectweb.proactive.Service(body);
        service.fifoServing();
    }

    public void leaving() {
        counter++;
    }

    public void arrived() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }
}
