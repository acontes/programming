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
package functionalTests.component.conform.components;

import java.util.List;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.util.wrapper.GenericTypeWrapper;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;


public class SlaveImpl implements Slave {
    public SlaveImpl() {
    }

    public void computeOneWay(String arg, String other) {
        System.err.println(PAActiveObject.getBodyOnThis().getNodeURL() + "Slave: " + this + " arg: " + arg +
            " other: " + other);
    }

    public StringWrapper computeAsync(String arg, String other) {
        return new StringWrapper(computeSync(arg, other));
    }

    public StringWrapper computeRoundRobinBroadcastAsync(String arg, List<String> other) {
        String aggregOther = "";
        for (String string : other) {
            aggregOther += string;
        }
        return new StringWrapper(computeSync(arg, aggregOther));
    }

    public GenericTypeWrapper<String> computeAsyncGenerics(String arg, String other) {
        return new GenericTypeWrapper<String>(computeSync(arg, other));
    }

    public String computeSync(String arg, String other) {
        computeOneWay(arg, other);
        return "arg: '" + arg + "',other: '" + other + "'";
    }

}
