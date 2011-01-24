/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.component.collectiveitf.multicast.classbased;

import java.util.List;

import functionalTests.component.collectiveitf.multicast.Identifiable;
import functionalTests.component.collectiveitf.multicast.WrappedInteger;


public class ServerImpl implements BroadcastServerItf, OneToOneServerItf, Identifiable {
    int id = 0;

    /*
     * @see functionalTests.component.collectiveitf.multicast.Identifiable#getID()
     */
    public String getID() {
        return Integer.valueOf(id).toString();
    }

    /*
     * @see functionalTests.component.collectiveitf.multicast.Identifiable#setID(java.lang.String)
     */
    public void setID(String id) {
        this.id = new Integer(id);
    }

    /*
     * @see functionalTests.component.collectiveitf.multicast.classbased.BroadcastServerItf#dispatch(java.util.List)
     */
    public WrappedInteger dispatch(List<WrappedInteger> l) {
        functionalTests.component.collectiveitf.multicast.ServerImpl s = new functionalTests.component.collectiveitf.multicast.ServerImpl();
        s.setID(getID());
        return s.testBroadcast_Param(l);
    }

    /*
     * @see functionalTests.component.collectiveitf.multicast.classbased.OneToOneServerItf#dispatch(functionalTests.component.collectiveitf.multicast.WrappedInteger)
     */
    public WrappedInteger dispatch(WrappedInteger i) {
        functionalTests.component.collectiveitf.multicast.ServerImpl s = new functionalTests.component.collectiveitf.multicast.ServerImpl();
        s.setID(getID());
        return s.testOneToOne_Method(i);
    }
}
