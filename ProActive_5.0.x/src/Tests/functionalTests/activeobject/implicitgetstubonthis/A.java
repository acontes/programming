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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package functionalTests.activeobject.implicitgetstubonthis;

import java.io.Serializable;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAMobileAgent;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;


public class A implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 500L;

    public A() {
    }

    public void migrateTo(Node n) {
        try {
            PAMobileAgent.migrateTo(n);
        } catch (MigrationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String sayHello() {
        return "hello from " + PAActiveObject.getBodyOnThis().getNodeURL();
    }

    public StringWrapper sayWrappedHello() {
        return new org.objectweb.proactive.core.util.wrapper.StringWrapper("hello");
    }

    public A returnThis() {
        return this;
    }

    public B aInB() {
        return new B(this);
    }

    public boolean callTakeAOnB(B b) {
        return b.takeA(this);
    }

}
