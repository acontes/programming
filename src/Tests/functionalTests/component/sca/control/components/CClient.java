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
package functionalTests.component.sca.control.components;

import org.oasisopen.sca.annotation.Property;
import org.objectweb.fractal.api.control.BindingController;


public class CClient implements TestIntentItf, TestIntentItf2, ExecuteItf, BindingController {
    @Property
    public String PropertyClient;

    private TestIntentItf testIntentItf;

    private TestIntentItf2 testIntentItf2;

    public void m() throws Exception {
        testIntentItf.m();
    }

    public int n() {
        return testIntentItf.n();
    }

    public void m2() throws Exception {
        testIntentItf2.m2();
    }

    public int n2() {
        return testIntentItf2.n2();
    }

    public void execute() {
        n2();
    }

    public String[] listFc() {
        return new String[] { TestIntentItf.CLIENT_ITF_NAME, TestIntentItf2.CLIENT_ITF_NAME };
    }

    public Object lookupFc(String clientItfName) {
        if (clientItfName.equals(TestIntentItf.CLIENT_ITF_NAME)) {
            return testIntentItf;
        }
        if (clientItfName.equals(TestIntentItf2.CLIENT_ITF_NAME)) {
            return testIntentItf2;
        } else {
            return null;
        }
    }

    public void bindFc(String clientItfName, Object serverItf) {
        if (clientItfName.equals(TestIntentItf.CLIENT_ITF_NAME)) {
            testIntentItf = (TestIntentItf) serverItf;
        }
        if (clientItfName.equals(TestIntentItf2.CLIENT_ITF_NAME)) {
            testIntentItf2 = (TestIntentItf2) serverItf;
        }
    }

    public void unbindFc(String clientItfName) {
        if (clientItfName.equals(TestIntentItf.CLIENT_ITF_NAME)) {
            testIntentItf = null;
        }
        if (clientItfName.equals(TestIntentItf2.CLIENT_ITF_NAME)) {
            testIntentItf2 = null;
        }
    }
}
