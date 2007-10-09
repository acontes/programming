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
package org.objectweb.proactive.extra.infrastructuremanager.imnode;

import java.util.Comparator;

import org.objectweb.proactive.extra.scheduler.common.scripting.VerifyingScript;


/**
 * Comparator for imnodes :
 * compare two nodes by their chances to verify a script.
 * @author ProActive Team
 * @version 1.0, Jul 12, 2007
 * @since ProActive 3.2
 */
public class IMNodeComparator implements Comparator<IMNode> {
    private VerifyingScript script;

    public IMNodeComparator(VerifyingScript script) {
        this.script = script;
    }

    public int compare(IMNode o1, IMNode o2) {
        int status1 = IMNode.NEVER_TESTED;
        if (o1.getScriptStatus().containsKey(script)) {
            status1 = o1.getScriptStatus().get(script);
        }
        int status2 = IMNode.NEVER_TESTED;
        if (o2.getScriptStatus().containsKey(script)) {
            status2 = o2.getScriptStatus().get(script);
        }
        return status2 - status1;
    }
}
