/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 *              Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
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
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.component.sca.control.components;

import java.io.Serializable;

import org.osoa.sca.annotations.Property;


//@snippet-start component_scauserguide_1

public class PropertyComponent implements Serializable {
    @Property
    public boolean x1;
    @Property
    protected byte x2;
    @Property
    protected char x3;
    @Property
    protected short x4;
    @Property
    protected int x5;
    @Property
    protected long x6;
    @Property
    protected float x7;
    @Property
    protected double x8;
    @Property
    protected String x9;
    @Property
    protected String[] x10;
    @Property
    protected Object x11;

    public PropertyComponent() {
    }

    public String toString() {
        return "The properties inside this class : " + x1 + " x2 " + x2 + " x3 " + x3 + " x4 " + x4 +
            "\n x5 " + x5 + " x6 " + x6 + " x7 " + x7 + " x8 " + x8 + " x9 " + x9 + " x10 " + x10;
    }
}
//@snippet-end component_scauserguide_1