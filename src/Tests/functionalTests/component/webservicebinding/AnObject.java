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
package functionalTests.component.webservicebinding;

import java.io.Serializable;


/**
 * A Java object to test to call web services with objects as argument or as result.
 * <br>
 * To manipulate objects through web services, the only requirement is to have a no-argument
 * constructor and the appropriate getters and setters in order to allow the
 * serialization/deserialization of the object.
 * <br>
 * There is another one specific requirement to manipulate array of objects through web
 * services, at least with the Axis2 library, which is that the object must ONLY have a
 * no-argument constructor and no more.
 *
 * @author The ProActive Team
 *
 */
public class AnObject implements Serializable {
    private String id;
    private int intField;
    private double[] arrayField;
    private AnObject objectField;

    public AnObject() {
        id = "Anonymous";
        intField = 0;
        arrayField = new double[0];
        objectField = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIntField() {
        return intField;
    }

    public void setIntField(int intField) {
        this.intField = intField;
    }

    public double[] getArrayField() {
        return arrayField;
    }

    public void setArrayField(double[] arrayField) {
        this.arrayField = arrayField;
    }

    public AnObject getObjectField() {
        return objectField;
    }

    public void setObjectField(AnObject objectField) {
        this.objectField = objectField;
    }
}
