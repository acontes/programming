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
package functionalTests.annotations.migrationsignal.inputs;

import org.objectweb.proactive.api.PAMobileAgent;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extensions.annotation.ActiveObject;
import org.objectweb.proactive.extensions.annotation.MigrationSignal;


@ActiveObject
public class Loopz {

    @MigrationSignal
    public void doWhileOne(Node node, boolean onCondition) throws MigrationException {
        do {
            System.out.println("Will migrate");
            doWhileTwo(node);
        } while (onCondition);
    }

    @MigrationSignal
    public void doWhileTwo(Node node) throws MigrationException {
        do
            PAMobileAgent.migrateTo(node);
        while (true);
    }

    @MigrationSignal
    public void WhileOne(Node node, boolean onCondition) throws MigrationException {
        while (onCondition) {
            System.out.println("Will migrate");
            doWhileOne(node, onCondition);
            System.out.println("not!");
        }
    }

    @MigrationSignal
    public void WhileTwo(Node node) throws MigrationException {
        while (true)
            PAMobileAgent.migrateTo(node);
    }

    @MigrationSignal
    public void ForOne(Node node, boolean onCondition) throws MigrationException {
        for (;;) {
            System.out.println("Will migrate");
            doWhileOne(node, onCondition);
            return; // this will actually cause an error!!! :)
        }
    }

    @MigrationSignal
    public void ForTwo(Node node) throws MigrationException {
        for (;;)
            PAMobileAgent.migrateTo(node);
    }

    @MigrationSignal
    public void EnhancedForOne(Node node, boolean onCondition) throws MigrationException {
        String[] stuffs = new String[] { "one", "two", "three" };
        for (String stuff : stuffs) {
            System.out.println("Will migrate");
            doWhileOne(node, onCondition);
        }

    }

    @MigrationSignal
    public void EnhancedForTwo(Node node) throws MigrationException {
        String[] stuffs = new String[] { "one", "two", "three" };
        for (String stuff : stuffs)
            PAMobileAgent.migrateTo(node);
    }

}
